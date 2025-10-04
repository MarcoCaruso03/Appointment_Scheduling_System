package it.unicas.clinic.address.view.appointment;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.model.AppointmentReport;
import it.unicas.clinic.address.model.Staff;
import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.AppointmentException;
import it.unicas.clinic.address.model.dao.ScheduleDAO;
import it.unicas.clinic.address.model.dao.StaffDAO;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.DAOClient;
import it.unicas.clinic.address.model.dao.mysql.ScheduleDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.StaffDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller of the GUI that manages the report generation.
 */
public class ReportLayoutController {
    private static AppointmentDAO daoApp= AppointmentDAOMySQLImpl.getInstance();
    private static StaffDAO daoStaff = StaffDAOMySQLImpl.getInstance();
    @FXML
    private TextField startDatePeriodTF;
    @FXML
    private TextField endDatePeriodTF;
    @FXML
    private TextField serviceTF;

    private Staff choosenStaff = null;
    @FXML
    private Label nameLabel;
    @FXML
    private Label surnameLabel;
    private boolean clickStaff=false;
    private boolean atLeastOneFilter=false;
    private boolean errorDate = false;
    //private String titleTxt="report";
    private Main mainApp;
    private Stage dialogStage;
    String service=null;
    LocalDate startDate = null;
    LocalDate endDate = null;
    int idStaff = 0;

    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
    //Saves the selected staff inside local variables
    @FXML
    private void chooseStaff() throws SQLException, IOException {
        clickStaff=true;
        //set the chosen Staff fields
        mainApp.showAppStaff();
        if (mainApp.getSavedStaff() != 0) {
            choosenStaff = daoStaff.select(mainApp.getSavedStaff());
            //set labels
            nameLabel.setText(choosenStaff.getName());
            surnameLabel.setText(choosenStaff.getSurname());
        }
        mainApp.getStaffData().clear();
    }
    //Generates the txt file for the report
    @FXML
    private void generateTXT() {
        int canc=0;

        List<Appointment> list= getApp();
        clickStaff=false;
        if(list == null) {
            nameLabel.setText("Name");
            surnameLabel.setText("Surname");
            startDatePeriodTF.clear();
            endDatePeriodTF.clear();
            serviceTF.clear();
            atLeastOneFilter = false;
            errorDate = false;
            return;
        }
        if(errorDate){
            mainApp.errorAlert("Report", "Date logic", "The end date must be later than the stat date");
            return;
        }
        if(list.isEmpty()){
            mainApp.errorAlert("Report", "Problem with the generation of the report", "" +
                    "The record will not be generate because there are no appointments which match those filters");
        }
        else{
            if(!atLeastOneFilter) {
                mainApp.warningAlert("Report", "No filter added", "You haven't added any filter. The report" +
                        " will contain all the appointments");
            }
            //create the extended appointment
            List<AppointmentReport> list_report = new ArrayList<>();
            for(Appointment a:list){
                try {
                    list_report.add(new AppointmentReport(a));
                } catch (SQLException e) {
                   mainApp.errorAlert("Report", "Problem with the generation of the report", "" +
                       "SQL ERROR, NOT ABLE TO LOAD APPOINTMENTS. SORRY :(");
                }
                if(a.getCancellation())
                    canc++;
            }
            //generate the TXT
            //stream closes auto because i've used try-with-resources
            try(PrintWriter printWriter = new PrintWriter(generateTitle().concat(".txt"))){
                printWriter.println("*****************************************************************************************************************");
                printWriter.println("Filter by: ");
                printWriter.print("Service: ");
                if(service!=null)
                    printWriter.println(service);
                else
                    printWriter.println("NO");
                printWriter.print("START PERIOD: ");
                if(startDate != null && endDate != null)
                    printWriter.println(startDate+","+endDate);
                else
                    printWriter.println("NO");
                printWriter.print("STAFF: ");
                if(idStaff != 0)
                    printWriter.println(choosenStaff.getName()+","+choosenStaff.getSurname());
                else
                    printWriter.println("NO");
                printWriter.println("*****************************************************************************************************************");
                printWriter.println("ID,SERVICE,DATE,TIME,DURATION,STAFF ID,STAFF NAME," +
                        "STAFF SURNAME,CLIENT ID,CLIENT NAME,CLIENT SURNAME,CANCELLATION");
                printWriter.println("*****************************************************************************************************************");

                for(AppointmentReport appointment: list_report){
                    //printWriter.println(appointment.toString());
                    printWriter.println(appointment.toString());
                }
                printWriter.println("*****************************************************************************************************************");
                printWriter.println("Number of appointments: " + list_report.size());
                printWriter.println("Number of cancellation: " + canc);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Report");
                alert.setHeaderText("Success");
                alert.setContentText("The report has been generated !");
                alert.showAndWait();
                choosenStaff=null;
                //set null field for staff: so if you want to do two consecutive report must reselect the staff
                nameLabel.setText("Name");
                surnameLabel.setText("Surname");
                startDatePeriodTF.clear();
                endDatePeriodTF.clear();
                serviceTF.clear();
                atLeastOneFilter = false;
                errorDate = false;
            }catch (IOException e){
                System.out.println(e);
            }


        }


    }

    //Gets the appointments to insert inside the report
    private List<Appointment> getApp(){
        service=null;
        startDate = null;
        endDate = null;
        idStaff = 0;
        if(!serviceTF.getText().isEmpty()){
            atLeastOneFilter = true;
            service = serviceTF.getText();
        }
        if(!startDatePeriodTF.getText().isEmpty() && !endDatePeriodTF.getText().isEmpty()){
            atLeastOneFilter = true;
            try {
                startDate = LocalDate.parse(startDatePeriodTF.getText());
                endDate = LocalDate.parse(endDatePeriodTF.getText());
            }catch (DateTimeException e){
                mainApp.errorAlert("Report", "Format date", "Insert the correct formal for" +
                        "date: YEAR-MONTH-DAY");
                return null;
            }
        }

        if((endDatePeriodTF.getText().isEmpty() && !startDatePeriodTF.getText().isEmpty())
        || (!endDatePeriodTF.getText().isEmpty() && startDatePeriodTF.getText().isEmpty())){
            mainApp.errorAlert("Error", "Date", "Please insert both dates");
            return null;
        }
        if(clickStaff) {
            atLeastOneFilter = true;
            idStaff = choosenStaff.getId();
        }
        //takes all the appointments filter by date, service and staff
        if(!startDatePeriodTF.getText().isEmpty() && !endDatePeriodTF.getText().isEmpty()
        && endDate.isBefore(startDate)) {
            errorDate = true;
        }
        try{
            List<Appointment> app = daoApp.select(new Appointment(0, service, null, null, null, idStaff, null),
                    startDate, endDate);
            return app;
        }catch (AppointmentException e){
            mainApp.errorAlert("Report", "Problem with the generation of the report", "" +
                    "SQL ERROR, NOT ABLE TO LOAD APPOINTMENTS. SORRY :(");
        }
        return null;

    }


    private String generateTitle(){
        // Inizializzazione della stringa base con il servizio
        String filterString = service != null && !service.isEmpty() ? service : "UnknownService";

        // Concateno la data di inizio, se presente
        if (startDate != null && endDate != null) {
            filterString = filterString.concat("_").concat(startDate.toString()).concat("_").concat(endDate.toString());
        }

        // Concateno la data di fine, se presente
        else {
            filterString = filterString.concat("_").concat("NO_PERIOD");
        }

        // Concateno l'ID dello staff, se presente
        if (idStaff != 0) {
            filterString = filterString.concat("_").concat(String.valueOf(idStaff));
        }
        else
            filterString = filterString.concat("_").concat("NO_STAFF");

        return filterString;
    }



    public static void main(String[] args) {

    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
