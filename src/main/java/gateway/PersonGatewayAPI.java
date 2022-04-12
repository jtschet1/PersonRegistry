package gateway;

import javafx.Alerts;
import model.AuditTrail;
import model.FetchResults;
import model.Person;
import myexceptions.UnauthorizedException;
import myexceptions.UnknownException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class PersonGatewayAPI implements PersonGateway{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String URL = "http://localhost:8080";
    private String token;
    public PersonGatewayAPI(){
    }
    //put the token here instead of in function to support interface
    public PersonGatewayAPI(String token){
        this.token=token;
    }



    public FetchResults fetchPersons(int pageNum, String lastName) throws UnauthorizedException, UnknownException {
        FetchResults results = new FetchResults();
        List<Person> persons = new ArrayList<>();
        LOGGER.info("Token = ",token);
        //lastName = "La";
        try{
            //[?pageNum=X&lastName=Y]
            System.out.println(token);
            String response = executeGetRequest(URL + "/people?pageNum="+pageNum+"&lastName="+lastName, token);
            System.out.println("Response: "+response);
            JSONObject personPage = new JSONObject(response);
            System.out.println("page: "+personPage);
            JSONArray personList = personPage.getJSONArray("content");
            System.out.println("Fetched:");
            //results.setPeople();
            for(Object person : personList){
                System.out.println(person.toString());
                persons.add(Person.fromJSONObject((JSONObject) person));
            }
            results.setPeople(persons);
            results.setTotalElements(personPage.getInt("totalElements"));
            results.setOffset(personPage.getJSONObject("pageable").getInt("pageNumber"));
            results.setTotalPages(personPage.getInt("totalPages"));
            results.setFirstIndex(personPage.getJSONObject("pageable").getInt("offset"));
            results.setLastIndex(personPage.getInt("numberOfElements")  + personPage.getJSONObject("pageable").getInt("offset"));
        }catch(RuntimeException e){
            throw new UnknownException(e);
        }
        return results;
    }

    public List<AuditTrail> fetchAudits(int id) throws UnauthorizedException, UnknownException{
        List<AuditTrail> audits = new ArrayList<>();
        LOGGER.info("Token = ", token);
        try{
            String response = executeGetRequest(URL + "/people/" + id + "/audittrail", token);
            JSONArray auditsList = new JSONArray(response);
            for(Object a : auditsList){
                audits.add(AuditTrail.fromJSONObject((JSONObject) a));
            }
        }catch(RuntimeException e){
            throw new UnknownException(e);
        }
        return audits;
    }



    public void deletePerson(int id) throws UnauthorizedException, UnknownException {
        String response = executeDeleteRequest(URL + "/people/"+id, token);
    }
    public JSONObject addPerson(String firstName, String lastName, String DOB) throws UnauthorizedException, UnknownException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        token = getToken();
        Period period = Period.between(LocalDate.parse(DOB), LocalDate.now());
        int age = period.getYears();
        //String strResponse = executePostRequest(URL + "/people", token);
        try{
            httpclient = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost("http://localhost:8080/people");

            JSONObject addition = new JSONObject();
            // Check object data from passed in object, compare to body equals to in json

            addition.put("firstName", firstName);
            addition.put("lastName", lastName);
            addition.put("dob", DOB);
            //addition.put("lastModified", lastModified);

            LOGGER.info(("Token is: "+token));
            if(token != null && token.length() > 0)
                //i am sesh token = invalid
                //i am a Session token
                postRequest.setHeader("Authorization", token);
            String personString = addition.toString();
            //System.out.println("PERSON STRING " + personString);
            StringEntity addEntity = new StringEntity(personString);
            //System.out.println("ADD ENTITY " + addEntity);
            postRequest.setEntity(addEntity);
            postRequest.setHeader("Accept","application/json");
            postRequest.setHeader("Content-Type","application/json");
            //System.out.println("POSTY" + postRequest);
            response = httpclient.execute(postRequest);
            //System.out.println("RESPONSE"+response+ " " + response.getStatusLine());

            switch(response.getStatusLine().getStatusCode()) {
                case 200:
                    HttpEntity ent= response.getEntity();
                    String sResponse = EntityUtils.toString(ent, StandardCharsets.UTF_8);
                    EntityUtils.consume(ent);

                    //Add id and age if response is true, to the json object
                    addition.put("id", sResponse.replaceAll("[^0-9]", ""));
                    addition.put("age", age);
                    return addition;
                case 401:
                    throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
                default:
                    System.out.println("STATUS: "+response.getStatusLine().getStatusCode());
                    throw new UnknownException(response.getStatusLine().getReasonPhrase());
            }
        }catch (IOException e) {
            e.printStackTrace();
            throw new UnknownException(e);

        } finally {
            try {
                if(response != null) {
                    response.close();
                }
                if(httpclient != null) {
                    httpclient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new UnauthorizedException(e);
            }
        }
    }
    public void updatePerson(String firstName, String lastName, String dob, int id, Instant lastModified) throws UnauthorizedException, UnknownException{
        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        token = getToken();
        try{
            client = HttpClients.createDefault();
            HttpPut putRequest = new HttpPut("http://localhost:8080/people/"+id);


            //LOGGER.info("Update is: "+update);
            LOGGER.info("Token is: "+token);
            JSONObject updated = new JSONObject();
            updated.put("firstName", firstName);
            updated.put("lastName", lastName);
            updated.put("dob", dob);

            if(token != null && token.length() > 0){
                putRequest.setHeader("Authorization", token);
            }
            String updatedString = updated.toString();
            StringEntity upEntity = new StringEntity(updatedString);
            putRequest.setEntity(upEntity);
            putRequest.setHeader("Accept","application/json");
            putRequest.setHeader("Content-Type","application/json");
            putRequest.setHeader("lastModified", lastModified.toString());
            response = client.execute(putRequest);
            LOGGER.info("UPDATE RESPONSE: "+response.getStatusLine().getStatusCode());
            if(response.getStatusLine().getStatusCode()==412){
                Alerts.errorAlert("Record Locked", "The record you are trying to update has been modified since you began updating. Please try again.");

            }
        }catch(IOException e){
            e.printStackTrace();
            throw new UnknownException(e);
        }finally{
            try{
                if(response != null){
                    response.close();
                }
                if(client != null){
                    client.close();
                }
            }catch(IOException e){
                e.printStackTrace();
                throw new UnauthorizedException(e);
            }
        }
    }
    private String executeDeleteRequest(String url, String token) throws UnauthorizedException, UnknownException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            client = HttpClients.createDefault();
            HttpDelete delete = new HttpDelete(url);

            if (token != null && token.length() > 0){
                delete.setHeader("Authorization", token);
            }

            response = client.execute(delete);

            switch(response.getStatusLine().getStatusCode()){
                case 200:
                    return getStringFromResponse(response);
                case 401:
                    throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
                default:
                    throw new UnknownException(response.getStatusLine().getReasonPhrase());
            }
        }catch(IOException e){
            e.printStackTrace();
            throw new UnauthorizedException(e);
        }finally{
            try{
                if(response != null){
                    response.close();
                }
                if(client != null){
                    client.close();
                }
            }catch(IOException e){
                e.printStackTrace();
                throw new UnauthorizedException(e);
            }
        }
    }
    private String executeGetRequest(String url, String token) throws UnauthorizedException, UnknownException{
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try{
            httpClient = HttpClients.createDefault();
            HttpGet getRequest = new HttpGet(url);

            if(token != null && token.length() > 0){
                getRequest.setHeader("Authorization", token);
            }
            response = httpClient.execute(getRequest);

            switch(response.getStatusLine().getStatusCode()){
                case 200:
                    return getStringFromResponse(response);
                case 401:
                    throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
                default:
                    throw new UnknownException(response.getStatusLine().getReasonPhrase());
            }
        }catch(IOException e){
            e.printStackTrace();
            throw new UnauthorizedException(e);
        }finally{
            try{
                if(response != null){
                    response.close();
                }
                if(httpClient!=null){
                    httpClient.close();
                }
            }catch(IOException e){
                e.printStackTrace();
                throw new UnauthorizedException(e);
            }
        }
    }
    private String executePutRequest(String url, String token) throws UnauthorizedException, UnknownException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = HttpClients.createDefault();
            HttpPut putRequest = new HttpPut(url);

            if(token != null && token.length() > 0)
                putRequest.setHeader("Authorization", token);

            response = httpclient.execute(putRequest);

            switch(response.getStatusLine().getStatusCode()) {
                case 200:
                    return getStringFromResponse(response);
                case 401:
                    throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
                default:
                    throw new UnknownException(response.getStatusLine().getReasonPhrase());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);

        } finally {
            try {
                if(response != null) {
                    response.close();
                }
                if(httpclient != null) {
                    httpclient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new UnauthorizedException(e);
            }
        }
    }
    private String executePostRequest(String url, String token) throws UnauthorizedException, UnknownException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost(url);

            if(token != null && token.length() > 0)
                postRequest.setHeader("Authorization", token);

            response = httpclient.execute(postRequest);

            switch(response.getStatusLine().getStatusCode()) {
                case 200:
                    return getStringFromResponse(response);
                case 401:
                    throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
                default:
                    throw new UnknownException(response.getStatusLine().getReasonPhrase());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);

        } finally {
            try {
                if(response != null) {
                    response.close();
                }
                if(httpclient != null) {
                    httpclient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new UnauthorizedException(e);
            }
        }
    }
    private String getStringFromResponse(CloseableHttpResponse response) throws IOException{
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        EntityUtils.consume(entity);
        return strResponse;
    }
    public String getToken(){return token;}
    public void setToken(String token){this.token = token;}
}
