package model;

import myexceptions.UnknownException;
import org.json.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class AuditTrail {
    String changeMsg;
    int changedBy;
    int personId;
    Instant whenOccured;
    public AuditTrail(){

    }
    public AuditTrail(String changeMsg, int changedBy, int personId, String whenOccured){
        this.changedBy = changedBy;
        this.changeMsg = changeMsg;
        this.personId = personId;
        this.whenOccured = Instant.parse(whenOccured);
    }

    public static AuditTrail fromJSONObject(JSONObject json){
        try{
            AuditTrail audit = new AuditTrail(json.getString("changeMsg"), json.getInt("changedBy"), json.getInt("personId"), json.getString("whenOccured"));
            return audit;
        }catch(Exception e){
            throw new IllegalArgumentException("Unable to parse person from provided json: " + json.toString());
        }
    }

    public String getChangeMsg() {
        return changeMsg;
    }

    public void setChangeMsg(String changeMsg) {
        this.changeMsg = changeMsg;
    }

    public int getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(int changedBy) {
        this.changedBy = changedBy;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public Instant getWhenOccured() {
        return whenOccured;
    }

    public void setWhenOccured(Instant whenOccured) {
        this.whenOccured = whenOccured;
    }
}
