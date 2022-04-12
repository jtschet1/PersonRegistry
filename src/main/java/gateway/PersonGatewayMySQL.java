package gateway;

import jdbc.JDBCConnect;
import model.AuditTrail;
import model.FetchResults;
import model.Person;
import myexceptions.DBException;
import myexceptions.UnauthorizedException;
import myexceptions.UnknownException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class PersonGatewayMySQL implements PersonGateway{
    public static final Logger LOGGER = LogManager.getLogger();
    private Connection connection;
    public PersonGatewayMySQL(){
        try {
            connection = JDBCConnect.connectToDB();
        } catch (SQLException | IOException e) {
            throw new DBException(e);
        }
    }


    public FetchResults fetchPersons(int page, String lastName) throws UnauthorizedException, UnknownException {
        System.out.println(page + lastName);
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Person> personList = new ArrayList<>();
        try{
            st = connection.prepareStatement("select * from persons");
            rs = st.executeQuery();
            while(rs.next()){
                LOGGER.info("fetched person " + rs.getString("firstName")+ " " + rs.getString("lastName"));
                personList.add(Person.fromResultSet(rs));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            try{
                rs.close();
                st.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return new FetchResults();
    }


    @Override
    public JSONObject addPerson(String firstName, String lastName, String dateOfBirth) throws UnauthorizedException, UnknownException {
        return null;
    }

    @Override
    public void updatePerson(String firstName, String lastName, String dob, int id, Instant lastModified) throws UnauthorizedException, UnknownException {

    }

    @Override
    public void deletePerson(int id) throws UnauthorizedException, UnknownException {

    }

    @Override
    public List<AuditTrail> fetchAudits(int id) throws UnauthorizedException, UnknownException{
        return null;
    }

    public void close(){
        try{
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
