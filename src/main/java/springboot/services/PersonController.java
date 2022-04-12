package springboot.services;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springboot.model.*;
import springboot.repository.*;


import javax.management.Query;
import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLOutput;
import java.time.Instant;
//import java.util.List;
import java.util.List;
import java.util.Optional;
import java.util.Random;

//inner join = get the overlap of two tables
//said it took a few minutes for the crazier queries
//ask about nosql, how could a database be non-relational?
//left join = get all rows from left table and its overlap in the right table
//exclusive left join = get all rows from left table that DON'T have overlap with right table (left join _ where _ is null)
//pseudo outer full join = left join + exclusive right join.
//indexing = creates b tree for a field and travels down b tree to find a match

@RestController
public class PersonController {



    private static final Logger LOGGER = LogManager.getLogger();



    private PersonRepository personRepository;
    private SessionRepository sessionRepository;
    private AuditTrailRepository auditTrailRepository;



    public PersonController(PersonRepository personRepository,SessionRepository sessionRepository, AuditTrailRepository auditTrailRepository){
        this.personRepository = personRepository;
        this.sessionRepository = sessionRepository;
        this.auditTrailRepository = auditTrailRepository;

    }

    @GetMapping("/people/{id}/audittrail")
    @ResponseBody
    public ResponseEntity<List<AuditTrail>> fetchAuditTrail(@RequestHeader HttpHeaders headers, @PathVariable int id){
        if(!headers.containsKey("authorization")){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }
        String token = headers.get("authorization").get(0);
        if (!sessionRepository.findById(token).isPresent()){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }
        Optional<Person> person = personRepository.findById(id);
        if(!person.isPresent()){
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));
        }
        return new ResponseEntity<>(auditTrailRepository.findAllByPersonId(person.get().getId()), HttpStatus.valueOf(200));

    }


    // add this when offset works:
    // , @RequestParam(value = "lastName", required = false) String lastName
    @GetMapping("/people")
    @ResponseBody
    public ResponseEntity<Page<Person>> fetchPeople(@RequestHeader HttpHeaders headers, @RequestParam(value = "pageNum", required = false) int pageNum, @RequestParam(value = "lastName", required = false) String lastName){
        //200 + json array of people as JSON objects
        //401, no body if session token is missing or invalid
        System.out.println(headers.toString());
        if(!headers.containsKey("authorization")){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }
        String token = headers.get("authorization").get(0);
        if (!sessionRepository.findById(token).isPresent()){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }
        Pageable firstX = PageRequest.of(pageNum,10);
        System.out.println("Page num = "+pageNum + "Last name = "+lastName);
        //System.out.println("Last name = "+lastName);

        LOGGER.info("Fetched all people\n");
        //return new ResponseEntity<>(personRepository.findAll(firstX), HttpStatus.valueOf(200));
        return new ResponseEntity<>(personRepository.findAllByLastNameStartingWith(lastName,firstX), HttpStatus.valueOf(200));
    }

    @PostMapping("/people")
    @ResponseBody
    public ResponseEntity<String> addPerson(@RequestHeader HttpHeaders headers, @RequestBody Person person){
        //200 + no body if successful
        //400 + JSON array of error message(s) if request body incorrect
        //401, no body if session token is missing or invalid
        System.out.println("ADD MAPPING");


        if(!headers.containsKey("authorization")){
            System.out.println("No sesh token");
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }
        String token = headers.get("authorization").get(0);
        System.out.println("Sesh = "+token);
        if (!sessionRepository.findById(token).isPresent()){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        try {
            person = personRepository.save(person);
        }catch(Exception e){
            JSONObject j = new JSONObject();
            j.put("error",e.getMessage());
            System.out.println(e.getMessage());
            return new ResponseEntity<>(j.toString(), HttpStatus.valueOf(400));
        }
        LOGGER.info("Inserting person: " + person.getFirstName() + person.getLastName());
        User user = sessionRepository.findById(token).get().getUser();
        String audit = "Id: AI. change_msg: added. changed_by: "+user.getId() +". person_id: "+person.getId();
        LOGGER.info(audit);
        AuditTrail auditTrail = new AuditTrail("added", user.getId(), person.getId());
        try {
            auditTrailRepository.save(auditTrail);
        }catch(Exception e){
            JSONObject j = new JSONObject();
            j.put("error",e.getMessage());
            System.out.println(e.getMessage());
            return new ResponseEntity<>(j.toString(), HttpStatus.valueOf(400));
        }
        return new ResponseEntity<>(null, HttpStatus.valueOf(200));
    }

    @PutMapping("/people/{id}")
    @ResponseBody
    public ResponseEntity<String> updatePerson(@RequestHeader HttpHeaders headers, @PathVariable long id, @RequestBody Person p){
        //200, no body if good
        //400 + JSON array of error message(s) if request body incorrect, don't worry about partial updates?
        //401, no body if session token is missing or invalid
        //404, no body if :id is not in database

        if(!headers.containsKey("authorization")){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }
        String token = headers.get("authorization").get(0);
        if (!sessionRepository.findById(token).isPresent()){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        Optional<Person> person = personRepository.findById((int)id);
        if(!person.isPresent()){
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));
        }

        //if the person's current lastModified value is not equal to the last modified
        //passed by the client, that means the client is working on an out of date version of
        //this record, and we must not allow the update.
        if(!headers.containsKey("lastModified") || !person.get().getLastModified().toString().equals(headers.get("lastModified").get(0))){
            LOGGER.info("Failed Optimistic lock\n"+"Current value: "+person.get().getLastModified().toString()
            +"\nYour value: "+ headers.get("lastModified").get(0));
            return new ResponseEntity<>(null, HttpStatus.valueOf(412));
        }

        String oldLast = person.get().getLastName();
        String oldFirst = person.get().getFirstName();
        String oldDOB = person.get().getDob();

        p.setId((int)id);
        try {
            personRepository.save(p);
        }catch(Exception e){
            JSONObject j = new JSONObject();
            j.put("error",e.getMessage());
            return new ResponseEntity<>(j.toString(), HttpStatus.valueOf(400));
        }

        String changed_msg = "";
        if(!p.getLastName().equals(oldLast)){
            LOGGER.info("Last name");
            changed_msg = changed_msg.concat("last_name changed from "+oldLast+" to "+p.getLastName()+". ");
        }
        if(!p.getFirstName().equals(oldFirst)){
            LOGGER.info("First name");
             changed_msg = changed_msg.concat("first_name changed from "+oldFirst+" to "+p.getFirstName()+". ");
        }
        if(!p.getDob().equals(oldDOB)){
            LOGGER.info("dob");
             changed_msg = changed_msg.concat("dob changed from "+oldDOB+" to "+p.getDob()+". ");
        }
        if (changed_msg == null){
            return new ResponseEntity<>(null, HttpStatus.valueOf(200));
        }

        LOGGER.info("Changed msg: "+changed_msg);
        User user = sessionRepository.findById(token).get().getUser();
        AuditTrail auditTrail = new AuditTrail(changed_msg, user.getId(), p.getId(), Instant.now());
        try {
            auditTrailRepository.save(auditTrail);
        }catch(Exception e){
            JSONObject j = new JSONObject();
            j.put("error",e.getMessage());
            return new ResponseEntity<>(j.toString(), HttpStatus.valueOf(400));
        }
        return new ResponseEntity<>(null, HttpStatus.valueOf(200));
    }

    @DeleteMapping("/people/{id}")
    @ResponseBody
    public ResponseEntity<String> deletePerson(@RequestHeader HttpHeaders headers,@PathVariable long id){
        //200 + no body if successful
        //401, no body if session token is missing or invalid
        //404, no body if id is not in database

        System.out.println("DELETE MAPPING");

        if(!headers.containsKey("authorization")){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }
        String token = headers.get("authorization").get(0);
        if (!sessionRepository.findById(token).isPresent()){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        LOGGER.info("Route id:"+id);
        Optional<Person> person = personRepository.findById((int)id);
        if(!person.isPresent()){
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));
        }
        personRepository.deleteById((int) id);
        return new ResponseEntity<>(null, HttpStatus.valueOf(200));
    }

    @GetMapping("/people/{id}")
    @ResponseBody
    public ResponseEntity<Person> fetchPersonById(@RequestHeader HttpHeaders headers, @PathVariable int id){
        //200 + json object of the person's fields
        //401, no body if session token is missing or invalid
        //404, no body if :id is not in database

        if(!headers.containsKey("authorization")){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }
        String token = headers.get("authorization").get(0);
        if (!sessionRepository.findById(token).isPresent()){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        Optional<Person> person = personRepository.findById((int)id);
        if(!person.isPresent()){
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));
        }
        return new ResponseEntity<>(person.get(), HttpStatus.valueOf(200));
    }

}