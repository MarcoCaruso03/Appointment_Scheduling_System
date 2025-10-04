package it.unicas.clinic.address.view.appointment.calendarView;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class DailyView2Controller {
    private Main mainApp;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private AppointmentDAO dao = AppointmentDAOMySQLImpl.getInstance();

    private LocalDate today = LocalDate.now();

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

    private ArrayList<Label> labelList = new ArrayList<>();

    private ArrayList<LocalTime> hours = new ArrayList<>();

    @FXML
    private GridPane gridPane;

    @FXML
    private Label dayLabel;


    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp: singleton MainApp.
     * @throws SQLException
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        init2();
    }

    //Creates the days' grid and highlights all the days with at least one appointment (no filter)
    private void init2() {
        dayLabel.setText(today.toString());

        int counter = 0;

        if(mainApp.getIsManager()) {
            staffId = 0;
        } else {
            staffId = mainApp.getUser_id();
        }

        for(int i = 0; i < hours.size(); i++) {
            ArrayList<Appointment> list2 = (ArrayList<Appointment>) dao.select(new Appointment(0, service, today, hours.get(i), null, staffId, clientId));
            if(list2.size() > 0) {
                for(int k = 0; k < list2.size(); k++) {
                    LocalTime duration = list2.get(k).getDuration();
                    int n = (duration.getHour() * 60 + duration.getMinute())/30;

                    for(int j = 1; j <= n; j++) {
                        Label label = new Label(list2.get(k).getService());
                        labelList.add(label);
                        labelList.get(counter).setStyle("-fx-background-color: lightblue; -fx-padding: 5px; -fx-text-fill: black; -fx-font-size: 12px;");

                        int tempIndex = i;
                        labelList.get(counter).setOnMouseClicked(event -> {
                            showAppointment(today.getDayOfMonth(), today.getMonthValue(), today.getYear(), hours.get(tempIndex));
                        });
                        gridPane.add(label, 1, i + j);

                        counter ++;
                    }
                }

            }
        }
    }



    @FXML
    private void initialize() {
        for(int i = 0; i < 24; i++) {
            hours.add(LocalTime.of(i, 0));
            hours.add(LocalTime.of(i, 30));
        }


        for (int i = 0; i < hours.size(); i++) {
            javafx.scene.control.Label hourLabel = new javafx.scene.control.Label(hours.get(i).toString());
            gridPane.add(hourLabel, 0, i);
        }

    }

    //Highlights only the days with appointments that satisfy the conditions set in the filter.
    public void filter() {
        for(int i = 0; i < labelList.size(); i++) {
            labelList.get(i).setStyle("-fx-background-color: transparent;");
            labelList.get(i).setText("");
            labelList.get(i).setOnMouseClicked(null);
        }

        labelList.removeAll(labelList);

        int counter = 0;

        for(int i = 0; i < hours.size(); i++) {
            ArrayList<Appointment> list2 = (ArrayList<Appointment>) dao.select(new Appointment(0, service, today, hours.get(i), null, staffId, clientId));
            if(list2.size() > 0) {
                for(int k = 0; k < list2.size(); k++) {
                    LocalTime duration = list2.get(k).getDuration();
                    int n = (duration.getHour() * 60 + duration.getMinute())/30;

                    for(int j = 1; j <= n; j++) {
                        Label label = new Label(list2.get(k).getService());
                        labelList.add(label);
                        labelList.get(counter).setStyle("-fx-background-color: lightblue; -fx-padding: 5px; -fx-text-fill: black; -fx-font-size: 12px;");

                        int tempIndex = i;
                        labelList.get(counter).setOnMouseClicked(event -> {
                            showAppointment(today.getDayOfMonth(), today.getMonthValue(), today.getYear(), hours.get(tempIndex));
                        });
                        gridPane.add(label, 1, i + j);

                        counter ++;
                    }
                }

            }
        }

        /*clientId = 0;
        staffId = 0;
        service = null;*/
    }

    //Helping function to select the appointments in a specific day.
    private void showAppointment(int day, int month, int year, LocalTime hour) {
        // Aggiungere un pulsante per filtrare gli appointment tramite data
        // e aprire la finestra da qui, chiamando la funzione subito dopo
        AppointmentDAO dao= AppointmentDAOMySQLImpl.getInstance();
        mainApp.getAppointmentData().clear();

        String day2 = "";
        String ymd = "";
        if(day < 10) {
            String temp = "" + day;
            day2 = "0" + day;
            ymd = year + "-" + month + "-" + day2;
        } else {
            ymd = year + "-" + month + "-" + day;

        }

        mainApp.getAppointmentData().addAll(dao.select(new Appointment(0,service, LocalDate.parse(ymd), hour,null, staffId, clientId)));
        mainApp.showCalendarAppView();

    }

    @FXML
    private void handleWeeklyView() throws IOException {
        mainApp.showWeeklyView();
        stage.close();

    }

    @FXML
    private void handleMonthlyView() throws IOException, SQLException {
        mainApp.showMonthlyView();
        stage.close();
    }

    //Pops up the filter window
    @FXML
    private void handleFilter() throws IOException {
        if(mainApp.getIsManager()) {
            mainApp.filterCalendarViewForManager(null, null, this);
        } else {
            mainApp.filterCalendarViewForMember(null, null, this);
        }
    }
    //Change to next day
    @FXML
    private void handleRightArrow() {
        today = today.plusDays(1);

        for(int i = 0 ; i < labelList.size(); i++) {
            labelList.get(i).setText("");
            labelList.get(i).setStyle("-fx-background-color: transparent;");
            labelList.get(i).setOnMouseClicked(null);
        }

        labelList.removeAll(labelList);

        dayLabel.setText(today.getDayOfMonth() + "");
        init2();
    }
    // Change to previous day
    @FXML
    private void handleLeftArrow() {
        today = today.minusDays(1);

        for(int i = 0 ; i < labelList.size(); i++) {
            labelList.get(i).setText("");
            labelList.get(i).setStyle("-fx-background-color: transparent;");
            labelList.get(i).setOnMouseClicked(null);
        }

        labelList.removeAll(labelList);

        dayLabel.setText(today.getDayOfMonth() + "");
        init2();

    }
    //Close calendar
    @FXML
    private void handleClose() {
        stage.close();
    }
}
