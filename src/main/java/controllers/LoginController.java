package controllers;

import gateway.LoginGateway;
import gateway.Session;
import javafx.Alerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import myexceptions.UnauthorizedException;
import myexceptions.UnknownException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pw_hash.HashUtils;


import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable, MyController {
    private static final Logger LOGGER = LogManager.getLogger();

    // instance variables containing the username and password submitted by the user attempting to login
    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    @FXML
    void doLogin(ActionEvent event){
        // get the user's login and password
        String login = this.loginField.getText();
        String password = this.passwordField.getText();
        //log login attempt
        LOGGER.info("Attempting login with following credentials...\n"+
                    "Username: " + login +
                    "\nPassword: "+password);
        // now that we have username and password, we can create a Session
        Session session = null;
        try{
            session = LoginGateway.login(login, password);
        }
        //If the username and password don't match what's in login.json, we get a 401 and enter this branch
        catch(UnauthorizedException e){
            Alerts.errorAlert("Login failed.", "Invalid Credentials");
            return;
        }
        //something weird happened. wasn't invalid credentials
        catch(UnknownException e){
            Alerts.errorAlert("Login Failed.", "Something bad happened");
            return;
        }
        //If the username and password are authorized, and login succeeds, we "hand the Session object
        // to the main controller" - DocRob
        //give application Session and Session token
        //login controller has no direct reference to the persongateway and doesn't need it
        //so it doesn't make sense to try to set the token here in the login controller
        //makes more sense to be the responsibilty of the main controller
        MainController.getInstance().setSession(session);
        MainController.getInstance().switchView(ScreenType.PERSONLIST, 0);
    }

    public void initialize(URL url, ResourceBundle resourceBundle){

    }
}
