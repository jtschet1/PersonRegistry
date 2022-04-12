package model;

import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

public class Person {
    //information associated with person objects
    private String firstName;
    private String lastName;
    private int id;
    private String DOB;
    private int age;
    private Instant lastModiifed;

    public Person(String firstName, String lastName, String dob, int id, String lastModiifed) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.DOB = dob;
        this.lastModiifed = Instant.parse(lastModiifed);
    }

    //creation of "blank" person object
    public Person() {
        firstName = "First";
        lastName = "Last";
        id = -1;
        this.setDOB(LocalDate.now().toString());
    }
    public Person(String firstName, String lastName, String dob, int id) {
        //call validators. if not valid, throw exception
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = dob;
    }
    public Person(String firstName, String lastName, String dateOfBirth, int id, int age) {
        //call validators. if not valid, throw exception
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = dateOfBirth;
        this.age = age;
    }



    public static Person fromJSONObject(JSONObject json){
        try{
            Person person = new Person(json.getString("firstName"), json.getString("lastName"), json.getString("dob"), json.getInt("id"), json.getString("lastModified"));
            return person;
        }catch(Exception e){
            throw new IllegalArgumentException("Unable to parse person from provided json: " + json.toString());
        }
    }



    public static Person fromResultSet(ResultSet rs) throws SQLException {
            Person person = new Person();
            person.setId(rs.getInt("id"));
            person.setFullName(rs.getString("firstName"), rs.getString("lastName"));
            person.setDOB(rs.getString("DOB"));
            return person;

    }

    //validators
    public static boolean isValidPersonName(String firstName, String lastName){
        if(firstName == null || lastName == null){
            return false;
            //throw exception describing what's wrong
        }
        if(firstName.length()<2 || firstName.length()>100){
            return false;
            //throw exception describing what's wrong
        }
        if(lastName.length()<2 || lastName.length()>100){
            return false;
            //throw exception describing what's wrong
        }
        return true;
    }

    //accessors
    private void setFullName(String firstName, String lastName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public String toString(){
        return id + " " + firstName + " " + lastName + " " + DOB + " " + lastModiifed;
    }
    private void setId(int id){
        this.id = id;
    }
    private void setDOB(String dob) {
        this.DOB = dob;
        Period period = Period.between(LocalDate.parse(DOB), LocalDate.now());
        this.age = period.getYears();

    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public int getId() {
        return id;
    }
    public String getDOB() {
        return DOB;
    }

    public Instant getLastModiifed() {
        return lastModiifed;
    }

    public void setLastModiifed(Instant lastModiifed) {
        this.lastModiifed = lastModiifed;
    }
}
