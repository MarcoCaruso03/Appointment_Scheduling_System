package it.unicas.clinic.address.view.staff;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Credential;
import it.unicas.clinic.address.model.Staff;
import it.unicas.clinic.address.model.dao.StaffDAO;
import it.unicas.clinic.address.model.dao.StaffException;
import it.unicas.clinic.address.model.dao.mysql.LoginDAOImplementation;
import it.unicas.clinic.address.model.dao.mysql.StaffDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller of the GUI that manages the staff adding window
 */
public class StaffAddingLayoutController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField specField;


    private Main mainApp;
    private Stage dialogStage;
    private boolean verifyLen = true;
    private Staff staff;
    private boolean okClicked = false;
    private StaffDAO dao=StaffDAOMySQLImpl.getInstance();

    private Credential credential;

    public void setCredential(Credential credential) {
        this.credential = credential;
    }
    @FXML
    private void initialize() {
    }


    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        this.verifyLen = verifyLen;

        // Set the dialog icon.
        //this.dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
    }


    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Link the local copy of MainApp with the singleton.
     * @param main: singleton MainApp.
     */
    public void setMainApp(Main main) {
        this.mainApp = main;
    }

    //Effectively adds the staff to the database
    @FXML
    private void handleSave() throws IOException, StaffException {
        this.staff = new Staff(nameField.getText(), surnameField.getText(), specField.getText());
        try {
            if(this.staff.verifyStaff(this.staff)){
                //Devo obbligare il manager a fornire delle credenziali per il nuovo staff member
                mainApp.initCredential(dialogStage, this);
                if(credential!=null){
                    try {
                        dao.insert(this.staff);
                        mainApp.getStaffData().add(dao.getLastStaff());


                        LoginDAOImplementation.insertCredential(credential.getUsername(), credential.getPassword(), credential.getOwner(),dao.getLastStaff().getId());

                        dialogStage.close();



                    /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initOwner(mainApp.getPrimaryStage());
                    alert.setTitle("Schedule");
                    alert.setHeaderText("Do you want to add a schedule?");
                    alert.setContentText("Do you want to add a schedule?");
                    ButtonType buttonTypeOne = new ButtonType("Yes");
                    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonTypeOne){
                        mainApp.addSchedule();
                    }*/
                    }catch (StaffException e){
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Database Error");
                        errorAlert.setHeaderText("Could not delete staff");
                        errorAlert.setContentText("An error occurred while trying to add the staff member.");
                        errorAlert.showAndWait();
                    }
                }/*else {
                    String errorMessage = "No credential found!";
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initOwner(dialogStage);
                    alert.setTitle("No Credential");
                    alert.setHeaderText("Please insert the credential");
                    alert.setContentText(errorMessage);

                    alert.showAndWait();
                }*/



            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Module error");
                alert.setContentText("You have to fill all the fields!!");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void handleCancel(){
        dialogStage.close();
    }

    private boolean isInputValid() {
        return true;
    }

    private void insertSchedule() {

    }

}
