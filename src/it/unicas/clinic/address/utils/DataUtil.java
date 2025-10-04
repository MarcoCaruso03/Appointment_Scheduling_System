package it.unicas.clinic.address.utils;

import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.model.Client;
import it.unicas.clinic.address.model.Schedule;
import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.StaffDAO;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.DAOClient;
import it.unicas.clinic.address.model.dao.mysql.StaffDAOMySQLImpl;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.sql.SQLException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;
/**
 * Contain useful classes for data manipulation
 */
public class DataUtil {


    static {
        appDAO = new AppointmentDAOMySQLImpl().getInstance();
    }


    private static AppointmentDAO appDAO= new AppointmentDAOMySQLImpl().getInstance();
    private static StaffDAO staffDAO = StaffDAOMySQLImpl.getInstance();
    /**
     * Class containing name, surname and whether the user is a manager or not, useful for login operations
     */
    public static class User{

        private String name;
        private String surname;
        private boolean isManager;
        int id;

        /**
         * Getter of name
         */
        public String getName() {
            return name;

        }
        /**
         * Setter of name, setting it equal to the one passed by argument
         * @param name: wanted name
         */
        public void setName(String name) {
            this.name = name;
        }
        /**
         * Getter of surname
         */
        public String getSurname() {
            return surname;
        }
        /**
         * Setter of surname, setting it equal to the one passed by argument
         * @param surname: wanted surname
         */
        public void setSurname(String surname) {
            this.surname = surname;
        }
        /**
         * Getter of info about being manager or not
         */
        public boolean getManager() {
            return isManager;
        }
        /**
         * Setter of the info about being a manager or not,
         * setting it equal to the one passed by argument
         * @param manager: wanted info
         */
        public void setManager(boolean manager) {
            isManager = manager;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        /**
         * Constructor: sets name and surname as empty strings and info about
         * being a manager false
         */
        public User() {
            this.name = "";
            this.surname = "";
            this.isManager = false;
            this.id=0;
        }
    }
    /**
     * Creates a LocalDate variable based on the string passed as argument
     * @param dateString: string containing format "yyyy-mm-dd"
     * @return
     */
    public static LocalDate parseToDate(String dateString) {
        //Format control
        if (dateString == null || !dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd.");
        }

        // Split the string into components
        String[] parts = dateString.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        if(month<0 || month>12 || day<0 || day>31) {
            throw new DateTimeException("Invalid date data!");
        }
        // Construct and return the LocalDate object
        return LocalDate.of(year, month, day);
    }

    /**
     * Creates a LocalTime variable based on the string passed as argument
     * @param timeString: string containing format "hh:mm:ss" or "hh:mm" depending on
     * modify parameter
     * @param modify: if true format is "hh:mm",
     *              if false format is "hh:mm:ss"
     * @return
     */
    public static LocalTime parseToTime(String timeString,boolean modify) {
        //We are building LocalTime from TextField ("hh:mm" format)
        if (modify) {
            //Format control
            if (timeString == null || !timeString.matches("\\d{1,2}:\\d{2}")) {
                throw new IllegalArgumentException("Invalid time format. Expected HH:mm");
            }

            // Split the string into components
            String[] parts = timeString.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            int second = 0;

            // Construct and return the LocalTime object
            return LocalTime.of(hour, minute, second);

        }
        //We already have "hh:mm:ss" format
        else {
            if (timeString == null || !timeString.matches("\\d{1,2}:\\d{2}:\\d{2}")) {
                System.out.print("\n" + timeString + " stringa eccomi qua 2" + "\n");

                throw new IllegalArgumentException("Invalid time format. Expected HH:mm:ss");
            }
            // Split the string into components
            String[] parts = timeString.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            int second = Integer.parseInt(parts[2]);

            // Construct and return the LocalTime object
            return LocalTime.of(hour, minute, second);
        }
    }

    public static LocalTime parseToDuration (String timeString,boolean modify){
        //We are building LocalTime from TextField ("hh:mm" format)
        if (modify) {
            //Format control
            if (timeString == null || !timeString.matches("\\d{1,2}|\\d{1,2}:\\d{2}")) {                System.out.println("Salve");
                throw new IllegalArgumentException("Invalid duration format. Expected hh:mm or mm");
            }
            if(timeString.matches("\\d{1,2}")) {
                // Split the string into components

                int hour = 0;
                int minute = Integer.parseInt(timeString);
                int second = 0;


                // Construct and return the LocalTime object
                return LocalTime.of(hour, minute, second);
            }
            else{
                // Split the string into components
                String[] parts = timeString.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                int second = 0;

                // Construct and return the LocalTime object
                return LocalTime.of(hour, minute, second);
            }

        }
        //We already have "hh:mm:ss" format
        else {
            if (timeString == null || !timeString.matches("\\d{1,2}:\\d{2}:\\d{2}")) {
                throw new IllegalArgumentException("Invalid time format. Expected HH:mm:ss");
            }
            // Split the string into components
            String[] parts = timeString.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            int second = Integer.parseInt(parts[2]);

            // Construct and return the LocalTime object
            return LocalTime.of(hour, minute, second);
        }
    }

    /**
     * Create a Boolean ArrayList representing time slots for the schedule passed by argument.
     * The element is true if the slot is available, false if not.
     * @param s: Schedule
     * @return
     */
    public static ArrayList<Boolean> avApp(Schedule s) {
        ArrayList<Boolean> list= new ArrayList<>();
        int temp = (int)Math.ceil((s.getStopTime().getHour()*60+s.getStopTime().getMinute()
                -(s.getStartTime().getHour()*60+s.getStartTime().getMinute()))/30.0);
        for(int i=0;i<temp;i++){
            list.add(true);
        }
        List<Appointment> appList = appDAO.select(new Appointment(
         0,null,s.getDay(),null,null,s.getStaffId(),0
        ));
        //Check for existing appointment
        for(Appointment el : appList){
            int start =(int)Math.ceil((el.getTime().getHour()*60+el.getTime().getMinute()
                    -(s.getStartTime().getHour()*60+s.getStartTime().getMinute()))/30.0);
            int end = (int) Math.ceil((el.getDuration().getHour() * 60
                    + el.getDuration().getMinute()) / 30.0) + start;
            for(int j=start;j<end;j++){
                list.set(j,false);
            }
        }
        return list;
    }

    /**
     * Filter the Boolean ArrayList in order to only consider lists capable of allocating the requested time slots.
     * @param list: Boolean ArrayList
     * @param time: Duration
     * @return
     */
    public static ArrayList<Boolean> avFilter(ArrayList<Boolean> list, LocalTime time){
        boolean ok=false;
        int count=0;
        int slots = (int)Math.ceil((time.getHour()*60+time.getMinute())/30.0);
        for(int i=0;i< list.size() && count<slots;i++){
            if(list.get(i))
                count++;
            else
                count=0;
        }
        if(count==slots)
            ok=true;
        if(ok)
            return list;
        else
            return null;
    }

    /**
     * Class to store preliminary Appointment information.
     */
    public static class AppInfo{

        private String service;
        private int staff_id;
        private int client_id;
        private LocalTime duration;

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public int getStaff() {
            return staff_id;
        }

        public void setStaff(int staff_id) {
            this.staff_id = staff_id;
        }

        public int getClient() {
            return client_id;
        }

        public void setClient(int client_id) {
            this.client_id = client_id;
        }

        public LocalTime getDuration() {
            return duration;
        }

        public void setDuration(LocalTime duration) {
            this.duration = duration;
        }
        public AppInfo(){
            this.service="";
            this.staff_id=0;
            this.client_id=0;
            this.duration=null;
        }
    }

    /**
     * Create an ObservableList of LocalTime in order to display all available starting times
     * of the appointment respecting the specified duration
     * @param s
     * @param boolList
     * @param duration
     * @return
     */
    public static ObservableList<LocalTime> timeSlots(Schedule s,ArrayList<Boolean> boolList,int duration){
        ObservableList<LocalTime> list = FXCollections.observableArrayList();
        //Consecutive available time slots
        int count=0;
        //Position in the boolean list
        int index=0;
        for (Boolean el : boolList) {
            if(el && count<duration) {
                count++;
            }
            else if(el && count==duration){
                int index_time=(index-count)*30;
                int hours = index_time / 60;
                int minutes = index_time % 60;

                LocalTime timePassed = LocalTime.of(hours,minutes);
                LocalTime timeStartApp = s.getStartTime().plusHours(timePassed.getHour())
                        .plusMinutes(timePassed.getMinute());
                list.add(timeStartApp);
            }
            else if(!el && count==duration) {
                int index_time = (index - count) * 30;
                int hours = index_time / 60;
                int minutes = index_time % 60;

                LocalTime timePassed = LocalTime.of(hours, minutes);
                LocalTime timeStartApp = s.getStartTime().plusHours(timePassed.getHour())
                        .plusMinutes(timePassed.getMinute());
                list.add(timeStartApp);
                count = 0;
            }
            else {
                count = 0;
            }

            index++;
            //To consider last available time slot
            int last = (int)Math.ceil((s.getStopTime().getHour()*60+s.getStopTime().getMinute()
                    -(s.getStartTime().getHour()*60+s.getStartTime().getMinute()))/30.0);
            if(index==last && el && count==duration){
                int index_time=(index-count)*30;
                int hours = index_time / 60;
                int minutes = index_time % 60;

                LocalTime timePassed = LocalTime.of(hours,minutes);
                LocalTime timeStartApp = s.getStartTime().plusHours(timePassed.getHour())
                        .plusMinutes(timePassed.getMinute());
                list.add(timeStartApp);
            }
        }
        return list;
    }


    public static void cleanUpRecord() throws SQLException {
        //10 year ago, data limite
        LocalDate tenYearsAgo = LocalDate.now().minusYears(10);

        //take all the staff members;
        //se staff è stato licenziato da almeno 10 anni lo elimino
        //di conseguenza on delete cascade si eliminano anche tutti i suoi appuntamenti
        List oldStaff = staffDAO.selectFiredBefore(tenYearsAgo);
        for(Object s : oldStaff){
            staffDAO.delete(s);
        }


        //take all client
        List<Client> oldClients = DAOClient.select(tenYearsAgo);
        //se client è stato licenziato da almeno 10 anni lo elimino
        //di conseguenza on delete cascade si eliminano anche tutti i suoi appuntamenti
        for (Client client : oldClients) {
            DAOClient.delete(client.getId());
        }

        List<Appointment> oldAppoint = appDAO.getPastApp(tenYearsAgo);
        for (Appointment appointment : oldAppoint) {
            appDAO.delete(appointment.getId());
        }



    }



    public static void main(String[] args) throws SQLException {

    }
}


