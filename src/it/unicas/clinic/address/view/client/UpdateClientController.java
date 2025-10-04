package it.unicas.clinic.address.view.client;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Client;
import it.unicas.clinic.address.model.dao.mysql.DAOClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Controller of the GUI that manages client update
 */
public class UpdateClientController {

    private Main mainApp;

    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public ClientOverviewController clientOverviewController;
    public void SetClientOverviewController(ClientOverviewController clientOverviewController) {
        this.clientOverviewController = clientOverviewController;
    }

    private int id;
    public void getClient(Client client) {

        id = client.getId();
        nameText.setText(client.getName());
        surnameText.setText(client.getSurname());
        emailText.setText(client.getEmail());
        numberText.setText(client.getNumber());
    }

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private TextField nameText;

    @FXML
    private TextField surnameText;

    @FXML
    private TextField emailText;

    @FXML
    private TextField numberText;


    //Effectively update the client
    @FXML
    public void OnClickUpdate(ActionEvent event) throws SQLException {

        if(isInputValid())
        {
            String name = nameText.getText();
            String surname = surnameText.getText();
            String email = emailText.getText();
            String number = numberText.getText();

            DAOClient.update(id, name, surname, email, number);

            ArrayList<Client> clients = DAOClient.getClientsList();

            clientOverviewController.updateTable(clients);

            stage.close();
        }

    }

    //Checks all the fields
    private boolean isInputValid(){
        String errorMessage = "";
        if(nameText.getText() == null || nameText.getText().length() == 0) {
            errorMessage += "Name cannot be empty!\n";
        }
        if(surnameText.getText() == null || surnameText.getText().length() == 0) {
            errorMessage += "Surname cannot be empty!\n";
        }
        if(emailText.getText() == null || emailText.getText().length() == 0) {
            errorMessage += "Email cannot be empty!\n";
        }
        if(numberText.getText() == null || numberText.getText().length() == 0) {
            errorMessage += "Number cannot be empty!\n";
        }
        if(errorMessage.length() == 0) {
            return true;
        }
        else {
            // Show the error message.
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
