package it.unicas.clinic.address.view.login;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.dao.mysql.LoginDAOImplementation;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller of the GUI that manages the staff username change window
 */
public class ChangeUsernameController {

    private Main mainApp;

    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp
     * @throws SQLException
     * @throws IOException
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private int staff_id;
    public void setId(int id) {staff_id = id;}

    @FXML
    private TextField passwordVisible;

    @FXML
    private TextField passwordHidden;

    @FXML
    private TextField username;

    @FXML
    private TextField confirmUsername;

    @FXML
    private ImageView openedEye;

    @FXML
    private ImageView closedEye;

    @FXML
    private void initialize(){

        closedEye.setVisible(false);
        openedEye.setVisible(true);

        passwordHidden.setVisible(true);
        passwordVisible.setVisible(false);

    }

    //Effectively changes the username in the database
    @FXML
    private void changeUsername() throws SQLException {
        String passwordText;
        if(!closedEye.isVisible()){
            passwordText = passwordHidden.getText();
        } else {
            passwordText = passwordVisible.getText();
        }
        String usernameText = username.getText();
        String confirmUsernameText = confirmUsername.getText();

        String errorMessage = "";

        if(isInputValid()) {
            //controllo che la passwordVisible sia inserita correttamente
            if(LoginDAOImplementation.checkPassword(passwordText,LoginDAOImplementation.getPassword(staff_id))) {
                //Controllo che il nuovo username sia inserito correttamente
                if(usernameText.equals(confirmUsernameText)) {
                    //Aggiorna username
                    LoginDAOImplementation.changeUsername(staff_id, usernameText);
                    stage.close();

                } else{
                    //Crea finestra d'errore
                    errorMessage = "Usernames do not match!";
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initOwner(stage);
                    alert.setTitle("Invalid Fields");
                    alert.setHeaderText("Please correct invalid fields");
                    alert.setContentText(errorMessage);

                    alert.showAndWait();
                }

            } else {
                //passwordVisible errata! riprovare
                errorMessage = "Wrong password! Try again!";
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(stage);
                alert.setTitle("Invalid Fields");
                alert.setHeaderText("Please correct invalid fields");
                alert.setContentText(errorMessage);

                alert.showAndWait();
            }

            //Prelevo dal database la vecchia passwordVisible e la confornto con quella inserita:
            //se non combaciano do errore


        }


    }

    //Makes the password visible
    @FXML
    private void onClickShowPassword(){
        closedEye.setVisible(true);
        openedEye.setVisible(false);

        passwordVisible.setVisible(true);
        passwordVisible.setText(passwordHidden.getText());
        passwordHidden.setVisible(false);


    }

    //Makes the password not visible
    @FXML
    private void onClickHidePassword(){
        closedEye.setVisible(false);
        openedEye.setVisible(true);

        passwordHidden.setVisible(true);
        passwordHidden.setText(passwordVisible.getText());
        passwordVisible.setVisible(false);
    }

    //Checks all the fields
    private boolean isInputValid() {
        String errorMessage = "";
        if(!closedEye.isVisible()) {
            if (passwordHidden.getText() == null || passwordHidden.getText().length() == 0) {
                errorMessage += "Insert the password!\n";
            }
        } else {
            if (passwordVisible.getText() == null || passwordVisible.getText().length() == 0) {
                errorMessage += "Insert the password!\n";
            }
        }

        if (username.getText() == null || username.getText().length() == 0) {
            errorMessage += "Insert username!\n";
        }
        if (confirmUsername.getText() == null || confirmUsername.getText().length() == 0) {
            errorMessage += "Confirm username!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
    @FXML
    private void handleCancel(){
        stage.close();
    }
}
