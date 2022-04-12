package gateway;

import model.AuditTrail;
import model.FetchResults;
import model.Person;
import myexceptions.UnauthorizedException;
import myexceptions.UnknownException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.List;

//interface that specifies methods that any type of personGateway has to have.
//There are multiple personGateways, so having them all implement this Interface helps
//make things easier
public interface PersonGateway {
    public FetchResults fetchPersons(int page, String lastName) throws UnauthorizedException, UnknownException;
    public List<AuditTrail> fetchAudits(int id) throws UnauthorizedException, UnknownException;
    public abstract JSONObject addPerson(String firstName, String lastName, String dateOfBirth) throws UnauthorizedException, UnknownException;
    public abstract void updatePerson(String firstName, String lastName, String dob, int id, Instant lastModified) throws UnauthorizedException, UnknownException;
    public abstract void deletePerson(int id) throws UnauthorizedException, UnknownException;




    //JSONObject addPerson(String text, String text1, String text2);

    //void setToken(String sessionId);
}
