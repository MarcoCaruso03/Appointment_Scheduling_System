package it.unicas.clinic.address.view.credential;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Credential;
import it.unicas.clinic.address.view.staff.StaffAddingLayoutController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller of the GUI that manages the insertion of new staff credentials
 */
public class InitCredentialController {

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

    private StaffAddingLayoutController staffAddingLayoutController;

    public void setStaffAddingLayoutController(StaffAddingLayoutController staffAddingLayoutController) {
        this.staffAddingLayoutController = staffAddingLayoutController;
    }

    private String username;
    private String password;
    private String confirmPassword;

    @FXML
    private TextField usernameTxt;

    @FXML
    private TextField passwordVisibleTxt;

    @FXML
    private TextField passwordHiddenTxt;

    @FXML
    private TextField confirmPasswordVisibleTxt;

    @FXML
    private TextField confirmPasswordHiddenTxt;

    @FXML
    private ImageView openedEye1;

    @FXML
    private ImageView openedEye2;

    @FXML
    private ImageView closedEye1;

    @FXML
    private ImageView closedEye2;

    @FXML
    private CheckBox ownerCheckBox;

    @FXML
    private void initialize(){

        closedEye1.setVisible(false);
        openedEye1.setVisible(true);

        passwordVisibleTxt.setVisible(false);
        passwordHiddenTxt.setVisible(true);

        closedEye2.setVisible(false);
        openedEye2.setVisible(true);

        confirmPasswordVisibleTxt.setVisible(false);
        confirmPasswordHiddenTxt.setVisible(true);

    }

    //Make the password visible
    @FXML
    private void onClickShowPassword1() {
        closedEye1.setVisible(true);
        openedEye1.setVisible(false);

        passwordVisibleTxt.setVisible(true);
        passwordVisibleTxt.setText(passwordHiddenTxt.getText());
        passwordHiddenTxt.setVisible(false);
    }

    //Make the password not visible
    @FXML
    private void onClickHidePassword1() {
        closedEye1.setVisible(false);
        openedEye1.setVisible(true);

        passwordVisibleTxt.setVisible(false);
        passwordHiddenTxt.setVisible(true);
        passwordHiddenTxt.setText(passwordVisibleTxt.getText());

    }

    //Make the confirmation password visible
    @FXML
    private void onClickShowPassword2() {
        closedEye2.setVisible(true);
        openedEye2.setVisible(false);

        confirmPasswordVisibleTxt.setVisible(true);
        confirmPasswordVisibleTxt.setText(confirmPasswordHiddenTxt.getText());
        confirmPasswordHiddenTxt.setVisible(false);
    }

    //Make the confirmation password not visible
    @FXML
    private void onClickHidePassword2() {
        closedEye2.setVisible(false);
        openedEye2.setVisible(true);

        confirmPasswordVisibleTxt.setVisible(false);
        confirmPasswordHiddenTxt.setVisible(true);
        confirmPasswordHiddenTxt.setText(confirmPasswordVisibleTxt.getText());

    }

    //Effectively adds the credential in the database
    @FXML
    private void handleAddCredential() {
        int owner;
        if(ownerCheckBox.isSelected()){
            owner = 1;
        } else {
            owner = 0;
        }
        username = usernameTxt.getText();
        password = passwordHiddenTxt.getText();
        confirmPassword = confirmPasswordHiddenTxt.getText();

        if(IsInputValid()){
            if(password.equals(confirmPassword)) {
                Credential credential = new Credential(username, password, owner);
                staffAddingLayoutController.setCredential(credential);
                stage.close();

            } else {
                String errorMessage = "Passwords dont match! Try again!";
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(stage);
                alert.setTitle("Invalid password");
                alert.setHeaderText("Please insert the same password");
                alert.setContentText(errorMessage);

                alert.showAndWait();
            }
        }

    }

    //Checks all the fields
    private boolean IsInputValid() {
        String errorMessage = "";

        if(username == null || username.equals("")){
            errorMessage += "Username cannot be empty!";
        }
        if(password == null || password.equals("")){
            errorMessage += "Password cannot be empty!";
        }
        if(confirmPassword == null || confirmPassword.equals("")){
            errorMessage += "Confirm Password cannot be empty!";
        }

        if(errorMessage.equals("")){
            return true;
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Please correct invalid fields");
            alert.setHeaderText(errorMessage);
            alert.setContentText(errorMessage);

            alert.showAndWait();
            return false;
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }
}
