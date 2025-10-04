package it.unicas.clinic.address.view;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.dao.StaffDAO;
import it.unicas.clinic.address.model.dao.mysql.StaffDAOMySQLImpl;
import javafx.stage.Stage;

public class ScheduleAddingLayoutController {


    private Main mainApp;
    private Stage dialogStage;
    private StaffDAO dao= StaffDAOMySQLImpl.getInstance();

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        // Set the dialog icon.
        //this.dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
    }
    public void setMainApp(Main main) {
        this.mainApp = main;
    }
}
