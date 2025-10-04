package it.unicas.clinic.address.view.appointment;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.StaffException;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Controller of the GUI that manages the appointments.
 */
public class AppointmentViewController {
    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<Appointment, String> serviceColumn;
    @FXML
    private TableColumn<Appointment, LocalDate> dateColumn;
    @FXML
    private TableColumn<Appointment, LocalTime> timeColumn;
    @FXML
    private TableColumn<Appointment, LocalTime> durationColumn;
    @FXML
    private TableColumn<Appointment, Integer> idColumn;
    @FXML
    private TableColumn<Appointment, Integer> staffIdColumn;
    @FXML
    private TableColumn<Appointment, Integer> clientIdColumn;

    // Reference to the main application.
    private Main mainApp;
    private AppointmentDAO dao= AppointmentDAOMySQLImpl.getInstance();

    public AppointmentViewController() throws SQLException {
    }

    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp: singleton MainApp.
     * @throws SQLException
     */
    public void setMainApp(Main mainApp) throws SQLException {
        this.mainApp = mainApp;
        // Add observable list data to the table
        appointmentTable.setItems(mainApp.getAppointmentData());
        if(mainApp.getIsManager())
            mainApp.getAppointmentData().addAll(dao.select(new Appointment(0,null, null, null,null,0,0)));
        else
            mainApp.getAppointmentData().addAll(dao.select(new Appointment(0,null, null, null,null, mainApp.getUser_id(), 0)));

    }
    @FXML
    public void initialize() {
        // Column binding with Appointment's properties
        serviceColumn.setCellValueFactory(cellData -> cellData.getValue().serviceProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject()); // IntegerProperty richiede asObject()
        staffIdColumn.setCellValueFactory(cellData -> cellData.getValue().staffIdProperty().asObject());
        clientIdColumn.setCellValueFactory(cellData -> cellData.getValue().clientIdProperty().asObject());
    }

    //Calls for the insert appointment GUI
    @FXML
    private void handleInsertApp() {
        mainApp.showAppInsertDialog();
    }

    //Calls for the update appointment GUI
    @FXML
    private void handleUpdateApp(){
        Appointment selectedApp = appointmentTable.getSelectionModel().getSelectedItem();
        if(selectedApp != null && selectedApp.getDate().isAfter(LocalDate.now())){
            mainApp.showAppUpdateDialog(selectedApp);
            //mainApp.getAppointmentData().remove(selectedApp);
        }
        else if(selectedApp != null && !selectedApp.getDate().isAfter(LocalDate.now())){
            mainApp.warningAlert("Attention",
                    "You can't update a past appointment",
            "Please select a valid appointment");
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

    //Deletes the selected appointment
    @FXML
    private void handleDeleteApp() {

        int selectedIndex = appointmentTable.getSelectionModel().getSelectedIndex();
        Appointment selectedApp = appointmentTable.getSelectionModel().getSelectedItem();
        if(selectedApp != null && selectedApp.getDate().isAfter(LocalDate.now()) ){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Delete an Appointment");
            alert.setHeaderText("Do you want to delete this appointment?");
            alert.setContentText("Do you want to delete this appointment?");
            ButtonType buttonTypeOne = new ButtonType("Yes");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne){
                try{
                    if(selectedApp.getDate().isAfter(LocalDate.now())){
                        mainApp.sendClientCancellation(selectedApp);
                    }
                    dao.softDelete(selectedApp.getId());
                    mainApp.getAppointmentData().remove(selectedApp);

                }catch (StaffException e){

                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Database Error");
                    errorAlert.setHeaderText("Could not delete appointment");
                    errorAlert.setContentText("An error occurred while trying to delete the appointment.");
                    errorAlert.showAndWait();

                } catch (SQLException e) {
                    mainApp.errorAlert("Mail error",
                            "Couldn't send email to client",
                            "");
                }

            }
        }
        else if(selectedApp != null && !selectedApp.getDate().isAfter(LocalDate.now()) ){
            mainApp.warningAlert("Attention",
                    "You can't delete a past appointment",
                    "Please select a valid appointment");
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Appointment Selected");
            alert.setContentText("Please select an Appointment into the table.");

            alert.showAndWait();
        }
    }

    /**
     * Go back to home View.
     */
    public void handleHome(){
       if(mainApp.getIsManager())
           mainApp.initStaffManager();
       else
           mainApp.initStaff();
    }
}
