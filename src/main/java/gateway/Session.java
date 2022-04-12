package gateway;

public class Session {
    // unique Session Id?
    private String sessionId;

    private String userFullName;

    public Session(){

    }
    public Session(String sessionId){
        this.sessionId = sessionId;
        this.userFullName = "";
    }
    public Session(String sessionId, String userFullName){
        this.sessionId = sessionId;
        this.userFullName = userFullName;
    }

    //String version of all Session details
    @Override
    public String toString(){
        return "Session{" +
                "sessionId='"+sessionId+'\''+
                ", userFullName='"+userFullName+'\''+
                '}';
    }
    public String getSessionId(){
        return sessionId;
    }
    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }
    public String getUserFullName(){
        return userFullName;
    }
    public void setUserFullName(String userFullName){
        this.userFullName = userFullName;
    }
}
