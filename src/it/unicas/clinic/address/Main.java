package it.unicas.clinic.address;

import it.unicas.clinic.address.model.*;
import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.DAOClient;
import it.unicas.clinic.address.utils.DataUtil;
import it.unicas.clinic.address.utils.DataUtil.AppInfo;
import it.unicas.clinic.address.view.appointment.*;
import it.unicas.clinic.address.view.client.*;
import it.unicas.clinic.address.view.credential.EditStaffCredentialController;
import it.unicas.clinic.address.view.credential.InitCredentialController;
import it.unicas.clinic.address.view.login.ChangePasswordController;
import it.unicas.clinic.address.view.login.ChangeUsernameController;
import it.unicas.clinic.address.view.login.LoginLayoutController;
import it.unicas.clinic.address.view.login.StaffManagerInitialLayoutController;
import it.unicas.clinic.address.view.login.StaffMemberInitialLayoutController;
import it.unicas.clinic.address.view.schedule.ScheduleAddingLayoutController;
import it.unicas.clinic.address.view.schedule.ScheduleManagementLayoutController;
import it.unicas.clinic.address.view.schedule.ScheduleUpdateLayoutController;
import it.unicas.clinic.address.view.staff.StaffAddingLayoutController;
import it.unicas.clinic.address.view.staff.StaffManagementLayoutController;
import it.unicas.clinic.address.view.staff.StaffUpdateLayoutController;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import it.unicas.clinic.address.view.appointment.calendarView.*;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import java.sql.SQLException;

/**
 * Class implementing the application
 */

public class Main extends Application {


    private static Main mainApp;
    private Stage primaryStage;
    private BorderPane loginLayout;
    private BorderPane staffInitialLayout;
    private AnchorPane appInitialLayout;
    private ObservableList<Staff> staffData = FXCollections.observableArrayList();
    private ObservableList<Appointment> appointmentData = FXCollections.observableArrayList();
    private ObservableList<Schedule> appSchedData = FXCollections.observableArrayList();
    private BorderPane page;
    private AppInfo appInfo = new AppInfo();
    private Boolean isManager;
    private int user_id;
    private ObservableList<Schedule> scheduleData = FXCollections.observableArrayList();

    public ObservableList<Schedule> getScheduleData() {
        return scheduleData;
    }
    private PauseTransition timer=null;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }


    public ObservableList<Schedule> getAppSchedData() {
        return appSchedData;
    }
    public void saveAppSchedData(ObservableList<Schedule> list){appSchedData=list;}
    public ObservableList<Staff> getStaffData() {
        return staffData;
    }
    public ObservableList<Appointment> getAppointmentData() {
        return appointmentData;
    }
    public Boolean getIsManager(){return isManager;}
    public void setIsManager(Boolean isManager){this.isManager=isManager;}

    public Main() {
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Default method called at the start of the application.
     * It makes the login GUI appear.
     */

    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        mainApp=this;
        DataUtil.cleanUpRecord();

        this.primaryStage=primaryStage;
        this.primaryStage.setTitle("Clinic");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/it/unicas/clinic/address/view/client/ClientOverview.fxml"));
        //   BorderPane root = FXMLLoader.load(getClass().getResource("/it/unicas/clinic/address/view/RootLayout.fxml"));
        AnchorPane root = loader.load();

        initLogin();
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    /**
     * Method to restart application.
     * Used when the user is kicked out from the system due to inactivity (15 min)
     */
    public static void restart() {
        Stage stage = mainApp.primaryStage; // Recupera il primaryStage
        stage.close(); // Chiudi la finestra attuale
        try {
            mainApp.start(new Stage()); // Avvia un nuovo Stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Pop up an alert for the exit operation.
     */
    public void handleExit() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to quit the app?");
        alert.setContentText("Click " + "\n" + "'Yes' to exit" + "\n" + "'Back' to close the window");

        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeCancel = new ButtonType("Back", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            System.exit(0);
        }
    }

    /**
     * Load the login GUI to the application window
     */
    public void initLogin() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class
                    .getResource("view/login/LoginLayout.fxml"));
            loginLayout = (BorderPane) loader.load();

            // Show the scene containing the login layout.
            Scene scene = new Scene(loginLayout);
            primaryStage.setScene(scene);

            //Implementing alert when you click on the 'X' of the window
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                handleExit();
            });
            primaryStage.setResizable(false);

            // Give the controller access to the main app.
            LoginLayoutController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Load the initial staff member GUI to the application window
     */
    public void initStaff(){
        try{
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            //loader.setLocation(Main.class
            //        .getResource("view/login/StaffMemberInitialLayout.fxml"));
            loader.setLocation(Main.class
                    .getResource("view/login/StaffMemberInitialLayout2.fxml"));
            staffInitialLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(staffInitialLayout);
            primaryStage.setScene(scene);

            //Implementing alert when you click on the 'X' of the window
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                handleExit();
            });
            primaryStage.setResizable(false);


            // Give the controller access to the main app.
            StaffMemberInitialLayoutController controller = loader.getController();
            controller.setMainApp(this);
            controller.setIcon();

            //Set and show primary stage
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);
            primaryStage.show();

            setTimer(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Load the initial staff manager GUI to the application window
     */
    public void initStaffManager(){
        try{

            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class
                    .getResource("view/login/StaffManagerInitialLayout.fxml"));
            staffInitialLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(staffInitialLayout);
            primaryStage.setScene(scene);

            //Implementing alert when you click on the 'X' of the window
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                handleExit();
            });


            // Give the controller access to the main app.
            StaffManagerInitialLayoutController controller = loader.getController();
            controller.setMainApp(this);

            //Set and show primary stage
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);
            primaryStage.show();

            setTimer(scene);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the GUI in order to add the data for a new staff member
     */
    public void showStaffInsertDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/staff/StaffAddingLayout.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Staff");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            //Controller
            StaffAddingLayoutController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);
            //controller.setStaff();

            // Set the dialog icon.
            dialogStage.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
            setTimer(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    /**
     * Load the GUI in order to add the data for a new staff member
     * @param s: the staff member you want to update
     */
    public void showStaffUpdateDialog(Staff s){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/staff/StaffUpdateLayout.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Staff");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            //Controller
            StaffUpdateLayoutController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);
            controller.setField(s);

            // Set the dialog icon.
            dialogStage.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
            setTimer(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Load a GUI in order to list all staff members
     * @throws IOException
     */
    public void loadStaffManagement() throws IOException{
        staffData.clear();
        //loadStaffManagementChoose();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/staff/StaffManagementLayout2.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        StaffManagementLayoutController controller = loader.getController();
        controller.setMainApp(this);
        primaryStage.setTitle("Clinic");
        primaryStage.setResizable(false);
        primaryStage.show();
        setTimer(scene);
    }

    public void searchClientLayout(ClientOverviewController clientController) throws IOException {
        Stage searchWindow = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicas/clinic/address/view/client/SearchClient.fxml"));
        AnchorPane layout = loader.load();


        SearchClientController controller = loader.getController();
        controller.setStage(searchWindow);
        controller.setMainApp(this);
        controller.SetClientOverviewController(clientController);

        searchWindow.initModality(Modality.WINDOW_MODAL);
        searchWindow.initOwner(primaryStage);


        searchWindow.setScene(new Scene(layout));
        setTimer(searchWindow.getScene());
        searchWindow.setResizable(false);
        searchWindow.showAndWait();
    }

    /**
     * Load a GUI in order to add a new Client
     * @param clientController
     * @throws IOException
     */
    public void addClientLayout(ClientOverviewController clientController) throws IOException {
        Stage addWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicas/clinic/address/view/client/AddClient.fxml"));
        AnchorPane layout = loader.load();
        AddClientController controller = loader.getController();
        controller.setStage(addWindow);
        controller.setMainApp(this);
        controller.SetClientOverviewController(clientController);

        addWindow.initModality(Modality.WINDOW_MODAL);
        addWindow.initOwner(primaryStage);

        addWindow.setScene(new Scene(layout));
        setTimer(addWindow.getScene());
        addWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        addWindow.setTitle("Add Client");
        addWindow.setResizable(false);
        addWindow.show();
    }

    /**
     * Load a GUI in order to update an existing Client
     * @param clientController
     * @param client: the client you want to update
     * @throws IOException
     */
    public void updateClientLayout(ClientOverviewController clientController, Client client) throws IOException {
        Stage updateWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/it/unicas/clinic/address/view/client/UpdateClient.fxml"));
        AnchorPane layout = (AnchorPane) loader.load();
        UpdateClientController controller = loader.getController();
        controller.setMainApp(this);
        controller.setStage(updateWindow);
        controller.SetClientOverviewController(clientController);
        controller.getClient(client);

        //definisco suo padre (x forza)
        updateWindow.initModality(Modality.WINDOW_MODAL);
        updateWindow.initOwner(primaryStage);

        updateWindow.setScene(new Scene(layout));
        setTimer(updateWindow.getScene());
        updateWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        updateWindow.setTitle("Update Client");
        updateWindow.setResizable(false);
        updateWindow.showAndWait();
    }

    /**
     * Load a GUI in order to list all clients
     * @throws IOException
     * @throws SQLException
     */
    public void showClientView() throws IOException, SQLException {
        this.primaryStage.setTitle("Clinic");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/it/unicas/clinic/address/view/client/ClientOverview.fxml")); //   BorderPane root = FXMLLoader.load(getClass().getResource("/it/unicas/clinic/address/view/RootLayout.fxml"));
        AnchorPane root = loader.load();
        ClientOverviewController controller = loader.getController();    controller.setMainApp(this);
        controller.ShowAllClients();
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
        setTimer(primaryStage.getScene());
    }

    /**
     * Load a GUI in order to list all appointments
     */
    public void initAppView(){
        try{
            appointmentData.clear();
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class
                    .getResource("view/appointment/AppointmentView.fxml"));
            appInitialLayout = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(appInitialLayout);
            primaryStage.setScene(scene);

            //Implementing alert when you click on the 'X' of the window
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                handleExit();
            });


            // Give the controller access to the main app.
            AppointmentViewController controller = loader.getController();
            controller.setMainApp(this);

            //Set and show primary stage
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);
            primaryStage.setTitle("Clinic");
            primaryStage.show();
            setTimer(scene);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load a GUI in order to add a new Appointment
     */
    public void showAppInsertDialog() {
        if(isManager) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("view/appointment/AppAddingLayout2.fxml"));
                AnchorPane page = (AnchorPane) loader.load();
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Add Appointment");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(primaryStage);
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);

                //Controller
                AppAddingLayoutController2 controller = loader.getController();
                controller.setMainApp(this);
                controller.setDialogStage(dialogStage);
                //controller.setStaff();

                // Set the dialog icon.
                //dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
                setTimer(scene);
                dialogStage.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
                dialogStage.setResizable(false);
                dialogStage.showAndWait();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("view/appointment/AppAddingLayout3.fxml"));
                AnchorPane page = (AnchorPane) loader.load();
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Add Appointment");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(primaryStage);
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);

                //Controller
                AppAddingLayoutController3 controller = loader.getController();
                controller.setMainApp(this);
                controller.setDialogStage(dialogStage);
                //controller.setStaff();

                // Set the dialog icon.
                //dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
                setTimer(scene);
                dialogStage.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
                dialogStage.setResizable(false);
                dialogStage.showAndWait();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            catch (SQLException e){
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Load a GUI in order to update an existing appointment
     * @param a: the appointment you want to update
     */
    public void showAppUpdateDialog(Appointment a){
        try {
            FXMLLoader loader = new FXMLLoader();
            if(isManager)
                loader.setLocation(Main.class.getResource("view/appointment/AppUpdateLayout2.fxml"));
            else
                loader.setLocation(Main.class.getResource("view/appointment/AppUpdateLayout3.fxml"));

            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Stuff");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            //Controller
            if(isManager) {
                AppUpdateLayoutController2 controller = loader.getController();
                controller.setMainApp(this);
                controller.setDialogStage(dialogStage);
                controller.setField(a);
            }
            else{
                AppUpdateLayoutController3 controller = loader.getController();
                controller.setMainApp(this);
                controller.setDialogStage(dialogStage);
                controller.setField(a);
            }
            // Set the dialog icon.
            //dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
            setTimer(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Load an error Alert in order to display information
     * @param title: alert title
     * @param header: alert header
     * @param content: alert content
     */
    public void errorAlert(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setResizable(false);
        alert.showAndWait();
    }
    /**
     * Load a warning Alert in order to display information
     * @param title: alert title
     * @param header: alert header
     * @param content: alert content
     */
    public void warningAlert(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setResizable(false);
        alert.showAndWait();
    }
    /**
     * Load an information Alert in order to display information
     * @param title: alert title
     * @param header: alert header
     * @param content: alert content
     */
    public void infoAlert(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setResizable(false);
        alert.showAndWait();
    }

    /**
     * Load a GUI in order to choose a staff member
     * @throws IOException
     */
    public void showAppStaff() throws IOException {
        appointmentData.clear();
        staffData.clear();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/appointment/AppStaffView.fxml"));
        AnchorPane p = (AnchorPane) loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Choose Staff Member");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(p);
        dialogStage.setScene(scene);
        AppStaffViewController controller = loader.getController();
        controller.setMainApp(this);
        controller.setDialogStage(dialogStage);
        setTimer(scene);
        dialogStage.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }

    /**
     * Load a GUI in order to choose a client
     * @throws IOException
     * @throws SQLException
     */
    public void showAppClient() throws IOException, SQLException {
        appointmentData.clear();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/appointment/AppClientView.fxml"));
        AnchorPane p = (AnchorPane) loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Choose Client");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(p);
        dialogStage.setScene(scene);
        AppClientViewController controller = loader.getController();
        controller.setMainApp(this);
        controller.ShowAllClients();
        controller.setDialogStage(dialogStage);
        setTimer(scene);
        dialogStage.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }

    /**
     * Save the id of chosen staff for appointment
     * @param id: id of the selected staff
     */
    public void saveStaff(int id){
        appInfo.setStaff(id);
    }

    /**
     * Get the id of selected staff for appointment
     * @return
     */
    public int getSavedStaff(){
        return appInfo.getStaff();
    }

    /**
     * Save the id of chosen client for appointment
     * @param id: id of the selected staff
     */
    public void saveClient(int id){appInfo.setClient(id);}
    /**
     * Get the id of selected client for appointment
     * @return
     */
    public int getSavedClient(){return appInfo.getClient();}

    /**
     * Save the service of the creating appointment
     * @param service: the service of the appointment that id being craeted
     */
    public void saveService(String service){appInfo.setService(service);}

    /**
     * Get the service of the creating appointment
     * @return
     */
    public String getSavedService(){return appInfo.getService();}

    /**
     * Save the duration of the creating appointment
     * @param duration: the duration of the appointment that's being created
     */
    public void saveDuration(LocalTime duration){appInfo.setDuration(duration);}
    /**
     * Get the duration of the creating appointment
     * @return
     */
    public LocalTime getSavedDuration(){return appInfo.getDuration();}

    /**
     * Load a GUI in order to list all dates and time slots available for the appointment
     * @param schedules: all existing schedules
     * @param list: boolean list for time slots
     * @throws IOException
     */
    public void showAvailableApp(ArrayList<Schedule> schedules,ArrayList<ArrayList<Boolean>> list) throws IOException {
        appSchedData.clear();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/appointment/AppSelectView.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        AppSelectViewController controller = loader.getController();
        controller.setMainApp(this,schedules,list);
        setTimer(scene);
        primaryStage.setTitle("Schedule Appointment");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Load a GUI in order to list all dates and time slots available for the appointment you want to update
     * @param schedules: all existing schedules
     * @param list: boolean list for time slots
     * @param a: the appointment you want to update
     * @throws IOException
     */
    public void showAvailableAppUp(ArrayList<Schedule> schedules,ArrayList<ArrayList<Boolean>> list,Appointment a) throws IOException {
        appSchedData.clear();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/appointment/AppSelectView2.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        AppSelectViewController2 controller = loader.getController();
        controller.setMainApp(this,schedules,list,a);
        setTimer(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Load a GUI in order to list all schedules associated to the selected staff
     * @param s: selected staff
     * @throws IOException
     */
    public void showScheduleManagmentLayout(Staff s) throws IOException {
        scheduleData.clear();
        Stage updateWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/it/unicas/clinic/address/view/schedule/ScheduleManagementLayout.fxml"));
        AnchorPane layout = (AnchorPane) loader.load();
        ScheduleManagementLayoutController controller = loader.getController();
        controller.setMainApp(this, s);
        //controller.setStage(updateWindow);

        updateWindow.initModality(Modality.WINDOW_MODAL);
        updateWindow.initOwner(primaryStage);

        updateWindow.setScene(new Scene(layout));
        // Aggiungi un listener per la chiusura della finestra. Quando si chiude devo svuotare la lista degli schedule
        updateWindow.setOnCloseRequest(event -> {
            scheduleData.clear();
        });
        updateWindow.setTitle("Schedule Management");
        updateWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        setTimer(updateWindow.getScene());
        updateWindow.setResizable(false);
        updateWindow.showAndWait();
    }

    /**
     * Load a GUI in order to add a schedule to the selected staff
     * @param s: selected staff
     */
    public void showScheduleInsertDialog(Staff s) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/schedule/ScheduleAddingLayout.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Schedule");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            //Controller
            ScheduleAddingLayoutController controller = loader.getController();
            controller.setMainApp(this, s);
            controller.setDialogStage(dialogStage);

            // Set the dialog icon.
            //dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
            setTimer(scene);
            dialogStage.setTitle("Add Schedule");
            dialogStage.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load a GUI in order to uodate an existing schedule
     * @param s
     * @param staff
     */
    public void showScheduleUpdateDialog(Schedule s, Staff staff){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/schedule/ScheduleUpdateLayout.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Schedule");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            //Controller
            ScheduleUpdateLayoutController controller = loader.getController();
            controller.setMainApp(this, staff);
            controller.setField(s);
            controller.setDialogStage(dialogStage);

            // Set the dialog icon.
            //dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
            setTimer(scene);
            dialogStage.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load a GUI in order to force reschedule of appointments when deleting/updating schedule
     * @param schedules: list of schedules
     * @param list: time slots
     * @param a: appointment
     * @throws IOException
     * @throws SQLException
     */
    public void showRescheduleApp(ArrayList<Schedule> schedules,ArrayList<ArrayList<Boolean>> list,Appointment a) throws IOException, SQLException {
        appSchedData.clear();
        Stage rescheduleWindow = new Stage();
        rescheduleWindow.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/appointment/AppSelectView3.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Scene scene = new Scene(page);
        rescheduleWindow.setScene(scene);
        AppSelectViewController3 controller = loader.getController();
        controller.setMainApp(this,schedules,list,a,rescheduleWindow);
        setTimer(scene);
        rescheduleWindow.setTitle("Reschedule Appointment");
        rescheduleWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        rescheduleWindow.setResizable(false);
        rescheduleWindow.showAndWait();
    }

    /**
     * Load a GUI in order to change password of the logged-in user
     * @throws IOException
     * @throws SQLException
     */
    public void changePassword() throws IOException, SQLException {
        Stage changePasswordWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/login/ChangePassword.fxml"));

        AnchorPane anchorPane = loader.load();
        ChangePasswordController controller = loader.getController();
        controller.setMainApp(this);
        controller.setStage(changePasswordWindow);
        controller.setId(user_id);

        changePasswordWindow.initModality(Modality.WINDOW_MODAL);
        changePasswordWindow.initOwner(primaryStage);

        changePasswordWindow.setScene(new Scene(anchorPane));
        setTimer(changePasswordWindow.getScene());
        changePasswordWindow.setTitle("Change Password");
        changePasswordWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        changePasswordWindow.setResizable(false);
        changePasswordWindow.showAndWait();

    }

    /**
     * Load a GUI in order to change username of the logged-in user
     * @throws IOException
     */
    public void changeUsername() throws IOException{
        Stage changeUsernameWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/login/ChangeUsername.fxml"));

        AnchorPane anchorPane = loader.load();
        ChangeUsernameController controller = loader.getController();
        controller.setMainApp(this);
        controller.setStage(changeUsernameWindow);
        controller.setId(user_id);

        changeUsernameWindow.initModality(Modality.WINDOW_MODAL);
        changeUsernameWindow.initOwner(primaryStage);

        changeUsernameWindow.setScene(new Scene(anchorPane));
        setTimer(changeUsernameWindow.getScene());
        changeUsernameWindow.setTitle("Change Username");
        changeUsernameWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        changeUsernameWindow.setResizable(false);
        changeUsernameWindow.showAndWait();
    }

    public void setId(int id) {user_id = id;}

    /**
     * Load a GUI in order to edit staff member credential
     * @param id: id of the staff member
     * @throws IOException
     */
    public void EditStaffCredential(int id) throws IOException {
        Stage editStaffWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/credential/EditStaffCredential.fxml"));
        AnchorPane anchorPane = loader.load();
        EditStaffCredentialController controller = loader.getController();
        controller.setMainApp(this);
        controller.setStage(editStaffWindow);
        controller.setManagerId(user_id);
        controller.setId(id);

        editStaffWindow.initModality(Modality.WINDOW_MODAL);
        editStaffWindow.initOwner(primaryStage);

        editStaffWindow.setScene(new Scene(anchorPane));
        editStaffWindow.setTitle("Edit Staff Credentials");
        editStaffWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        editStaffWindow.setResizable(false);
        editStaffWindow.showAndWait();
    }

    /**
     * Load a GUI in order to show client history
     */
    public void showClientHistory(){
        try{
            //appointmentData.clear();
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class
                    .getResource("view/client/ClientHistoryView.fxml"));
            appInitialLayout = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(appInitialLayout);
            primaryStage.setScene(scene);

            //Implementing alert when you click on the 'X' of the window
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                handleExit();
            });


            // Give the controller access to the main app.
            ClientHistoryViewController controller = loader.getController();
            controller.setMainApp(this);

            //Set and show primary stage
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);
            setTimer(scene);
            primaryStage.setTitle("Client History");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load a GUI in order to create a report file
     * @throws IOException
     */
    public void openReport() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/appointment/ReportLayout.fxml"));
        AnchorPane p = (AnchorPane) loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Generate a report");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(p);
        dialogStage.setScene(scene);
        ReportLayoutController controller = loader.getController();
        controller.setMainApp(this);
        controller.setDialogStage(dialogStage);
        setTimer(scene);
        dialogStage.setTitle("Report");
        dialogStage.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        dialogStage.setResizable(false);
        dialogStage.show();
    }

    /**
     * Load a GUI in order to show monthly appointments
     * @throws IOException
     * @throws SQLException
     */
    public void showMonthlyView() throws IOException, SQLException {
        Stage monthlyViewWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/appointment/calendarView/MonthlyView.fxml"));
        AnchorPane anchorPane = loader.load();

        MonthlyViewController controller = loader.getController();
        controller.setMainApp(this);
        controller.setStage(monthlyViewWindow);

        monthlyViewWindow.initModality(Modality.WINDOW_MODAL);
        monthlyViewWindow.initOwner(primaryStage);

        monthlyViewWindow.setScene(new Scene(anchorPane));
        setTimer(monthlyViewWindow.getScene());
        monthlyViewWindow.setTitle("Monthly Calendar");
        monthlyViewWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        monthlyViewWindow.setResizable(false);
        monthlyViewWindow.show();
    }
    /**
     * Load a GUI in order to show weekly appointments
     * @throws IOException
     * @throws SQLException
     */
    public void showWeeklyView() throws IOException {
        Stage weeklyViewWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(Main.class.getResource("view/appointment/calendarView/WeeklyView.fxml"));
        AnchorPane anchorPane = loader.load();

        WeeklyViewController controller = loader.getController();
        controller.setMainApp(this);
        controller.setStage(weeklyViewWindow);

        weeklyViewWindow.initModality(Modality.WINDOW_MODAL);
        weeklyViewWindow.initOwner(primaryStage);
        weeklyViewWindow.setScene(new Scene(anchorPane));
        setTimer(weeklyViewWindow.getScene());
        weeklyViewWindow.setTitle("Weekly Calendar");
        weeklyViewWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        weeklyViewWindow.setResizable(false);

        weeklyViewWindow.show();

    }
    /**
     * Load a GUI in order to show daily appointments
     * @throws IOException
     * @throws SQLException
     */
    public void showDailyView() throws IOException {
        Stage dailyViewWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/appointment/calendarView/DailyView2.fxml"));
        AnchorPane anchorPane = loader.load();

        DailyView2Controller controller = loader.getController();
        controller.setMainApp(this);
        controller.setStage(dailyViewWindow);

        dailyViewWindow.initModality(Modality.WINDOW_MODAL);
        dailyViewWindow.initOwner(primaryStage);
        dailyViewWindow.setScene(new Scene(anchorPane));
        setTimer(dailyViewWindow.getScene());
        dailyViewWindow.setTitle("Daily Calendar");
        dailyViewWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        dailyViewWindow.setResizable(false);
        dailyViewWindow.show();
    }

    /**
     * Load a GUI in order to show daily appointments of the logged-in user
     * @throws IOException
     * @throws SQLException
     */
    public void showDailyView2() throws IOException {
        Stage dailyViewWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/appointment/calendarView/DailyView.fxml"));
        AnchorPane anchorPane = loader.load();

        DailyViewController controller = loader.getController();
        controller.setMainApp(this);
        controller.setStage(dailyViewWindow);

        dailyViewWindow.initModality(Modality.WINDOW_MODAL);
        dailyViewWindow.initOwner(primaryStage);
        dailyViewWindow.setScene(new Scene(anchorPane));
        setTimer(dailyViewWindow.getScene());
        dailyViewWindow.setTitle("Daily Calendar");
        dailyViewWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        dailyViewWindow.setResizable(false);
        dailyViewWindow.show();
    }

    /**
     * Load a GUI in order to filter the shown appointments in the calendar for the staff manager
     * @param conM
     * @param conW
     * @param conD
     * @throws IOException
     */
    public void filterCalendarViewForManager(MonthlyViewController conM, WeeklyViewController conW, DailyView2Controller conD) throws IOException {
        Stage filterCalendarWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(Main.class.getResource("view/appointment/calendarView/FilterByForStaffManager.fxml"));
        AnchorPane anchorPane = loader.load();

        FilterByForStaffManagerController controller = loader.getController();
        controller.setMainApp(this);
        controller.setDialogStage(filterCalendarWindow);
        if(conM != null) {
            controller.setMvController(conM);
        } else if(conW != null) {
            controller.setWvController(conW);
        } else if(conD != null) {
            controller.setDvController(conD);
        }



        filterCalendarWindow.initModality(Modality.WINDOW_MODAL);
        filterCalendarWindow.initOwner(primaryStage);
        filterCalendarWindow.setScene(new Scene(anchorPane));
        setTimer(filterCalendarWindow.getScene());
        filterCalendarWindow.setTitle("Calendar View");
        filterCalendarWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        filterCalendarWindow.setResizable(false);
        filterCalendarWindow.showAndWait();
    }

    /**
     * Load a GUI in order to filter the shown appointments in the calendar for the logged-in user
     * @param conM
     * @param conW
     * @param conD
     * @throws IOException
     */
    public void filterCalendarViewForMember(MonthlyViewController conM, WeeklyViewController conW, DailyView2Controller conD) throws IOException {
        Stage filterCalendarWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(Main.class.getResource("view/appointment/calendarView/FilterByForStaffMember.fxml"));
        AnchorPane anchorPane = loader.load();

        FilterByForStaffMemberController controller = loader.getController();
        controller.setMainApp(this);
        controller.setDialogStage(filterCalendarWindow);
        if(conM != null) {
            controller.setMvController(conM);
        } else if(conW != null) {
            controller.setWvController(conW);
        } else if(conD != null) {
            controller.setDvController(conD);
        }



        filterCalendarWindow.initModality(Modality.WINDOW_MODAL);
        filterCalendarWindow.initOwner(primaryStage);
        filterCalendarWindow.setScene(new Scene(anchorPane));
        setTimer(filterCalendarWindow.getScene());
        filterCalendarWindow.setTitle("Calendar Filter");
        filterCalendarWindow.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        filterCalendarWindow.setResizable(false);
        filterCalendarWindow.showAndWait();
    }
    /**
     * Load a GUI in order to filter the shown appointments in the calendar for the logged-in user
     * @param conM
     * @param conW
     * @param conD
     * @throws IOException
     */
    public void filterCalendarViewForMember2(MonthlyViewController conM, WeeklyViewController conW, DailyViewController conD) throws IOException {
        Stage filterCalendarWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(Main.class.getResource("view/appointment/calendarView/FilterByForStaffMember.fxml"));
        AnchorPane anchorPane = loader.load();

        FilterByForStaffMemberController controller = loader.getController();
        controller.setMainApp(this);
        controller.setDialogStage(filterCalendarWindow);
        if(conM != null) {
            controller.setMvController(conM);
        } else if(conW != null) {
            controller.setWvController(conW);
        } else if(conD != null) {
            controller.setDvController1(conD);
        }



        filterCalendarWindow.initModality(Modality.WINDOW_MODAL);
        filterCalendarWindow.initOwner(primaryStage);
        filterCalendarWindow.setScene(new Scene(anchorPane));
        setTimer(filterCalendarWindow.getScene());
        filterCalendarWindow.setResizable(false);
        filterCalendarWindow.showAndWait();
    }


    /**
     * Load a GUI in order to see the appointments in a selected time slot
     */
    public void showCalendarAppView(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/appointment/calendarView/AppCalendarView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Schedule");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            //Controller
            AppCalendarViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            // Set the dialog icon.
            //dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
            setTimer(scene);
            dialogStage.setTitle("Appointments");
            dialogStage.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the timer with a specified scene
     * @param scene: specific scene
     */
    public void setTimer(Scene scene){
        // Configura il timeout con PauseTransition
        if(timer!=null)
            timer.stop();
        timer = new PauseTransition(Duration.minutes(15));
        timer.setOnFinished(e -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Attention");
                alert.setHeaderText("Incativity detected");
                alert.setContentText("You'll be kicked out");
                alert.setResizable(false);
                alert.showAndWait();
                restart();
            });
        });

        // Metodo per resettare il timeout
        Runnable resetTimeout = () -> {
            timer.stop();
            timer.playFromStart(); // Riavvia il timer
        };
        // Aggiungi listener per eventi di interazione (mouse e tastiera)
        scene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_MOVED, e -> resetTimeout.run());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> resetTimeout.run());

        // Avvia il timeout inizialmente
        timer.play();
    }

    /**
     * Send email to client in order to remind the client about appointments (24 hrs before)
     * @throws SQLException
     */
    public void sendClientNotification() throws SQLException {
        AppointmentDAO appDao = AppointmentDAOMySQLImpl.getInstance();
        ArrayList<Appointment> apps = appDao.getTomorrowApp();
        if (apps != null) {
            for (Appointment app : apps) {
                Client c = DAOClient.select(app.getClientId());
                String email = c.getEmail();
                Email mailSender = new Email(email);
                try {
                    if (mailSender.sendEmail(app)) {
                        infoAlert("Notification",
                                "Emails sent successfully",
                                "Clients have been noticed about upcoming appointments");
                    }
                } catch (IOException e) {
                    mainApp.errorAlert("Error",
                            "Can not add information to the log",
                            "");
                }
            }

        }
    }

    /**
     * Send email to client in order to notice the client about deleted appointments
     * @param app: deleted appointment
     * @throws SQLException
     */
    public void sendClientCancellation(Appointment app) throws SQLException {

        Client c = DAOClient.select(app.getClientId());
        String email = c.getEmail();
        Email mailSender = new Email(email);
        try {
            if (mailSender.sendCancellation(app)) {
                infoAlert("Notification",
                        "Email sent successfully",
                        "Client has been noticed about deleted appointment");
            }
        }catch(IOException e){
            mainApp.infoAlert("Error",
                    "Can not add information to the log",
                    "");
        }
    }

    /**
     * Send email to client in order to notice the client about updated appointments
     * @param app: updated appointment
     * @throws SQLException
     */
    public void sendClientReschedule(Appointment app) throws SQLException{

        Client c = DAOClient.select(app.getClientId());
        String email = c.getEmail();
        Email mailSender = new Email(email);
        try{
            if(mailSender.sendUpdate(app)) {
                infoAlert("Notification",
                        "Email sent successfully",
                        "Client has been noticed about appointment reschedule");
            }
        }catch(IOException e){
            mainApp.infoAlert("Error",
                    "Can not add information to the log",
                    "");
        }
    }

    /**
     * Load a GUI in order to insert credentials for a freshly created staff member
     * @param owner
     * @param con
     * @throws IOException
     */
    public void initCredential(Stage owner, StaffAddingLayoutController con ) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/credential/InitCredential.fxml"));
        AnchorPane anchorPane = loader.load();
        Stage dialogStage = new Stage();

        InitCredentialController controller = loader.getController();
        controller.setMainApp(this);
        controller.setStaffAddingLayoutController(con);



        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(owner);

        controller.setStage(dialogStage);

        Scene scene = new Scene(anchorPane);
        dialogStage.setScene(scene);
        dialogStage.setTitle("Insert Credentials");
        dialogStage.getIcons().add(new Image("resources/login_icons/clinic-icon.png"));
        dialogStage.setResizable(false);
        dialogStage.showAndWait();

    }

    public static void main(String[] args) {
        launch(args);
    }

}