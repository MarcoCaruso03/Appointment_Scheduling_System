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


public class AppUpdateLayoutController {
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

    private Appointment app;
    private Stage dialogStage;
    private Main mainApp;
    private AppointmentDAO dao= AppointmentDAOMySQLImpl.getInstance();
    private StaffDAO staffDao= StaffDAOMySQLImpl.getInstance();
    private ScheduleDAO scheduleDao= ScheduleDAOMySQLImpl.getInstance();
    @FXML
    private void initialize() {
    }
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        // Set the dialog icon.
        //this.dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
    }
    public void setField(Appointment a){
        app=a;
        serviceField.setText(a.getService());
        dateField.setText(a.getDate().toString());
        timeField.setText(a.getTime().toString());
        durationField.setText(a.getDuration().toString());
        staffIdField.setText(Integer.toString(a.getStaffId()));
        clientIdField.setText(Integer.toString(a.getClientId()));
    }
    @FXML
    private void handleUpdate() throws SQLException {
        //Control all fields are filled
        if(serviceField.getText().equals("") || dateField.getText().equals("") || timeField.getText().equals("") || staffIdField.getText().equals("")) {
            mainApp.errorAlert("Error",
                    "Module Error",
                    "You have to fill all the fields");
        }

        else {
            try {
                app.setService(serviceField.getText());
                app.setDate(DataUtil.parseToDate(dateField.getText()));
                app.setTime(DataUtil.parseToTime(timeField.getText(), true));
                app.setDuration(DataUtil.parseToDuration(durationField.getText(), true));
                app.setStaffId(Integer.parseInt(staffIdField.getText()));
                app.setClientId(Integer.parseInt(clientIdField.getText()));
            }
            //DateTimeException catches date and time errors
            catch(DateTimeException e){
                    mainApp.errorAlert("Error",
                            "Date/Time Error",
                            "You have to enter valid date and time");
                app.setService(null);
            }
            catch(IllegalArgumentException e){
                mainApp.errorAlert("Format Error",
                        "Format Error",
                        "You have to enter valid date/time format");
                app.setService(null);
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
                // Verify if app is an effective appointment
                if (app.verifyAppointment(app)) {
                    dao.update(app);
                    Appointment updatedApp = new Appointment(app.getId(), app.getService(), app.getDate(), app.getTime(),app.getDuration(), app.getStaffId(), app.getClientId());
                    int index = mainApp.getAppointmentData().indexOf(app);
                    if (index >= 0) {
                        // Substitute object in list
                        mainApp.getAppointmentData().set(index, updatedApp);
                    }

                    dialogStage.close();
                }
            } catch (AppointmentException e) {
                mainApp.errorAlert("Database Error",
                        "Could not update appointment",
                        "An error occurred while trying to update the appointment ");
            }

        }
    }
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

}
