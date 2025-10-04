package it.unicas.clinic.address.view.staff;

import it.unicas.clinic.address.Main;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller of the GUI that manages the top bar of the home view
 */
public class ChooseOwnerLayoutController {

    private Main main;

    /**
     * Link the local copy of MainApp with the singleton.
     * @param main: singleton MainApp.
     * @throws SQLException
     */
    public void setMain(Main main) {
        this.main = main;
    }
    //Pops up the Staff selection window
    @FXML
    private void handleChooseStaff() throws IOException {
        main.loadStaffManagement();
    }

    @FXML
    private void handleChooseSchedule()throws IOException {
        //main.loadScheduleManagement();
    }
    //Close window
    @FXML
    private void handleBack(){
        main.initStaffManager();
    }
}
