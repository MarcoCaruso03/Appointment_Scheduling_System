package it.unicas.clinic.address.view.appointment;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Staff;
import it.unicas.clinic.address.model.dao.StaffDAO;
import it.unicas.clinic.address.model.dao.StaffException;
import it.unicas.clinic.address.model.dao.mysql.StaffDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller of the GUI to select the staff while creating an appointment
 */
public class AppStaffViewController {

    @FXML
    private TableView<Staff> staffTable;
    @FXML
    private TableColumn<Staff, String> nameColumn;
    @FXML
    private TableColumn<Staff, String> surnameColumn;
    @FXML
    private TableColumn<Staff, String> specColumn;
    @FXML
    private TableColumn<Staff, Integer> idColumn;
    @FXML
    private TextField staffName;
    @FXML
    private TextField staffSurname;

    // Reference to the main application.
    private Main mainApp;
    private StaffDAO dao=StaffDAOMySQLImpl.getInstance();
    private Stage dialogStage;

    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        // Add observable list data to the table
        staffTable.setItems(mainApp.getStaffData());
        mainApp.getStaffData().addAll(dao.select(new Staff(0,null, null, null)));

    }
    @FXML
    public void initialize() {
        // Binding delle colonne con le proprietà della classe Staff
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        surnameColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        specColumn.setCellValueFactory(cellData -> cellData.getValue().specialtiesProperty());
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject()); // IntegerProperty richiede asObject()


    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    //Saves the selected staff in main.
    @FXML
    private void handleSelect(){
        Staff selectedStaff = staffTable.getSelectionModel().getSelectedItem();
        if(selectedStaff != null){
            mainApp.saveStaff(selectedStaff.getId());
        }
        else{
            mainApp.errorAlert("Error",
                    "Table Error",
                    "Please select a staff member");
        }
        mainApp.getStaffData().clear();
        dialogStage.close();
    }
    //Close the window
    @FXML
    private void handleBack(){
        mainApp.getStaffData().clear();
        dialogStage.close();
    }
    @FXML
    private void handleFilter(){
        String name = staffName.getText();
        String surname = staffSurname.getText();
        if(name.equals("") && surname.equals("")){
            List<Staff> staffList = dao.select(new Staff(0,null, null, null));
            mainApp.getStaffData().clear();
            mainApp.getStaffData().addAll(staffList);
        }
        else{
            List<Staff> list = dao.select(new Staff(0,name,surname,null));
            mainApp.getStaffData().clear();
            mainApp.getStaffData().addAll(list);
        }
    }
}
