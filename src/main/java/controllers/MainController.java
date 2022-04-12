package controllers;

import gateway.PersonGateway;
import gateway.PersonGatewayAPI;
import gateway.PersonGatewayMySQL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import model.AuditTrail;
import model.FetchResults;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private JSONObject person;

   // URL url;

    //private FetchResults fetchResults;

    //Logger, literally keeps a log of everything that happens
    private static final Logger LOGGER = LogManager.getLogger();

    //an instance of MainController. A specific variation of the
    //MainController object.
    private static MainController instance = null;

    //An instance of Session. Contains a sessionID and a username
    private gateway.Session session;

    private PersonGateway personGateway;

    //a Borderpane, declared for the sake of the switchview, depending
    //on which screen the user needs to interact with
    @FXML
    private BorderPane rootPane;

    private MainController(){
        personGateway = new PersonGatewayAPI(session.getSessionId());
    }
    private MainController(PersonGateway gateway){
        this.personGateway = gateway;
    }
    public void setOBJ(JSONObject person){
        this.person = person;
    }


    public void switchView(ScreenType screenType, Object... args){
        // Straightforward. The fileName of the view requested by the user
        String viewFileName = "";

        MyController controller = null;

        LOGGER.info(session);

        switch(screenType) {
            //The user is switching to the person list view, where all persons and their
            // information are available
            case PERSONLIST:
                LOGGER.info("Case: List");

                //get personlist view
                viewFileName = "/views/mvc/person_list.fxml";
                int page = 0;
                String lastName = "";
                if(args.length > 0) {
                    page = (int) args[0];
                }
                if(args.length > 1) {
                    lastName = (String) args[1];
                    if(lastName==null){
                        lastName = "";
                    }
                }
                System.out.println("LAST NAME = "+lastName);
                ArrayList<Person> persons= null;
                FetchResults results;
                results = personGateway.fetchPersons(page, lastName);
                LOGGER.info("\n\n" + results.getPeople());

                //Need a list controller because we're going into the list view
                //list view needs the list of persons "persons"
                controller = new ListController(results, lastName);
                break;
            case PERSONDETAIL:
                LOGGER.info("Case: Detail");
                //get detail view
                viewFileName = "/views/mvc/person_detail.fxml";



                //If argument other than person passed, can't open detail on that argument
                if (!(args[0] instanceof Person)) {
                    throw new IllegalArgumentException("Argument passed is not a Person object. Arg[0]:\n" + args[0].toString());
                }



                ArrayList<AuditTrail> auditTrails;
                int numbah =  ((Person) args[0]).getId();
                System.out.println();
                if(numbah == -1){
                    LOGGER.info("NEW PERSON");
                    controller = new DetailController((Person)args[0]);
                    break;
                }
                System.out.println("NUMBAH " + numbah);
                auditTrails = (ArrayList<AuditTrail>) personGateway.fetchAudits(numbah);
                //type cast the argument to person and use that as the basis for the Detail Controller.
                controller = new DetailController((Person) args[0], auditTrails);
                break;
            case LOGIN:
                LOGGER.info("Case: Login");
                //get login view
                viewFileName = "/views/mvc/login.fxml";
                controller = new LoginController();
                break;
            default:
                LOGGER.error("Unknown screen type: " + screenType);
                return;
        }
        //Load fxml file specified in viewFilename
        System.out.println(viewFileName);
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(viewFileName));

        //Controller will corrspond to the view the user requested, see switch case for proof
        loader.setController(controller);

        //getting rootNode ready
        Parent rootNode = null;

        //Try to set root node to view set in loader a few lines earlier.
        //If fails, print IOException
        try{
            rootNode = loader.load();
            rootPane.setCenter(rootNode);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        switchView(ScreenType.LOGIN);
    }

    public static MainController getInstance() {
        if(instance==null)
            //instance = new MainController(new PersonGatewayAPI("i am a Session token"));
            instance = new MainController(new PersonGatewayMySQL());
        return instance;
    }

    public gateway.Session getSession(){
        return session;
    }


    //has direct reference to gateway, so main controller should set Session token
    //MySQL gateway doesn't have or need a Session token
    public void setSession(gateway.Session session) {
        this.session = session;
        if(personGateway instanceof PersonGatewayAPI){
            ((PersonGatewayAPI) personGateway).setToken(session.getSessionId());
        }
    }
    //return type is now abstract
    //no need to determine concretely if API or MySQL gateway
    public PersonGateway getPersonGateway(){
        return personGateway;
    }

    public void setPersonGateway(PersonGateway personGateway){
        this.personGateway = personGateway;
    }


}
