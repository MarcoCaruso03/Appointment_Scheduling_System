package it.unicas.clinic.address.view.staff;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Staff;
import it.unicas.clinic.address.model.dao.StaffDAO;
import it.unicas.clinic.address.model.dao.StaffException;
import it.unicas.clinic.address.model.dao.mysql.StaffDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

/**
 * Controller of the GUI that manages the staff update window
 */
public class StaffUpdateLayoutController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField specField;

    private Staff staff;
    private Stage dialogStage;
    private Main mainApp;
    private StaffDAO dao= StaffDAOMySQLImpl.getInstance();
    @FXML
    private void initialize() {
    }
    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp: singleton MainApp.
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        // Set the dialog icon.
        //this.dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
    }
    //Shows information of the existing staff
    public void setField(Staff s){
        staff=s;
        nameField.setText(s.getName());
        surnameField.setText(s.getSurname());
        specField.setText(s.getSpecialties());
    }
    //Effectively updates the staff in the database
    @FXML
    private void handleUpdate() {
        staff.setName(nameField.getText());
        staff.setSurname(surnameField.getText());
        staff.setSpecialties(specField.getText());
        try {
            if(!(staff.getName().equals("") &&  staff.getSurname().equals("") &&  staff.getSpecialties().equals(""))) {
                dao.update(staff);
                Staff updatedStaff = new Staff(staff.getId(), staff.getName(), staff.getSurname(), staff.getSpecialties());
                int index = mainApp.getStaffData().indexOf(staff);
                if (index >= 0) {
                    mainApp.getStaffData().set(index, updatedStaff); // Sostituisce l'oggetto nella lista
                }
                dialogStage.close();
            }
            else{
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Module error");
                errorAlert.setContentText("You have to fill all the fields!!!!");
                errorAlert.showAndWait();
            }
        } catch (StaffException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Database Error");
            errorAlert.setHeaderText("Could not update staff");
            errorAlert.setContentText("An error occurred while trying to update the staff member.");
            errorAlert.showAndWait();
        }
    }
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

}
