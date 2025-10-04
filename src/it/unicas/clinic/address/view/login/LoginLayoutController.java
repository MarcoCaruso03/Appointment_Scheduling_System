package it.unicas.clinic.address.view.login;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.dao.mysql.LoginDAOImplementation;
import it.unicas.clinic.address.utils.DataUtil;
import it.unicas.clinic.address.utils.DataUtil.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Controller for the Login GUI, which provides text fields to be filled with username and password.
 * It also provides buttons for exiting the application and for trying to log in with
 * the specified credentials.
 * Moreover, if the credentials are right it welcomes the user, else it notices the user.
 */
public class LoginLayoutController {

    private Main main;


    private String password;

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordShowField;
    @FXML
    private PasswordField passwordHideField;
    @FXML
    private ImageView openedEye;
    @FXML
    private ImageView closedEye;
    @FXML
    private Label forgottenPassword;

    /**
     * Is called by the main application to give a reference back to itself.
     * @param main: application
     */
    public void setMainApp(Main main) {
        this.main = main;
    }
    @FXML
    private void initialize(){
        /*
        Show opened eye icon and the password field
        Not show closed eye icon and the text field fot the password
        Set password as an empty string
         */
        passwordShowField.setVisible(false);
        passwordHideField.setVisible(true);
        closedEye.setVisible(false);
        openedEye.setVisible(true);
        password="";
    }

    //Makes the password visible
    @FXML
    private void handleShowPassword() {
        /*
        Show opened eye icon and the password field
        Not show closed eye icon and the text field fot the password
         */
        password=passwordHideField.getText();
        passwordShowField.setVisible(true);
        passwordShowField.setText(password);
        passwordHideField.setVisible(false);
        closedEye.setVisible(true);
        openedEye.setVisible(false);
    }

    //Makes the password not visible
    @FXML
    private void handleHidePassword() {
        /*
        Not show opened eye icon and the password field
        Show closed eye icon and the text field fot the password
         */
        password=passwordShowField.getText();
        passwordShowField.setVisible(false);
        passwordHideField.setVisible(true);
        passwordHideField.setText(password);
        closedEye.setVisible(false);
        openedEye.setVisible(true);
    }

    //Close window
    @FXML
    private void handleExit(){
        //Recall the fucntion in mainApp
        main.handleExit();
    }

    //Checks the credentials and loads proper home Views for staff manager/member
    @FXML
    private void handleLogin() throws SQLException {
        //Get inserted username
        String username = usernameField.getText();
        //Get inserted password
         if(closedEye.isVisible()){
             password= passwordShowField.getText();
         }
         else
             password= passwordHideField.getText();
         //Insert username and password in the login DAO
         LoginDAOImplementation impl = new LoginDAOImplementation(username,password);



         //Initialize data as null
        User data = null;
        try {
            //Save in data the result of the operation on teh SQL database
            data = impl.searchUser();
        } catch (SQLException e) {
            //Pop up an alert if something with the database operation goes wrong
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Database Error");
            error.setHeaderText("Something went wrong within database connection/operations");
            error.setContentText("Please contact the database assistance");

            ButtonType buttonTypeOne = new ButtonType("Ok");

            error.getButtonTypes().setAll(buttonTypeOne);

            Optional<ButtonType> result = error.showAndWait();
            if (result.get() == buttonTypeOne){
                System.exit(0);
            }
        }
        if(data!=null){ //Credentials found

            main.setId(impl.getId());
            main.setIsManager(data.getManager());
            //Alert to welcome user
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login result");
            alert.setHeaderText("Logged in successfully");
            alert.setContentText("Welcome back "+data.getName()+" "+data.getSurname());
             ButtonType button = new ButtonType("Ok");
             alert.getButtonTypes().setAll(button);
             Optional<ButtonType> r = alert.showAndWait();
             if (r.get() == button){
                 alert.close();
                 if(data.getManager()) {
                     main.initStaffManager();
                 }
                 else {
                     main.initStaff();
                 }
                 main.sendClientNotification();

             }

           //  main.getId(StaffDAO.);

         }
         else { //Credentials not found

             //Alert notices user about wrong username or password
             Alert alert = new Alert(Alert.AlertType.INFORMATION);
             alert.setTitle("Login result");
             alert.setHeaderText("Login failed");
             alert.setContentText("Username or password is incorrect");
             ButtonType button = new ButtonType("Ok");
             alert.getButtonTypes().setAll(button);
             Optional<ButtonType> r = alert.showAndWait();
             if (r.get() == button){
                 alert.close();
             }
         }
    }

    @FXML
    private void handleEnter(KeyEvent event) throws SQLException {
        if(event.getCode()==KeyCode.ENTER){
            handleLogin();
        }
    }

    @FXML
    private void handleForgottenPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgotten Password");
        alert.setHeaderText("Please, contact the staff manager to restore you password");
        alert.setContentText("If the problem persists, please contact the database assistance");
        ButtonType button = new ButtonType("Ok");
        alert.getButtonTypes().setAll(button);
        Optional<ButtonType> r = alert.showAndWait();
    }
}
