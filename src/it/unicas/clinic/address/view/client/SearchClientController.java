package it.unicas.clinic.address.view.client;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Client;
import it.unicas.clinic.address.model.dao.mysql.DAOClient;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;


public class SearchClientController {

    public ClientOverviewController clientOverviewController;

    public void SetClientOverviewController(ClientOverviewController clientOverviewController) {
        this.clientOverviewController = clientOverviewController;
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
    private Button searchButton;

    private Main mainApp;

    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void OnClickSearchButton(ActionEvent event) throws SQLException {
       if(isInputValid())
       {
           ArrayList<Client> list;
           String name = nameText.getText();
           String surname = surnameText.getText();
           String email = emailText.getText();

           list = DAOClient.filterClient(name, surname, email);

           clientOverviewController.updateTable(list);
           stage.close();
       }

    }

    //Checks all the fields
    private boolean isInputValid(){
        if(nameText.getText().isEmpty() && surnameText.getText().isEmpty() && emailText.getText().isEmpty()){
            String errorMessage = "Insert at least one field!";

                // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;


        }

        return true;
    }


}
