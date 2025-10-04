package it.unicas.clinic.address.view.appointment.calendarView;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.StaffException;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;


/**
 * Controller of the GUI that shows appointments in a specific date in the calendar view.
 */
public class AppCalendarViewController {
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
    private Stage dialogueStage;

    public AppCalendarViewController() throws SQLException {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogueStage = dialogStage;
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

    public void handleHome(){
       dialogueStage.close();
    }
}
