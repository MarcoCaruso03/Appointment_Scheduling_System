package it.unicas.clinic.address.view.appointment;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.model.Client;
import it.unicas.clinic.address.model.Schedule;
import it.unicas.clinic.address.model.Staff;
import it.unicas.clinic.address.model.dao.*;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.DAOClient;
import it.unicas.clinic.address.model.dao.mysql.ScheduleDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.StaffDAOMySQLImpl;
import it.unicas.clinic.address.utils.DataUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.util.ArrayList;

/**
 * Controller of the GUI that manages the update of an existing appointment being the staff manager
 */
public class AppUpdateLayoutController2 {
    @FXML
    private TextField serviceField;
    @FXML
    private TextField timeField;
    @FXML
    private Label staffName;
    @FXML
    private Label staffSurname;
    @FXML
    private Label clientName;
    @FXML
    private Label clientSurname;


    private Main mainApp;
    private Stage dialogStage;
    private boolean verifyLen = true;
    private Appointment app;
    private boolean okClicked = false;
    private AppointmentDAO dao = AppointmentDAOMySQLImpl.getInstance();
    private StaffDAO staffDao = StaffDAOMySQLImpl.getInstance();
    private ScheduleDAO scheduleDao = ScheduleDAOMySQLImpl.getInstance();
    private Staff selectedStaff;
    private Client selectedClient;

    @FXML
    private void initialize() {
    }


    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        this.verifyLen = verifyLen;

        // Set the dialog icon.
        //this.dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
    }

    /**
     * Link the local copy of MainApp with the singleton.
     * @param main
     */
    public void setMainApp(Main main) {
        this.mainApp = main;
    }

    /**
     * Sets the information of the existing appointments
     * @param a: existing appointment.
     */
    public void setField(Appointment a){
        app=a;
        serviceField.setText(a.getService());
        timeField.setText(a.getDuration().toString());
        mainApp.saveStaff(app.getStaffId());
        mainApp.saveClient(app.getClientId());
    }
    //Saves the selected staff inside local variables
    @FXML
    private void handleStaffSelect() throws IOException, SQLException {
        mainApp.showAppStaff();
        if (mainApp.getSavedStaff() != 0) {
            selectedStaff = staffDao.select(mainApp.getSavedStaff());
            staffName.setText(selectedStaff.getName());
            staffSurname.setText(selectedStaff.getSurname());
        }
    }
    // Checks if all fields are filled and calls the function to show all available time slots.
    @FXML
    private void handleSave() throws SQLException, IOException {
        mainApp.saveService(serviceField.getText());
        dao.softDelete(app.getId());
        try {
            mainApp.saveDuration(DataUtil.parseToDuration(timeField.getText(), true));
        } catch (DateTimeException e) {
            mainApp.errorAlert("Error",
                    "Time Error",
                    "Please insert valid duration");
        } catch (IllegalArgumentException e) {
            mainApp.errorAlert("Error",
                    "Format error",
                    "Please insert correct duration format. Expected hh:mm or mm.");
        }
        if (serviceField.getText().isEmpty() || timeField.getText().isEmpty()
                || mainApp.getSavedService().isEmpty() || mainApp.getSavedDuration() == null) {
            mainApp.errorAlert("Error",
                    "Module error",
                    "Please fill all the fields");
        } else {

            appInsert();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    //Saves the selected client inside local variables.
    @FXML
    private void handleClientSelect() throws IOException, SQLException {
        mainApp.showAppClient();
        if (mainApp.getSavedClient() != 0) {
            selectedClient = DAOClient.select(mainApp.getSavedClient());
            clientName.setText(selectedClient.getName());
            clientSurname.setText(selectedClient.getSurname());
        }
    }
    //Helping function to prepare available dates and time slots.
    private void appInsert() throws IOException, SQLException {
        //Each element of arrayList is linked to a single schedule of scheduleList
        ArrayList<ArrayList<Boolean>> arrayList = new ArrayList<>();
        ArrayList<Schedule> scheduleList = scheduleDao.futureSchedule(mainApp.getSavedStaff());
        //Boolean translation from schedule list
        for (Schedule schedule : scheduleList) {
            arrayList.add(DataUtil.avApp(schedule));
        }
        //dim saves the position of unavailable schedules
        ArrayList<Integer> dim = new ArrayList<Integer>();
        for (int i = 0; i < arrayList.size(); i++) {
            ArrayList<Boolean> temp = DataUtil.avFilter(arrayList.get(i), mainApp.getSavedDuration());
            if (temp == null) {
                dim.add(i);
            }
        }
        //Set null unavailable schedules using dim to find
        //unavailable schedules
        for (int i = 0; i < dim.size(); i++) {
            arrayList.set((int) dim.get(i), null);
        }
        dialogStage.close();
        System.out.println(app.getDate().toString() + app.getTime().toString());
        mainApp.showAvailableAppUp(scheduleList, arrayList,app);
    }

}
