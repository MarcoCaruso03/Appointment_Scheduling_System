package it.unicas.clinic.address.view.appointment;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.AppointmentException;
import it.unicas.clinic.address.model.dao.ScheduleDAO;
import it.unicas.clinic.address.model.dao.StaffDAO;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.DAOClient;
import it.unicas.clinic.address.model.dao.mysql.ScheduleDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.StaffDAOMySQLImpl;
import it.unicas.clinic.address.utils.DataUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.DateTimeException;


public class AppAddingLayoutController {

    @FXML
    private TextField serviceField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField timeField;
    @FXML
    private TextField durationField;
    @FXML
    private TextField staffIdField;
    @FXML
    private TextField clientIdField;



    private Main mainApp;
    private Stage dialogStage;
    private boolean verifyLen = true;
    private Appointment app;
    private boolean okClicked = false;
    private AppointmentDAO dao= AppointmentDAOMySQLImpl.getInstance();
    private StaffDAO staffDao= StaffDAOMySQLImpl.getInstance();
    private ScheduleDAO scheduleDao = ScheduleDAOMySQLImpl.getInstance();

    @FXML
    private void initialize() {
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        this.verifyLen = verifyLen;

        // Set the dialog icon.
        //this.dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    public void setMainApp(Main main) {
        this.mainApp = main;
    }


    @FXML
    private void handleSave() throws SQLException {
        //Control that all fields are filled
        if(serviceField.getText().equals("") || dateField.getText().equals("") || timeField.getText().equals("") || staffIdField.getText().equals("")) {
            mainApp.errorAlert("Error",
                    "Module Error",
                    "You have to fill all the fields");
        }
        else {
            try {
                app = new Appointment(serviceField.getText(), DataUtil.parseToDate(dateField.getText()), DataUtil.parseToTime(timeField.getText(), true), DataUtil.parseToDuration(durationField.getText(), true),Integer.parseInt(staffIdField.getText()), Integer.parseInt(clientIdField.getText()));
            }
            //DateTimeException catches errors in LocalDate and LocalTime values
            catch (DateTimeException e) {
                mainApp.errorAlert("Error",
                        "Date/Time Error",
                        "You have to enter valid date and time");
                app = new Appointment(0,null,null,null,null,0,0);
            }
            catch(IllegalArgumentException e){
                mainApp.errorAlert("Format Error",
                        "Format Error",
                        "You have to enter valid date/time format");
                app = new Appointment(0,null,null,null,null,0,0);
            }
            if(staffDao.select(app.getStaffId()) == null) {
                mainApp.errorAlert("Module Error",
                        "Staff Error",
                        "You have to enter valid staff id");
                return;
            }
            if (DAOClient.select(app.getClientId())==null){
                mainApp.errorAlert("Module Error",
                        "Client Error",
                        "You have to enter valid client id");
                return;
            }
            if(!scheduleDao.isAvailable(app.getDate(),app.getTime(),app.getStaffId())){
                mainApp.errorAlert("Error",
                        "Schedule Error",
                        "Staff member is not available on specified day/time");
                return;
            }
            try {
                //Verify if app is an effective appointment
                if (app.verifyAppointment(app)) {
                    try {
                        dao.insert(app);
                        mainApp.getAppointmentData().add(dao.getLastApp());
                        dialogStage.close();

                    } catch (AppointmentException e) {
                        mainApp.errorAlert("Database Error",
                                "Could not add the appointment",
                                "An error occured while trying to add the appointment");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void handleCancel(){
        dialogStage.close();
    }

    private boolean isInputValid() {
        return true;
    }


}
