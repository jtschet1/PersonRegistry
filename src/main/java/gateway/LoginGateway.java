package gateway;

import myexceptions.UnauthorizedException;
import myexceptions.UnknownException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Recives credentials from login controller, and creates Session object
public class LoginGateway {
    //Literally logs events
    private static final Logger LOGGER = LogManager.getLogger();
    //method to create Session based on user credentials
    public static Session login(String userName, String password) throws UnauthorizedException{
        //*********************************************************
        // what actually are these things?
        //Literally a response code like from Comp Net?
        //*********************************************************
        CloseableHttpClient httpCLient = null;
        CloseableHttpResponse response = null;

        try{
            //*********************************************************
            //What does this do?
            //*********************************************************
            httpCLient = HttpClients.createDefault();

            //Going to  Url "login"?
            HttpPost postRequest = new HttpPost("http://localhost:8080/login");

            //use this for submitting form data as raw json
            //*********************************************************
            //From Oracle website:
            //A JSONObject is an unordered collection of name/value pairs. String wrapped in curly braces
            //with colons between names and correlating values, and commas between the pairs.
            //Get() and opt() access values
            //put() adds and replaces values
            JSONObject formData = new JSONObject();
            //create key:values pairs.
            //key="username" value=userName
            //key="password" value=password
            formData.put("login", userName);
            formData.put("password", password);
            //System.out.println("Form data:\n"+formData);
            //tidies it all up in a nice little string
            String formDataString = formData.toString();

            StringEntity reqEntity = new StringEntity(formDataString);

            postRequest.setEntity(reqEntity);
            postRequest.setHeader("Accept","application/json");
            postRequest.setHeader("Content-Type","application/json");


            response = httpCLient.execute(postRequest);
            switch(response.getStatusLine().getStatusCode()){
                case 200:
                    HttpEntity entity = response.getEntity();
                    // use org.apache.http.util.EntityUtils to read json as string

                    String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    //consume ensures that the entity content is fully consumed and the content stream, if exists, is closed
                    EntityUtils.consume(entity);

                    //cannot find way to get the token from entity, so i get the whole response body,
                    //make a new json obj from that, then  get the token from the new obj and save it
                    LOGGER.info("strResponse = "+strResponse);
                    JSONObject j = new JSONObject(strResponse);
                    strResponse = (String) j.get("token");


                    // strResponse must be a Session Id
                    LOGGER.info("strResponse = "+strResponse);



                    Session session = new Session(strResponse);
                    System.out.println("ENTITIT  - " + entity.getContent());
                    return session;
                case 401:
                    throw new UnauthorizedException("Login failed");
                default:
                    throw new UnknownException(response.getStatusLine().getReasonPhrase());
            }


            //*********************************************************
            //this is hideous
            //*********************************************************
        }catch(IOException e){
            e.printStackTrace();
            throw new UnauthorizedException(e);
        }finally{
            try{
                if(response != null){
                    response.close();
                }
                if(httpCLient != null){
                    httpCLient.close();
                }
            }catch(IOException e){
                e.printStackTrace();
                throw new UnauthorizedException(e);
            }
        }

    }
}
