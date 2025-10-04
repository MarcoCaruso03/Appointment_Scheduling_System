package it.unicas.clinic.address.view.schedule;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Schedule;
import it.unicas.clinic.address.model.Staff;
import it.unicas.clinic.address.model.dao.ScheduleDAO;
import it.unicas.clinic.address.model.dao.ScheduleException;
import it.unicas.clinic.address.model.dao.mysql.ScheduleDAOMySQLImpl;
import it.unicas.clinic.address.utils.DataUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Controller of the GUI that manages the schedule update window
 */
public class ScheduleUpdateLayoutController {
    @FXML
    private TextField dayField;
    @FXML
    private TextField startTimeField;
    @FXML
    private TextField stopTimeField;


    private Stage dialogStage;
    private Main mainApp;
    private Staff staff;
    private Schedule schedule;
    private ScheduleDAO dao= ScheduleDAOMySQLImpl.getInstance();

    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp: singleton main
     * @param staff: staff
     */
    public void setMainApp(Main mainApp, Staff staff) {
        this.staff = staff;
        this.mainApp = mainApp;
    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        // Set the dialog icon.
        //this.dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
    }

    //Effectively updates the schedule in the database and forces rescheduling
    @FXML
    private void handleUpdate(){
        try {
            LocalDate d = DataUtil.parseToDate(dayField.getText());
            LocalTime startTime = DataUtil.parseToTime(startTimeField.getText(), true);
            LocalTime stopTime = DataUtil.parseToTime(stopTimeField.getText(), true);
            schedule.setDay(d);
            schedule.setStartTime(startTime);
            schedule.setStopTime(stopTime);
            if(Schedule.verifySchedule(schedule) /*&& !Schedule.isEmpty(schedule)*/) {
                try {
                    //update in the list
                    dao.update(schedule);
                    //update in the list (ricarico)
                    mainApp.getScheduleData().clear();
                    mainApp.getScheduleData().addAll(dao.select(new Schedule(0, null, null, null, staff.getId())));
                    dialogStage.close();
                } catch (ScheduleException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Database Error");
                    errorAlert.setHeaderText("Could not update schedule");
                    errorAlert.setContentText("An error occurred while trying to update schedule-");
                    errorAlert.showAndWait();
                }

            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error-logic");
                alert.setHeaderText("Error");
                alert.setContentText("Errore logico nelle ore");
                alert.showAndWait();
            }
        }catch(IllegalArgumentException | DateTimeException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error-Format");
            alert.setHeaderText("Error"); //argomenti passati
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    private void handleCancel(){
        dialogStage.close();
    }

    //Shows information about existing schedule
    public void setField(Schedule s) {
        schedule = s;
        dayField.setText(s.getDay().toString());
        startTimeField.setText(s.getStartTime().toString());
        stopTimeField.setText(s.getStopTime().toString());
    }
}
