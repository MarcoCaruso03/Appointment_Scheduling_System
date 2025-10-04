package it.unicas.clinic.address.view.appointment;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class SearchAppointmentController {

    private Main mainApp;
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private TextField clientTxt;

    @FXML
    private TextField staffTxt;

    @FXML
    private TextField dateTxt;

    @FXML
    private void handleSearchAppointment() throws SQLException {

        AppointmentDAO dao= AppointmentDAOMySQLImpl.getInstance();

        mainApp.getAppointmentData().clear();
        mainApp.getAppointmentData().addAll(dao.select(new Appointment(0,null, LocalDate.parse(dateTxt.getText()), null,null, null, 0)));


        stage.close();
        //Modificare il DAO addinché sia possibile filtrare gli appointment per
        //1) CLiente (cognome)
        //2) Staff (cognome)
        //3) Data (YY-MM-DD)
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }
}
