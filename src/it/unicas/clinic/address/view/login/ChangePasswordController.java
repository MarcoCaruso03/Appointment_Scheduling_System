package it.unicas.clinic.address.view.login;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.dao.mysql.DAOMySQLSettings;
import it.unicas.clinic.address.model.dao.mysql.LoginDAOImplementation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller of the GUI that manages the password change window
 */
public class ChangePasswordController {

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
    private TextField oldPasswordVisible;

    @FXML
    private TextField oldPasswordHidden;

    @FXML
    private TextField newPasswordVisible;

    @FXML
    private TextField newPasswordHidden;

    @FXML
    private TextField confirmPasswordVisible;

    @FXML
    private TextField confirmPasswordHidden;

    @FXML
    private ImageView closedEye1;

    @FXML
    private ImageView openedEye1;

    @FXML
    private ImageView closedEye2;

    @FXML
    private ImageView openedEye2;

    @FXML
    private ImageView closedEye3;

    @FXML
    private ImageView openedEye3;

    @FXML
    private void initialize(){

        closedEye1.setVisible(false);
        openedEye1.setVisible(true);

        newPasswordVisible.setVisible(false);
        newPasswordHidden.setVisible(true);

        closedEye2.setVisible(false);
        openedEye2.setVisible(true);

        confirmPasswordVisible.setVisible(false);
        confirmPasswordHidden.setVisible(true);

        closedEye3.setVisible(false);
        openedEye3.setVisible(true);

        oldPasswordVisible.setVisible(false);
        oldPasswordHidden.setVisible(true);
    }

    //Effectively changes the password in the database
    @FXML
    private void changePassword(ActionEvent event) throws SQLException {

        String oldPasswordText;
        String newPasswordText;
        String confirmPasswordText;

        if(openedEye1.isVisible()){
            newPasswordText = newPasswordHidden.getText();
        }
        else {
            newPasswordText = newPasswordVisible.getText();
        }

        if(openedEye2.isVisible()){
            confirmPasswordText = confirmPasswordHidden.getText();
        } else {
            confirmPasswordText = confirmPasswordVisible.getText();
        }

        if(openedEye3.isVisible()){
            oldPasswordText = oldPasswordHidden.getText();
        } else {
            oldPasswordText = oldPasswordVisible.getText();
        }


        String errorMessage = "";

        if(isInputValid()) {
            //controllo che la vecchia password sia inserita correttamente
            if(LoginDAOImplementation.checkPassword(oldPasswordText,LoginDAOImplementation.getPassword(staff_id))) {
                //Controllo che la password nuova sia inserita correttamente
                if(newPasswordText.equals(confirmPasswordText)) {
                    //Aggiorna password
                    LoginDAOImplementation.changePassword(staff_id, newPasswordText);
                    stage.close();

                } else{
                    //Crea finestra d'errore
                    errorMessage = "Passwords do not match!";
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initOwner(stage);
                    alert.setTitle("Invalid Fields");
                    alert.setHeaderText("Please correct invalid fields");
                    alert.setContentText(errorMessage);

                    alert.showAndWait();
                }

            } else {
                //password errata! riprovare
                errorMessage = "Wrong password! Try again!";
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(stage);
                alert.setTitle("Invalid Fields");
                alert.setHeaderText("Please correct invalid fields");
                alert.setContentText(errorMessage);

                alert.showAndWait();
            }

            //Prelevo dal database la vecchia password e la confornto con quella inserita:
            //se non combaciano do errore


        }


    }

    //Makes the old password visible
    @FXML
    private void onClickShowPassword1() {
        closedEye1.setVisible(true);
        openedEye1.setVisible(false);

        newPasswordVisible.setVisible(true);
        newPasswordVisible.setText(newPasswordHidden.getText());
        newPasswordHidden.setVisible(false);
    }

    //Makes the old password not visible
    @FXML
    private void onClickHidePassword1() {
        closedEye1.setVisible(false);
        openedEye1.setVisible(true);

        newPasswordVisible.setVisible(false);
        newPasswordHidden.setVisible(true);
        newPasswordHidden.setText(newPasswordVisible.getText());

    }

    //Makes the new password visible
    @FXML
    private void onClickShowPassword2() {
        closedEye2.setVisible(true);
        openedEye2.setVisible(false);

        confirmPasswordVisible.setVisible(true);
        confirmPasswordVisible.setText(confirmPasswordHidden.getText());
        confirmPasswordHidden.setVisible(false);
    }

    //Makes the new password not visible
    @FXML
    private void onClickHidePassword2() {
        closedEye2.setVisible(false);
        openedEye2.setVisible(true);

        confirmPasswordVisible.setVisible(false);
        confirmPasswordHidden.setVisible(true);
        confirmPasswordHidden.setText(confirmPasswordVisible.getText());
    }

    //Makes the confirmation password visible
    @FXML
    private void onClickShowPassword3() {
        closedEye3.setVisible(true);
        openedEye3.setVisible(false);

        oldPasswordVisible.setVisible(true);
        oldPasswordVisible.setText(oldPasswordHidden.getText());
        oldPasswordHidden.setVisible(false);
    }

    //Makes the confirmation password not  visible
    @FXML
    private void onClickHidePassword3() {
        closedEye3.setVisible(false);
        openedEye3.setVisible(true);

        oldPasswordVisible.setVisible(false);
        oldPasswordHidden.setVisible(true);
        oldPasswordHidden.setText(oldPasswordVisible.getText());
    }

    @FXML
    private void onClickCloseWindow()
    {
        stage.close();
    }

    //Checks all the fields
    private boolean isInputValid() {
        String errorMessage = "";
        if(openedEye3.isVisible()){
            if (oldPasswordHidden.getText() == null || oldPasswordHidden.getText().length() == 0) {
                errorMessage += "Insert the old password!\n";
            }
        } else {
            if (oldPasswordVisible.getText() == null || oldPasswordVisible.getText().length() == 0) {
                errorMessage += "Insert the old password!\n";
            }
        }

        if(openedEye1.isVisible()){
            if (newPasswordHidden.getText() == null || newPasswordHidden.getText().length() == 0) {
                errorMessage += "Insert the new password!\n";
            }
        } else {
            if (newPasswordVisible.getText() == null || newPasswordVisible.getText().length() == 0) {
                errorMessage += "Insert the new password!\n";
            }
        }

        if(openedEye2.isVisible()){
            if (confirmPasswordHidden.getText() == null || confirmPasswordHidden.getText().length() == 0) {
                errorMessage += "Confirm password!\n";
            }
        } else {
            if (confirmPasswordVisible.getText() == null || confirmPasswordVisible.getText().length() == 0) {
                errorMessage += "Confirm password!\n";
            }
        }

        if (errorMessage.isEmpty()) {
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