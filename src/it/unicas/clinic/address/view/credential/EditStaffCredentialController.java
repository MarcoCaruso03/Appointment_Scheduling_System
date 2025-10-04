package it.unicas.clinic.address.view.credential;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.dao.mysql.DAOMySQLSettings;
import it.unicas.clinic.address.model.dao.mysql.LoginDAOImplementation;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller of the GUI that manages the staff edit credential window
 */
public class EditStaffCredentialController {

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

    private int staffManager_id;
    public void setManagerId(int id) {staffManager_id = id;}

    private int staff_id;
    public void setId(int id) {staff_id = id;}

    @FXML
    private TextField staffManagerPasswordVisible;

    @FXML
    private TextField staffManagerPasswordHidden;

    @FXML
    private TextField newStaffUsername;

    @FXML
    private TextField newStaffPasswordVisible;

    @FXML
    private TextField newStaffPasswordHidden;

    @FXML
    private TextField confirmNewPasswordVisible;

    @FXML
    private TextField confirmNewPasswordHidden;

    @FXML
    private ImageView openedEye1;

    @FXML
    private ImageView closedEye1;

    @FXML
    private ImageView openedEye2;

    @FXML
    private ImageView closedEye2;

    @FXML
    private ImageView openedEye3;

    @FXML
    private ImageView closedEye3;

    @FXML
    private void initialize() {
        openedEye1.setVisible(true);
        closedEye1.setVisible(false);

        staffManagerPasswordVisible.setVisible(false);
        staffManagerPasswordHidden.setVisible(true);

        openedEye2.setVisible(true);
        closedEye2.setVisible(false);

        newStaffPasswordVisible.setVisible(false);
        newStaffPasswordHidden.setVisible(true);

        openedEye3.setVisible(true);
        closedEye3.setVisible(false);

        confirmNewPasswordVisible.setVisible(false);
        confirmNewPasswordHidden.setVisible(true);

    }

    //Effectively changes the credentials in the db
    @FXML
    private void OnClickEditCredential() throws SQLException {
        String managerPassword;
        String newPassword;
        String confirmPassword;
        String newUsername = newStaffUsername.getText();

        if(openedEye1.isVisible()) {
            managerPassword = staffManagerPasswordHidden.getText();
        } else {
            managerPassword = staffManagerPasswordVisible.getText();
        }

        if(openedEye2.isVisible()) {
            newPassword = newStaffPasswordHidden.getText();
        } else {
            newPassword = newStaffPasswordVisible.getText();
        }

        if(openedEye3.isVisible()) {
            confirmPassword = confirmNewPasswordHidden.getText();
        } else {
            confirmPassword = confirmNewPasswordVisible.getText();
        }

        if(LoginDAOImplementation.checkPassword(managerPassword, LoginDAOImplementation.getPassword(staffManager_id))){
            if(newPassword.equals(confirmPassword)){
                LoginDAOImplementation.changeUsername(staff_id, newUsername);
                LoginDAOImplementation.changePassword(staff_id, newPassword);
                stage.close();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Invalid password");
                alert.setHeaderText("Password does not match!");
                alert.setContentText(" Try again!");

                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Incorrect Password");
            alert.setHeaderText("Wrong Password!");
            alert.setContentText(" Try again!");


            alert.showAndWait();
        }

    }

    //Make the manager password visible
    @FXML
    private void OnClickShowPassword1() {
        openedEye1.setVisible(false);
        closedEye1.setVisible(true);

        staffManagerPasswordVisible.setVisible(true);
        staffManagerPasswordVisible.setText(staffManagerPasswordHidden.getText());
        staffManagerPasswordHidden.setVisible(false);
    }

    //Make the manager password not visible
    @FXML
    private void OnClickHidePassword1() {
        openedEye1.setVisible(true);
        closedEye1.setVisible(false);

        staffManagerPasswordHidden.setVisible(true);
        staffManagerPasswordHidden.setText(staffManagerPasswordVisible.getText());
        staffManagerPasswordVisible.setVisible(false);

    }

    //Make the new password visible
    @FXML
    private void OnClickShowPassword2() {
        openedEye2.setVisible(false);
        closedEye2.setVisible(true);

        newStaffPasswordVisible.setVisible(true);
        newStaffPasswordVisible.setText(newStaffPasswordHidden.getText());
        newStaffPasswordHidden.setVisible(false);
    }

    //Make the new password not visible
    @FXML
    private void OnClickHidePassword2() {
        openedEye2.setVisible(true);
        closedEye2.setVisible(false);

        newStaffPasswordHidden.setVisible(true);
        newStaffPasswordHidden.setText(newStaffPasswordVisible.getText());
        newStaffPasswordVisible.setVisible(false);

    }

    //Make the confirmation visible
    @FXML
    private void OnClickShowPassword3() {
        openedEye3.setVisible(false);
        closedEye3.setVisible(true);

        confirmNewPasswordVisible.setVisible(true);
        confirmNewPasswordVisible.setText(confirmNewPasswordHidden.getText());
        confirmNewPasswordHidden.setVisible(false);
    }

    //Make the confirmation not visible
    @FXML
    private void OnClickHidePassword3() {
        openedEye3.setVisible(true);
        closedEye3.setVisible(false);

        confirmNewPasswordHidden.setVisible(true);
        confirmNewPasswordHidden.setText(confirmNewPasswordVisible.getText());
        confirmNewPasswordVisible.setVisible(false);

    }

    //Close the window
    @FXML
    private void OnClickCancel() {
        stage.close();
    }
}
