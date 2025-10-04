package it.unicas.clinic.address.view.appointment;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Client;
import it.unicas.clinic.address.model.dao.mysql.DAOClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Controller of the GUI in order to choose the client for the appointment.
 */
public class AppClientViewController {

    // Table
    @FXML
    private TableView<Client> table;

    @FXML
    private TableColumn<Client, String> name;

    @FXML
    private TableColumn<Client, String> surname;

    @FXML
    private TableColumn<Client, String> email;

    @FXML
    private TableColumn<Client, String> number;
    @FXML
    private TextField clientName;
    @FXML
    private TextField clientSurname;


    @FXML
    private void initialize() {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        surname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        number.setCellValueFactory(new PropertyValueFactory<>("number"));
    }

    private ObservableList<Client> clientData = FXCollections.observableArrayList();
    private Stage dialogStage;
    private Main mainApp;

    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp: singleton MainApp.
     * @throws SQLException
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    //Saves the selected client in the mainApp.
    @FXML
    private void handleSelect(){
        if(isAvailable()){
            Client client = table.getSelectionModel().getSelectedItem();
            mainApp.saveClient(client.getId());
        }
        dialogStage.close();

    }
    @FXML
    private void handleCancel(){
        dialogStage.close();
    }

    //Implements the filter for the client
    @FXML
    private void handleFilter() throws SQLException {
        String name = clientName.getText();
        String surname = clientSurname.getText();
        if(name.equals("") && surname.equals("")){
            ArrayList<Client> list = DAOClient.getClientsList();
            updateTable(list);
        }
        else{
            ArrayList<Client> list = DAOClient.select(name,surname);
            updateTable(list);
        }
    }


    public void updateTable(ArrayList<Client> clients) {
        clientData.clear();
        clientData.addAll(clients);
        table.setItems(clientData);
    }

    //Reset filter
    public void ShowAllClients() throws SQLException {
        ArrayList<Client> clients = DAOClient.getClientsList();

        clientData.clear();
        clientData.addAll(clients);

        table.setItems(clientData);
    }

    private boolean isAvailable() {
        String errorMessage = "";
        if(table.getSelectionModel().getSelectedItem() == null) {
            errorMessage = "Select a client first!";
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            //    alert.initOwner();
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
        return true;
    }

}
