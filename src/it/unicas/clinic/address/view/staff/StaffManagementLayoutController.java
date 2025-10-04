package it.unicas.clinic.address.view.staff;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.model.Schedule;
import it.unicas.clinic.address.model.Staff;
import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.ScheduleDAO;
import it.unicas.clinic.address.model.dao.StaffDAO;
import it.unicas.clinic.address.model.dao.StaffException;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.ScheduleDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.StaffDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller of the GUI that manages the general Staff table
 */
public class StaffManagementLayoutController {

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
    private AppointmentDAO daoApp = AppointmentDAOMySQLImpl.getInstance();
    private ScheduleDAO daoSchedule= ScheduleDAOMySQLImpl.getInstance();

    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp: singleton MainApp.
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


    //Reset the filter
    @FXML
    private void showStaff(){
        //StaffDAOMySQLImpl.getInstance().select(new Staff(0,null, null, null));
        mainApp.getStaffData().addAll(dao.select(new Staff(0,null, null, null)));
    }
    //Pops up the staff adding window
    @FXML
    private void handleInsertStaff() {
        mainApp.showStaffInsertDialog();
    }
    //Pops up the staff update window
    @FXML
    private void handleUpdateStaff(){
        Staff selectedStaff = staffTable.getSelectionModel().getSelectedItem();
        if(selectedStaff != null){
            mainApp.showStaffUpdateDialog(selectedStaff);
            mainApp.getStaffData().remove(selectedStaff);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Staff Selected");
            alert.setContentText("Please select a Staff into the table.");

            alert.showAndWait();
        }
    }
    //Soft deletes the staff in the database
    @FXML
    private void handleDeleteStaff() throws SQLException {

        int selectedIndex = staffTable.getSelectionModel().getSelectedIndex();
        if(selectedIndex >= 0){
            Staff selectedStaff = staffTable.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Delete a Staff Member");
            alert.setHeaderText("Do you want to delete this member?");
            alert.setContentText("Do you want to delete this member? This operation will delete also all the schedules of this staff member");
            ButtonType buttonTypeOne = new ButtonType("Yes");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne){
                try{
                    dao.softDelete(selectedStaff);
                    List< Appointment> futureApp= daoApp.getfutureAppStaff(LocalDate.now(), selectedStaff.getId());
                    for(Appointment a : futureApp)
                        daoApp.delete(a.getId());
                    List<Schedule> futureSchedule = daoSchedule.futureSchedule(selectedStaff.getId());
                    for(Schedule s: futureSchedule)
                        daoSchedule.delete(s);

                    //daoSchedule.delete(selectedStaff);
                    mainApp.getStaffData().remove(selectedStaff);
                    //mainApp.getStaffData().forEach(System.out::println);

                }catch (StaffException e){

                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Database Error");
                    errorAlert.setHeaderText("Could not delete staff");
                    errorAlert.setContentText("An error occurred while trying to delete the staff member.");
                    errorAlert.showAndWait();


                }

            }
        }

        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Staff Selected");
            alert.setContentText("Please select a Staff into the table.");

            alert.showAndWait();
        }
    }
    //Pops up the schedule table
    @FXML
    private void handleSchedule() throws IOException {
        //take the staff del quale dovrò mostrare gli schedule
        Staff selectedStaff = staffTable.getSelectionModel().getSelectedItem();
        if(selectedStaff != null){
            mainApp.showScheduleManagmentLayout(selectedStaff);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Staff Selected");
            alert.setContentText("Please select a Staff into the table.");

            alert.showAndWait();
        }
    }
    //Applies filter
    @FXML
    private void handleFilter(){
        String name = staffName.getText();
        String surname = staffSurname.getText();
        if(!name.equals("") && !surname.equals("")){
            List<Staff> list= dao.select(new Staff(0,null,null,null));
            mainApp.getStaffData().clear();
            mainApp.getStaffData().addAll(list);
        }
        else {
            List<Staff> list = dao.select(new Staff(0, name, surname, null));
            mainApp.getStaffData().clear();
            mainApp.getStaffData().addAll(list);
        }
    }
    //Close the window
    @FXML
    private void handleBack(){
        mainApp.initStaffManager();
    }
}
