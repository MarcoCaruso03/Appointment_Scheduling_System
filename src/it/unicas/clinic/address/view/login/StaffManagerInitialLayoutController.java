package it.unicas.clinic.address.view.login;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Staff;
import it.unicas.clinic.address.model.dao.StaffDAO;
import it.unicas.clinic.address.model.dao.mysql.StaffDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Staff manager GUI. It provides 4 sections to select
 * - Client section to manage clients
 * - Staff section to manage staff members
 * - Appointments section to manage appointments
 * - Calendar section to see appointments in calendar mode
 */
public class StaffManagerInitialLayoutController {

    private Main main;

    boolean clickStaff = false;

    private Staff chosenStaff = null;

    private static StaffDAO daoStaff = StaffDAOMySQLImpl.getInstance();

    @FXML
    private ImageView clientBackground;
    @FXML
    private ImageView staffBackground;
    @FXML
    private ImageView appointmentBackground;
    @FXML
    private ImageView calendarBackground;

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
    //Return to the login view
    @FXML
    private void handleLogout(){
        //Alert to return in the login GUI
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
        }
    }
    //Methods to highlight the section only when mouse passes on them
    @FXML
    private void handleClientHighlight(){
        clientBackground.setOpacity(0.50);
    }
    @FXML
    private void handleStaffHighlight(){
        staffBackground.setOpacity(0.50);
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
    private void handleStaffDownlight(){
        staffBackground.setOpacity(0.25);
    }
    @FXML
    private void handleAppointmentsDownlight(){
        appointmentBackground.setOpacity(0.25);
    }
    @FXML
    private void handleCalendarDownlight(){
        calendarBackground.setOpacity(0.25);
    }

    //Pops up the Staff management table
    @FXML
    private void handleStaffManagerView() throws IOException {
        main.loadStaffManagement();
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
    private void handleChangeUsername() throws SQLException, IOException {
        main.changeUsername();
    }
    //Pops up the Appointment management table
    @FXML
    private void handleApp(){
        main.initAppView();
    }
    //Pops up the Report window
    @FXML
    private void handleReport() throws IOException {main.openReport();}
    //Pops up the Calendar view
    @FXML
    private void handleCalendar() throws SQLException, IOException {
        main.showMonthlyView();
    }
    //Pops up the credential edit window (only for staff manager)
    @FXML
    private void handleEditStaffCredential() throws IOException, SQLException {
        clickStaff=true;

        int staffId;

        main.showAppStaff();
        if (main.getSavedStaff() != 0) {
            chosenStaff = daoStaff.select(main.getSavedStaff());
            main.EditStaffCredential(chosenStaff.getId());
        }


    }

}
