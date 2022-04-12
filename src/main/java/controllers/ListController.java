package controllers;

import gateway.PersonGateway;
import gateway.Session;
import javafx.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import model.FetchResults;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


//import java.awt.event.ActionEvent;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ListController implements Initializable, MyController{
    private static final Logger LOGGER = LogManager.getLogger();
    private ObservableList<Person> persons;
    private FetchResults fetchResults;

    @FXML
    private TextField summaryText;

    @FXML
    private TextField searchBar;

    private String lastName;

    @FXML
    private ListView<Person> personListView;


    private Session session;
    private PersonGateway gateway;

    public ListController(FetchResults fetchResults, String lastName){
        this.persons = FXCollections.observableArrayList(fetchResults.getPeople());
        //LOGGER.info(persons);
        this.fetchResults = fetchResults;
        this.lastName = lastName;
    }

    @FXML
    void search(){
        lastName = searchBar.getText();
        MainController.getInstance().switchView(ScreenType.PERSONLIST, 0, lastName);
    }

    @FXML
    void nextPage(){
        int offset = fetchResults.getOffset();
        if(offset >= fetchResults.getTotalElements()/10-1){
            offset = fetchResults.getTotalPages()-1;
        }
        else{
            offset += 1;
        }
        MainController.getInstance().switchView(ScreenType.PERSONLIST, offset, lastName);
    }

    @FXML
    void previousPage(){
        int offset = fetchResults.getOffset();
        if(offset < 1){
            offset = 0;
        }
        else{
            offset -= 1;
        }
        MainController.getInstance().switchView(ScreenType.PERSONLIST, offset, lastName);
    }

    @FXML
    void firstPage(){
        MainController.getInstance().switchView(ScreenType.PERSONLIST, 0, lastName);
    }

    @FXML
    void lastPage(){
        MainController.getInstance().switchView(ScreenType.PERSONLIST, fetchResults.getTotalPages()-1, lastName);
    }


    @FXML
    void deletePerson(ActionEvent event){
        Person person = personListView.getSelectionModel().getSelectedItem();
        if(person==null){
            LOGGER.error("No item selected, deletion unsuccessful");
            return;
        }
        LOGGER.info("Deleting "+person.getFirstName()+ " " + person.getLastName());
        gateway.deletePerson(person.getId());
        MainController.getInstance().setOBJ(null);
        MainController.getInstance().switchView(ScreenType.PERSONLIST);
    }

    @FXML
    void clickPerson(MouseEvent click){
        //on double click only
        //1. get model that is double clicked (if none, then create)
        //2. switch to the cat editing screen with the model that is selected
        Person person = personListView.getSelectionModel().getSelectedItem();
        if(click.getClickCount()==2){
            //if person selected, update
            if(person != null){
                //System.out.println(person);
                LOGGER.info("Reading " + person.toString());
                MainController.getInstance().switchView(ScreenType.PERSONDETAIL, person);
            }
            //person is null meaning a blank cell was clicked, so create

            else{
                System.out.println("NULL");
                MainController.getInstance().switchView(ScreenType.PERSONDETAIL, new Person());
            }


        }
    }

    @FXML
    void addPerson(ActionEvent event){
        LOGGER.info("Add person clicked");
        MainController.getInstance().switchView(ScreenType.PERSONDETAIL, new Person());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        //ObservableList<Person> tempList = FXCollections.observableArrayList(persons);
        gateway = MainController.getInstance().getPersonGateway();
        session = MainController.getInstance().getSession();
        summaryText.setText("Fetching records "+(fetchResults.getFirstIndex()+1)+" to "+fetchResults.getLastIndex()+" out of "+fetchResults.getTotalElements());


        LOGGER.info(persons);
        personListView.setItems(persons);

    }
}
