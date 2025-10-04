package it.unicas.clinic.address.view.login;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Staff manager GUI. It provides 3 sections to select
 * - Client section to manage clients
 * - Appointments section to manage appointments
 * - Calendar section to see appointments in calendar mode
 */
public class StaffMemberInitialLayoutController {

    private Main main;

    @FXML
    private ImageView clientBackground;
    @FXML
    private ImageView appointmentBackground;
    @FXML
    private ImageView calendarBackground;
    @FXML
    private ImageView todayApp;


    private AppointmentDAO appointmentDAO = AppointmentDAOMySQLImpl.getInstance();
    private boolean found;

    /**
     * Is called by the main application to give a reference back to itself.
     * @param main: application
     */
    public void setMainApp(Main main) {
        this.main = main;
    }


    @FXML
    private void initialize(){
    }

    //Return to login view
    @FXML
    private void handleLogout(){

        //Alert to go back to login GUI
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("Click "+"\n"+"'Yes' to logout"+"\n"+"'Back' to close the window");

        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeCancel = new ButtonType("Back", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            main.initLogin();
            main.setIsManager(null);
            main.setUser_id(0);
        }

    }
    //Methods to highlight sections only when mouse passes on them
    @FXML
    private void handleClientHighlight(){
        clientBackground.setOpacity(0.50);
    }
    @FXML
    private void handleAppointmentsHighlight(){
        appointmentBackground.setOpacity(0.50);
    }
    @FXML
    private void handleCalendarHighlight(){
        calendarBackground.setOpacity(0.50);
    }
    @FXML
    private void handleClientDownlight(){
        clientBackground.setOpacity(0.25);
    }
    @FXML
    private void handleAppointmentsDownlight(){
        appointmentBackground.setOpacity(0.25);
    }
    @FXML
    private void handleCalendarDownlight(){
        calendarBackground.setOpacity(0.25);
    }

    //Pops up the Client management table
    @FXML
    private void handleClientView() throws SQLException, IOException {
        main.showClientView();
    }
    //Pops up the Password change window
    @FXML
    private void handleChangePassword() throws SQLException, IOException {
        main.changePassword();
    }
    //Pops up the Username change window
    @FXML
    private void handleChangeUsername() throws IOException {
        main.changeUsername();
    }
    //Pops up the Appointment management table
    @FXML
    private void handleApp(){
        main.initAppView();
    }
    //Pops up the Calendar view
    @FXML
    private void handleCalendar() throws SQLException, IOException {
        main.showMonthlyView();
    }
    //Pops up the notification area (daily appointment of logged-in user)
    @FXML
    private void notificationArea() throws IOException {
        if(found) {
            main.showDailyView2();
        }
        else{
            main.infoAlert("Attention",
                    "You have no appointments today",
                    "");
        }
    }
    //Set icon to notify the presence of daily appointments
    public void setIcon() {
        //numero di appuntamenti cancellati oggi
        int count = 0;
        //if the staff has some appointmetns for the day than => red circle (busy) otherwise green circle (is free)
        //take all the appointment for the day (select restituisce gli appuntamenti che non sono stati cancellati
        List<Appointment> list = appointmentDAO.select(new Appointment(null, null, LocalDate.now(), null, null, main.getUser_id(), null));
        //se non ci sono appuntamenti oggi=> libero (green)
        if (list.isEmpty()) {
            Image image = new Image("it/unicas/clinic/address/view/login/green_circle.png");
            todayApp.setImage(image);
            found=false;
            return;
        }
        else{
            Image image = new Image("it/unicas/clinic/address/view/login/red_circle.png");
            todayApp.setImage(image);
            found=true;
            return;
        }
    }

}
