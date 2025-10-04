package it.unicas.clinic.address.view.appointment.calendarView;

import it.unicas.clinic.address.Main;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class RootViewLayoutController {
    private Main mainApp;
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleDailyView() {

    }
}
