package it.unicas.clinic.address.view.appointment.calendarView;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class MonthlyViewController {

    private Main mainApp;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private LocalDate date;

    private AppointmentDAO dao = AppointmentDAOMySQLImpl.getInstance();

    private int counter = 0;

    private ArrayList<Label> labelList = new ArrayList<>();

    private int clientId = 0;

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    private int staffId = 0;

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    private String service = null;

    public void setService(String service) {
        this.service = service;
    }

    int rows = 6;
    int cols = 7;

    private int month;

    private int year;

    @FXML
    private BorderPane borderPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label dateLabel;

    @FXML
    private Label MondayLabel;
    @FXML
    private Label TuesdayLabel;
    @FXML
    private Label WednesdayLabel;
    @FXML
    private Label ThursdayLabel;
    @FXML
    private Label FridayLabel;
    @FXML
    private Label SaturdayLabel;
    @FXML
    private Label SundayLabel;

    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp: singleton MainApp.
     * @throws SQLException
     */
    public void setMainApp(Main mainApp) throws SQLException {
        this.mainApp = mainApp;
        init2();
    }



    @FXML
    private void initialize() throws Exception {
        date = LocalDate.now();

        month = date.getMonth().getValue();

        year = date.getYear();

        dateLabel.setText(Month.of(month) + " " + year);

  //      LocalDate firstDay = LocalDate.of(year, month, 1);

    }

    //Creates the days' grid and highlights all the days with at least one appointment (no filter)
    private void init2() throws SQLException {


        boolean first = true;

        LocalDate firstDay = LocalDate.of(year, month, 1);

        int day = firstDay.getDayOfWeek().getValue();


        for(int i = 0 ; i < rows ; i++) {
            for(int j = 0 ; j < cols ; j++) {
                if(firstDay.getMonth().getValue() != month)
                {
                    break;
                }
                if(first) {
                    j = day - 1;
                    first = false;
                }

                labelList.add(new Label(firstDay.getDayOfMonth() + ""));
                labelList.get(counter).setAlignment(Pos.CENTER);

                if(!mainApp.getIsManager()) {
                    staffId = mainApp.getUser_id();
                }

                if(!AppointmentDAOMySQLImpl.getInstance().select(new Appointment(0, service, firstDay, null, null, staffId,clientId)).isEmpty()){
                    labelList.get(counter).setStyle("-fx-background-color: lightblue; -fx-padding: 5px; -fx-text-fill: black; -fx-font-size: 12px;");
                    LocalDate finalFirstDay = firstDay;
                    labelList.get(counter).setOnMouseClicked(event -> {
                        showAppointment(finalFirstDay.getDayOfMonth(), month, finalFirstDay.getYear());
                    });
                }

                gridPane.setHalignment(gridPane, HPos.CENTER);
                gridPane.setValignment(gridPane, VPos.CENTER);
                gridPane.add(labelList.get(counter), j, i);

                counter ++;
                firstDay = firstDay.plusDays(1);
            }
        }
    }

    //Highlights only the days with appointments that satisfy the conditions set in the filter.
    public void filter() throws SQLException {
        for(int i  = 0 ; i < counter ; i ++) {
            labelList.get(i).setText("");
            labelList.get(i).setStyle("-fx-background-color: transparent; -fx-padding: 5px; -fx-text-fill: black; -fx-font-size: 12px;");
            labelList.get(i).setOnMouseClicked(null);
        }

        init2();
    }

    //Helping function to select the appointments in a specific day.
    private void showAppointment(int day, int month, int year) {
       // Aggiungere un pulsante per filtrare gli appointment tramite data
       // e aprire la finestra da qui, chiamando la funzione subito dopo
       AppointmentDAO dao = AppointmentDAOMySQLImpl.getInstance();

        mainApp.getAppointmentData().clear();

       if (mainApp.getIsManager()) {
           mainApp.getAppointmentData().addAll(dao.select(new Appointment(0,service, LocalDate.of(year, month, day), null, null, staffId, clientId)));
       } else {
           mainApp.getAppointmentData().addAll(dao.select(new Appointment(0,service, LocalDate.of(year, month, day), null, null, mainApp.getUser_id(), clientId)));
       }
        mainApp.showCalendarAppView();
    }

    @FXML
    private void handleWeeklyView() throws IOException {
        mainApp.showWeeklyView();
        stage.close();

    }

    @FXML
    private void handleDailyView() throws IOException {
        mainApp.showDailyView();
        stage.close();
    }

    //Pops up the filter window
    @FXML
    private void handleFilter() throws IOException {

        service = null;
        clientId = 0;
        staffId = 0;

        if(mainApp.getIsManager()) {
            mainApp.filterCalendarViewForManager(this, null, null);
        } else {
            mainApp.filterCalendarViewForMember(this, null, null);
        }
    }

    //Change to next day
    @FXML
    private void handleRightArrow() throws SQLException {
        month ++;

        if(month == 13) {
            month = 1;
            year++;
        }

        for(int i = 0; i < labelList.size(); i++) {
            //labelList.get(i).setText("");
            labelList.get(i).setStyle("-fx-background-color: transparent; -fx-padding: 10px; -fx-text-fill: black; -fx-font-size: 16px;");
            labelList.get(i).setOnMouseClicked(null);
        }

        gridPane.getChildren().clear();


        dateLabel.setText(Month.of(month) + " " + year);

        init2();
    }

    // Change to previous day
    @FXML
    private void handleLeftArrow() throws SQLException {
        month --;

        if(month == 0) {
            month = 12;
            year--;
        }

        for(int i = 0; i < labelList.size(); i++) {
        //    labelList.get(i).setText("");
            labelList.get(i).setStyle("-fx-background-color: transparent; -fx-padding: 10px; -fx-text-fill: black; -fx-font-size: 16px;");
            labelList.get(i).setOnMouseClicked(null);
        }

        gridPane.getChildren().clear();


        dateLabel.setText(Month.of(month) + " " + year);

        init2();

    }

    //Close calendar
    @FXML
    private void handleClose() {
        stage.close();
    }
}
