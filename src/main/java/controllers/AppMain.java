package controllers;

import com.sun.tools.javac.Main;
import controllers.MainController;
import gateway.PersonGatewayAPI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
//test comment
public class AppMain extends Application {
    private static final Logger LOGGER = LogManager.getLogger();

    private ArrayList<Person> persons;

    public static void main (String[] args){
        LOGGER.info("Before Launch");
        launch(args);
        LOGGER.info("After Launch");
    }


    @Override
    public void stop() throws Exception{
        super.stop();
    }

    @Override
    public void init() throws Exception{
        super.init();
       // LOGGER.info("In init");
       // persons = new ArrayList<>();
    }

   //@Override
    public void start(Stage stage) throws Exception{
        LOGGER.info("Before start");
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/mvc/main_view.fxml"));
        loader.setController(MainController.getInstance());
        //Inject the concrete gateway dependency
        MainController.getInstance().setPersonGateway(new PersonGatewayAPI());
        //MainController.getInstance().setPersonGateway(new PersonGatewayMySQL());

        Parent rootNode = loader.load();
        stage.setScene(new Scene(rootNode));

        stage.setTitle("Johann");
        stage.show();
        LOGGER.info("After start");
    }
}
