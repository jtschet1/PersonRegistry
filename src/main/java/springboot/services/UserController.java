package springboot.services;

import org.hibernate.mapping.Collection;
import org.json.JSONObject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw_hash.HashUtils;
import springboot.model.Session;
import springboot.model.User;
import springboot.repository.SessionRepository;
import springboot.repository.UserRepository;

//import java.time.LocalDate;
//import java.util.Date;
import java.util.List;

@RestController
public class UserController {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    /*
    String login = this.loginField.getText();
    String hashedPassword = HashUtils.getCryptoHash(this.passwordField.getText(), "SHA-256");
     */

    public UserController(UserRepository userRepository, SessionRepository sessionRepository){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    //ask about the routes
    //generally accepted protocol: /users
    @GetMapping("/users")
    @ResponseBody
    public List<User> fetchUsers(){
        return userRepository.findAll();
    }

    @PostMapping("/users")
    @ResponseBody
    //spring receives json and creates user from it magically
    public String addUser(@RequestBody User user){
        user = userRepository.save(user);
        return "ok";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody User user){
        //if a record has the same login / pass, create session
        //if no record has the same login / pass, invalid
        String hashedPassword = HashUtils.getCryptoHash(user.getPassword(), "SHA-256");
        //System.out.println(hashedPassword);
        List<User> u = userRepository.findAllByLoginAndPassword(user.getLogin(), hashedPassword);
        if(u.size()==0){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }
        String token = HashUtils.getCryptoHash(user.getId() + "" + user.getPassword() + "" + System.currentTimeMillis(), "SHA-256");
        Session session = new Session();
        session.setId(token);
        session.setUser(u.get(0));
        //we successfully find user with specified login and password, and create a session object with the id and token
        //just need to create a session record
        sessionRepository.save(session);

        JSONObject j = new JSONObject();
        j.put("token",session.getId());
        return new ResponseEntity<>(j.toString(), HttpStatus.valueOf(200));

    }

    

    // getters and setters

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
