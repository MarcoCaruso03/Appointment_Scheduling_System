package it.unicas.clinic.address.view.schedule;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Schedule;
import it.unicas.clinic.address.model.Staff;
import it.unicas.clinic.address.model.dao.ScheduleDAO;
import it.unicas.clinic.address.model.dao.ScheduleException;
import it.unicas.clinic.address.model.dao.StaffDAO;
import it.unicas.clinic.address.model.dao.mysql.ScheduleDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.StaffDAOMySQLImpl;
import it.unicas.clinic.address.utils.DataUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Controller of the GUI that manages the schedule adding window
 */
public class ScheduleAddingLayoutController {
    @FXML
    private TextField dayField;
    @FXML
    private TextField startHourField;
    @FXML
    private TextField endHourField;


    private Main mainApp;
    private Stage dialogStage;
    private Schedule schedule;
    private Staff staff;
    private ScheduleDAO dao= ScheduleDAOMySQLImpl.getInstance();

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        // Set the dialog icon.
        //this.dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
    }

    /**
     * Link the local copy of MainApp with the singleton.
     * @param main: singleton main
     * @param s: staff
     */
    public void setMainApp(Main main, Staff s) {
        this.staff = s;
        this.staff.setId(s.getId());//in questo modo ho l'id dello staff che ho selezionato nella finestra precedente
        this.mainApp = main;
    }

    //Effectively adds the schedule in the database (it previously checks the fields)
    @FXML
    private void handleSave(){
            try {
                if(dayField.getText().equals("") || startHourField.getText().equals("") || endHourField.getText().equals("")){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error");
                    alert.setContentText("Fill all the fields!!!!!");
                    alert.showAndWait();
                    return;
                }
                LocalDate d = LocalDate.parse(dayField.getText());
                LocalTime st = DataUtil.parseToTime(startHourField.getText(),true);
                LocalTime et = DataUtil.parseToTime(endHourField.getText(),true);
                this.schedule = new Schedule(d, st, et, staff.getId());

                if (Schedule.verifySchedule(this.schedule) /*&& !Schedule.isEmpty(this.schedule)*/) {
                    //aggiungo nel db e nalla lista
                    try {
                        dao.insert(this.schedule);
                        //devo recuperare l'id associato a questo schedule, quindi recupero l'ultimo
                        mainApp.getScheduleData().add(dao.getLastSchedule());
                        dialogStage.close();
                    } catch (ScheduleException e) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Database Error");
                        errorAlert.setHeaderText("Could not insert schedule");
                        errorAlert.setContentText("An error occurred while trying to insert schedule-");
                        errorAlert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error-logic");
                    alert.setHeaderText("Error");
                    alert.setContentText("Errore logico nelle ore");
                    alert.showAndWait();
                }
            } catch (IllegalArgumentException | DateTimeException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error-Format");
                alert.setHeaderText("Error"); //argomenti passati
                alert.setContentText("Format");
                alert.showAndWait();
            }

    }


    @FXML
    private void handleCancel(){
        dialogStage.close();
    }


}
