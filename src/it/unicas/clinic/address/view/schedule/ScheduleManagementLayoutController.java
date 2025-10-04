package it.unicas.clinic.address.view.schedule;

import it.unicas.clinic.address.Main;
import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.model.Schedule;
import it.unicas.clinic.address.model.Staff;
import it.unicas.clinic.address.model.dao.*;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.DAOClient;
import it.unicas.clinic.address.model.dao.mysql.ScheduleDAOMySQLImpl;
import it.unicas.clinic.address.utils.DataUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Controller of the GUI that manages the general schedule table
 */
public class ScheduleManagementLayoutController {
    @FXML
    private TableView<Schedule> scheduleTable;
    @FXML
    private TableColumn<Schedule, Integer> idColumn;
    @FXML
    private TableColumn<Schedule, LocalDate> dayColumn;
    @FXML
    private TableColumn<Schedule, LocalTime> starTimeColumn;
    @FXML
    private TableColumn<Schedule, LocalTime> endTimeColumn;
    @FXML
    private TableColumn<Schedule, Integer> staffidColumn;

    private Main mainApp;
    private ScheduleDAO dao= ScheduleDAOMySQLImpl.getInstance();
    private AppointmentDAO appDao = AppointmentDAOMySQLImpl.getInstance();
    private Schedule schedule;
    private Staff staff; //staff selezionato nella StaffManagementLayout, del quale mostrare gli schedule

    /**
     * Link the local copy of MainApp with the singleton.
     * @param mainApp: singleton main
     * @param s: staff
     */
    public void setMainApp(Main mainApp, Staff s) {
        this.mainApp = mainApp;
        // Add observable list data to the table
        scheduleTable.setItems(mainApp.getScheduleData());
        staff=s;
        staff.setId(s.getId());
        mainApp.getScheduleData().addAll(dao.select(new Schedule(0, null, null, null, staff.getId())));
    }
    @FXML
    private void initialize() {
        //nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        //surnameColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        //specColumn.setCellValueFactory(cellData -> cellData.getValue().specialtiesProperty());
        //idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        starTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("stopTime"));
        staffidColumn.setCellValueFactory(new PropertyValueFactory<>("staffId"));


    }
    //Pops up the insert schedule window
    @FXML
    private void handleInsertSchedule() {
        mainApp.showScheduleInsertDialog(staff);

    }
    //Effectively deletes the schedule in the database and forces rescheduling
    @FXML
    private void handleDeleteSchedule() {
        //check if there is a selected schedule
        int selectedIndex = scheduleTable.getSelectionModel().getSelectedIndex();
        Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();

        if(selectedSchedule != null && selectedSchedule.getDay().isAfter(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Delete a Staff Member");
            alert.setHeaderText("Do you want to delete this schedule? "+selectedSchedule.getDay().toString());
            alert.setContentText("Do you want to delete this schedule?");
            ButtonType buttonTypeOne = new ButtonType("Yes");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {
                try{
                    mainApp.getScheduleData().remove(selectedSchedule);
                    ArrayList<Appointment> schedApp = appDao.getSchedApp(selectedSchedule);
                    dao.delete(selectedSchedule);
                    boolean empty=false;
                    if(schedApp!=null) {
                        //mainApp.getPrimaryStage().close();
                        for (Appointment app : schedApp) {
                            rescheduleApp(app);
                        }
                        dao.delete(selectedSchedule);
                    }

                    Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                    errorAlert.setTitle("Success");
                    errorAlert.setHeaderText("Schedule deleted successfully");
                    errorAlert.showAndWait();


                }catch(ScheduleException e){
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Database Error");
                    errorAlert.setHeaderText("Could not delete schedule");
                    errorAlert.setContentText(e.getMessage());
                    errorAlert.showAndWait();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else if(selectedSchedule != null && !selectedSchedule.getDay().isAfter(LocalDate.now())) {
            mainApp.warningAlert("Attention",
                    "You can't delete past schedule",
                    "Please select a valid schedule");
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Schedule Selected");
            alert.setContentText("Please select a Schedule into the table.");
            alert.showAndWait();
        }
    }
    //Pops up the update schedule window
    @FXML
    private void handleUpdateSchedule() {
        //take the staff del quale dovrò mostrare gli schedule
        Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
        if(selectedSchedule != null && selectedSchedule.getDay().isAfter(LocalDate.now())) {
            mainApp.showScheduleUpdateDialog(selectedSchedule, staff);
        }
        else if(selectedSchedule != null && !selectedSchedule.getDay().isAfter(LocalDate.now())) {
            mainApp.warningAlert("Attention",
                    "You can't update a past schedule",
                    "Please select a valid schedule");
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Schedule Selected");
            alert.setContentText("Please select a Schedule into the table.");

            alert.showAndWait();
        }

    }
    //Helping function to force rescheduling
    private void rescheduleApp(Appointment app) throws IOException, SQLException {
            appDao.softDelete(app.getId());
            //Each element of arrayList is linked to a single schedule of scheduleList
            ArrayList<ArrayList<Boolean>> arrayList = new ArrayList<>();
            ArrayList<Schedule> scheduleList = dao.futureSchedule(app.getStaffId());
            //Boolean translation from schedule list
            for (Schedule schedule : scheduleList) {
                arrayList.add(DataUtil.avApp(schedule));
            }
            //dim saves the position of unavailable schedules
            ArrayList<Integer> dim = new ArrayList<Integer>();
            for (int i = 0; i < arrayList.size(); i++) {
                ArrayList<Boolean> temp = DataUtil.avFilter(arrayList.get(i), app.getDuration());
                if (temp == null) {
                    dim.add(i);
                }
            }
            //Set null unavailable schedules using dim to find
            //unavailable schedules
            for (int i = 0; i < dim.size(); i++) {
                arrayList.set((int) dim.get(i), null);
            }
            mainApp.saveService(app.getService());
            mainApp.saveDuration(app.getDuration());
            mainApp.saveClient(app.getClientId());
            mainApp.saveStaff(app.getStaffId());
            if(scheduleList.isEmpty() || arrayList.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No schedules found");
                alert.setHeaderText("The following appointment will be deleted");
                alert.setContentText("Service: "+app.getService()+"\n"+ "Client: "
                        +DAOClient.select(app.getClientId()).getName()+" "+DAOClient.select(app.getClientId()).getSurname());

                ButtonType buttonTypeOne = new ButtonType("Ok");

                alert.getButtonTypes().setAll(buttonTypeOne);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonTypeOne){
                    mainApp.sendClientCancellation(app);
                }
            }
            else
                mainApp.showRescheduleApp(scheduleList, arrayList, app);
        }
    }



