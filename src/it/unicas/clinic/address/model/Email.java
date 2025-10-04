package it.unicas.clinic.address.model;

import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.mysql.AppointmentDAOMySQLImpl;
import it.unicas.clinic.address.model.dao.mysql.DAOClient;
import javax.mail.*;
import javax.mail.internet.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Implements the class Email in order to send emails to the client.
 */
public class Email {
    // Email Configuration
    String sender = "unicasmedicalclinic@gmail.com";
    String password = "nttb rdmm lpbq sxxr";
    String receiver;
    String host = "smtp.gmail.com";
    private AppointmentDAO appointmentDAO = AppointmentDAOMySQLImpl.getInstance();
    // SMTP Server Properties
    Properties properties;
    public Email(String receiver) {
        this.receiver = receiver;
        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
    }

    /**
     * Returns true if the notification mail is sent successfully, else returns false.
     * @param appointment: appointment to be noticed to the client.
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public boolean sendEmail(Appointment appointment) throws SQLException, IOException {
        if(appointment.getNotice()==null) {
            // Create session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(sender, password);
                }
            });

            try {
                // Create email message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender));
                message.setRecipients(
                        Message.RecipientType.TO, InternetAddress.parse(receiver));
                message.setSubject("Appointment Reminder");
                message.setText("Dear " + DAOClient.getClient(appointment.getClientId()).getName() + ",\n" +
                        "We would like to remind you the appointment for " + appointment.getDate().toString() + ".\n" +
                        "We recommend to be punctual, the starting time is at " + appointment.getTime().toString());

                // Send the email
                Transport.send(message);
                appointmentDAO.setNotice(appointment);

            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            writeLog(0,appointment);
            return true;
        }
        else
            return false;

    }

    /**
     * Returns true if the cancellation mail is sent successfully, else returns false.
     * @param appointment: deleted appointment.
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public boolean sendCancellation(Appointment appointment) throws SQLException, IOException {
        if(appointment.getNotice()==null || !appointment.getNotice()) {
            // Create session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(sender, password);
                }
            });

            try {
                // Create email message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender));
                message.setRecipients(
                        Message.RecipientType.TO, InternetAddress.parse(receiver));
                message.setSubject("Appointment Cancellation");
                message.setText("Dear " + DAOClient.getClient(appointment.getClientId()).getName() + ",\n" +
                        "We are sorry to inform you that the appointment for " + appointment.getDate().toString() +
                        " has been deleted.\n" +
                        "Please contact the clinic center in order to reschedule it. ");

                // Send the email
                Transport.send(message);
                appointmentDAO.setNotice(appointment);

            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            writeLog(1,appointment);
            return true;
        }
        else
            return false;
    }

    /**
     * Returns true if the update/reschedule mail is sent successfully, else returns false.
     * @param appointment: updated/rescheduled appointment.
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public boolean sendUpdate(Appointment appointment) throws SQLException, IOException {
        if(appointment.getNotice()==null) {
            // Create session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(sender, password);
                }
            });

            try {
                // Create email message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender));
                message.setRecipients(
                        Message.RecipientType.TO, InternetAddress.parse(receiver));
                message.setSubject("Appointment Reschedule");
                message.setText("Dear " + DAOClient.getClient(appointment.getClientId()).getName() + ",\n" +
                        "We are sorry to inform you that the appointment: " + appointment.getService()+
                        " has been rescheduled to the following date:"+ appointment.getDate().toString() +".\n" +
                        "Please contact the clinic center if you want to reschedule it. ");

                // Send the email
                Transport.send(message);
                //appointmentDAO.setNotice(appointment);

            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            writeLog(2,appointment);
            return true;
        }
        else
            return false;
    }

    private void writeLog(int choice, Appointment app) throws IOException, SQLException {

        FileWriter fileWriter = new FileWriter("EmailLog.txt", true); // true per abilitare l'append
        PrintWriter printWriter = new PrintWriter(fileWriter);

        Client c = DAOClient.select(app.getClientId());
        if(choice==0){
            printWriter.println("Reminder email has been sent to " + c.getName()+ " "+ c.getSurname()+
                    " on date: "+ LocalDate.now()+", at time:"+
                    LocalTime.now()+
                    ", regarding appointment with id: "+app.getId());
        }
        else if(choice==1){
            printWriter.println("Cancellation email has been sent to " + c.getName()+ " "+ c.getSurname()+
                    " on date: "+ LocalDate.now()+", at time:"+
            LocalTime.now()+
                    ", regarding appointment with id: "+app.getId());
        }
        else if(choice==2){
            printWriter.println("Update email has been sent to " + c.getName()+ " "+ c.getSurname()+
                    " on date: "+ LocalDate.now()+", at time:"+
                    LocalTime.now()+
                    ", regarding appointment with id: "+app.getId());
        }
        printWriter.close();
    }


}
