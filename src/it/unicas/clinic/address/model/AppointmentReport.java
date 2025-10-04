package it.unicas.clinic.address.model;

import it.unicas.clinic.address.model.dao.StaffDAO;
import it.unicas.clinic.address.model.dao.mysql.DAOClient;
import it.unicas.clinic.address.model.dao.mysql.StaffDAOMySQLImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Extends Appointment with name and surname of Client and Staff associated with the specific appointment.
 */
public class AppointmentReport extends Appointment {
    private String staffName;
    private String staffSurname;
    private String clientName;
    private String clientSurname;
    private static StaffDAO daoStaff = StaffDAOMySQLImpl.getInstance();


    // Costruttore che accetta un'istanza di Appointment
    public AppointmentReport(Appointment appointment) throws SQLException {
        // Chiama il costruttore della classe base usando i getter di Appointment
        super(appointment.getId(),
                appointment.getService(),
                appointment.getDate(),
                appointment.getTime(),
                appointment.getDuration(),
                appointment.getStaffId(),
                appointment.getClientId(),
                appointment.getCancellation());

        // Inizializza i nuovi campi con valori predefiniti
        Staff tempStaff = daoStaff.select(appointment.getStaffId());
        this.staffName = tempStaff.getName();
        this.staffSurname = tempStaff.getSurname();
        Client tempClient = DAOClient.getClient(appointment.getClientId());
        this.clientName = tempClient.getName();
        this.clientSurname = tempClient.getSurname();
    }

    @Override
    public String toString() {
        return this.getId() + "," + this.getService() + "," + this.getDate() + ","
                + this.getTime() + "," + this.getDuration() + "," + this.getStaffId() + ","
                + this.staffName + "," + this.staffSurname + "," + this.getClientId() + ","
                + this.clientName + "," + this.clientSurname + ","
                + (!this.getCancellation() ? "NO" : (this.getCancellation() ? "YES" : "NO"));
    }

}
