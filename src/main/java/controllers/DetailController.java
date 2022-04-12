package controllers;

import gateway.PersonGateway;
import gateway.Session;
import javafx.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AuditTrail;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.ResourceBundle;

//Controller for the personDetail view
public class DetailController implements Initializable, MyController {
    private static final Logger LOGGER = LogManager.getLogger();

    //Represents the 4 textfields that make up the Detail View.
    //These fields are filled in with values by the user which are used to
    //update or create a person
    @FXML
    private TextField personFirstname;

    @FXML
    private TextField personLastname;

    @FXML
    private TextField personDOB;

    @FXML
    private TextField personId;

    @FXML
    private TableView table;


    @FXML
    private TableColumn col_description;

    @FXML
    private TableColumn col_by;

    @FXML
    private TableColumn col_for;

    @FXML
    private TableColumn col_when;





    private ObservableList<AuditTrail> auditTrails;

    //This is the potential or current person that is being created or updated by the user
    private Person person;
    private Session session;
    private PersonGateway gateway;

    public DetailController(Person person){
        this.person = person;
    }

    public DetailController(Person person, ArrayList<AuditTrail> auditTrails){
        //assign the person instance variable with the potential or current person passed in by the user
        this.person = person;
        this.auditTrails = FXCollections.observableArrayList(auditTrails);
    }

    @FXML
    void cancelSave(ActionEvent event){
        MainController.getInstance().switchView(ScreenType.PERSONLIST);
    }

    @FXML
    void save(ActionEvent event){
        if(personFirstname.getText().length()==0 || personLastname.getText().length()==0 || personId.getText().length()==0 || personDOB.getText().length()==0){
            Alerts.errorAlert("Invalid submission", "Must fill out all fields");
            return;
        }
        //If id is -1, creating person
        if(person.getId()==-1){
            LOGGER.info("Creating person: " + personFirstname.getText() + " " + personLastname.getText());
            JSONObject newPerson = gateway.addPerson(personFirstname.getText(), personLastname.getText(), personDOB.getText());
            MainController.getInstance().setOBJ(newPerson);
            //MainController.getInstance().switchView(ScreenType.PERSONLIST);
        }
        //If Id is not -1, updating person
        else{
            LOGGER.info("Updating person: " + person.toString());
            System.out.println("LAST MODDED " + person.getLastModiifed());
            gateway.updatePerson(personFirstname.getText(), personLastname.getText(), personDOB.getText(), Integer.parseInt(personId.getText()), person.getLastModiifed());
           // MainController.getInstance().switchView(ScreenType.PERSONLIST);
        }
        MainController.getInstance().switchView(ScreenType.PERSONLIST);
    }

    //@Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        gateway = MainController.getInstance().getPersonGateway();
        session = MainController.getInstance().getSession();

        LOGGER.info(person.getFirstName());

        personFirstname.setText(person.getFirstName());
        personLastname.setText(person.getLastName());
        personId.setText(""+person.getId());
        personDOB.setText(person.getDOB());
        if(person.getId() != -1) {
            col_description.setCellValueFactory(new PropertyValueFactory<AuditTrail, String>("changeMsg"));
            col_by.setCellValueFactory(new PropertyValueFactory<AuditTrail, Integer>("changedBy"));
            col_for.setCellValueFactory(new PropertyValueFactory<AuditTrail, Integer>("personId"));
            col_when.setCellValueFactory(new PropertyValueFactory<AuditTrail, Instant>("whenOccured"));
            table.setItems(auditTrails);
        }

    }
}
