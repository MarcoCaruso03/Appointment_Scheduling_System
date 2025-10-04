package it.unicas.clinic.address.view.client;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Client;
import it.unicas.clinic.address.model.dao.mysql.DAOClient;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Controller of the GUI that manages the insertion of a client
 */
public class AddClientController {
    public ClientOverviewController clientOverviewController;

    public void SetClientOverviewController(ClientOverviewController clientOverviewController) {
        this.clientOverviewController = clientOverviewController;
    }

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Main mainApp;

    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp: singleton MainApp.
     * @throws SQLException
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private TextField nameText;

    @FXML
    private TextField surnameText;

    @FXML
    private TextField emailText;

    @FXML
    private TextField phoneNumberText;

    //Check if fields are filled and then insert the client in the database
    @FXML
    private void OnClickAddClient() throws SQLException {

        if(isInputValid())
        {
            String name = nameText.getText();
            String surname = surnameText.getText();
            String email = emailText.getText();
            String phoneNumber = phoneNumberText.getText();

            DAOClient.insert(name, surname, email, phoneNumber);
            ArrayList<Client> list = DAOClient.getClientsList();

            clientOverviewController.updateTable(list);

            stage.close();
        }


    }

    //Checks the fields
    private boolean isInputValid() {
        String errorMessage = "";
        if (nameText.getText() == null || nameText.getText().length() == 0) {
            errorMessage += "Name cannot be empty!\n";
        }
        if (surnameText.getText() == null || surnameText.getText().length() == 0) {
            errorMessage += "Surname cannot be empty!\n";
        }
        if (emailText.getText() == null || emailText.getText().length() == 0) {
            errorMessage += "Email cannot be empty!\n";
        }
        if (phoneNumberText.getText() == null || phoneNumberText.getText().length() == 0) {
            errorMessage += "Phone number cannot be empty!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}
