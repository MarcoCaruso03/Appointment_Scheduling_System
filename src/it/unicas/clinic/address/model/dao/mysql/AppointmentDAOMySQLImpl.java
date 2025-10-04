package it.unicas.clinic.address.model.dao.mysql;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import it.unicas.clinic.address.model.Schedule;
import it.unicas.clinic.address.model.dao.AppointmentDAO;
import it.unicas.clinic.address.model.dao.AppointmentException;
import it.unicas.clinic.address.model.Appointment;
import it.unicas.clinic.address.utils.DataUtil;

/**
 * Contains the implementation of the interaction between application and database for Appointment table.
 */
public class AppointmentDAOMySQLImpl implements AppointmentDAO<Appointment>{

    private static AppointmentDAO dao = null;
    private static Logger logger = null;

    public AppointmentDAOMySQLImpl() {}

    public static AppointmentDAO getInstance() {
        if (dao == null){
            dao = new AppointmentDAOMySQLImpl();
            logger = Logger.getLogger(AppointmentDAOMySQLImpl.class.getName());
        }
        return dao;
    }

    /**
     * Returns a List of Appointments similar to the one passed by argument.
     * @param s: passed argument.
     * @return
     * @throws AppointmentException
     */
    @Override
    public List<Appointment> select(Appointment s) throws AppointmentException {
        // If passed appointment is null create new one with null fields
        if(s==null)
            s = new Appointment(0,null,null, null,null,0,0);

        ArrayList<Appointment> list = new ArrayList<>();

        if(s.getId()<=0 && s.getService()==null && s.getDate()==null && s.getTime()==null & s.getStaffId()==0 && s.getClientId()==0){
            s= new Appointment(0,null,null,null,null,0,0);
        }
        String sqlSelect = "SELECT * FROM appointment WHERE cancellation IS NULL ";

        // Add dynamicly the condition of the fields not null
        if (s.getId() > 0) {
            sqlSelect += " AND id = ?";
        }
        if (s.getService() != null && !s.getService().isEmpty()) {
            sqlSelect += " AND service LIKE ?";
        }
        if (s.getDate() != null) {
            sqlSelect += " AND date LIKE ?";
        }
        if (s.getTime() != null) {
            sqlSelect += " AND time LIKE ?";
        }
        if(s.getStaffId() > 0){
            sqlSelect += " AND staff_id = ?";
        }
        if(s.getClientId() > 0){
            sqlSelect += " AND client_id = ?";
        }
        // Log final query
        logger.info("SQL Query: " + sqlSelect);

        // Prepare PreparedStatement
        try (Connection con = DAOMySQLSettings.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlSelect)) {

            // Set the params dinamicly
            int index = 1; // index of the parms of the preparedStatement

            if (s.getId() > 0) {
                stmt.setInt(index++, s.getId());  //set ID
            }
            if(s.getService()!=null && !s.getService().isEmpty()){
                stmt.setString(index++, s.getService()+"%");
            }
            if (s.getDate() != null) {
                stmt.setDate(index++, Date.valueOf(s.getDate()));  // set the day
            }
            if (s.getTime() != null) {
                stmt.setTime(index++, Time.valueOf(s.getTime()));  // set start time
            }
            if (s.getStaffId()>0) {
                stmt.setInt(index++, s.getStaffId());  // set final time
            }
            if (s.getClientId() > 0) {
                stmt.setInt(index, s.getClientId());  // set staffId
            }

            // execute the query
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // create an object with the result
                    Appointment a1 = new Appointment(
                            rs.getInt("id"),
                            rs.getString("service"),
                            rs.getDate("date").toLocalDate(),
                            rs.getTime("time").toLocalTime(),
                            rs.getTime("duration").toLocalTime(),
                            rs.getInt("staff_id"),
                            rs.getInt("client_id"),
                            rs.getBoolean("cancellation"),
                            rs.getBoolean("notice")
                    );
                    list.add(a1);  // add the object in the list
                }
            }

            if (!list.isEmpty()) {
                logger.info("Query executed successfully: " + sqlSelect + " | Number of records found: " + list.size());
            } else {
                logger.info("Query executed successfully: " + sqlSelect + " | No records found.");
            }
        } catch (SQLException e) {
            logger.severe("SQL Error: " + e.getMessage());
            throw new AppointmentException("SQL: In select(): An error occurred while fetching appointment data");
        }
        return list;
    }

    @Override
    public void update(Appointment s) throws AppointmentException {
        if(s!=null){
            try{
                Connection connection = DAOMySQLSettings.getConnection();
                String sqlUpdate = "update appointment set service=?, date=?, time=?, staff_id=?, client_id=? where id=?";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
                preparedStatement.setString(1, s.getService());
                preparedStatement.setDate(2, Date.valueOf(s.getDate()));
                preparedStatement.setTime(3, Time.valueOf(s.getTime()));
                preparedStatement.setInt(4, s.getStaffId());
                preparedStatement.setInt(5, s.getClientId());
                preparedStatement.setInt(6, s.getId());

                preparedStatement.executeUpdate();
                connection.close();
            }catch(SQLException e){
                logger.severe("Something went wrong while updating appointment");
            }

        }
        else
            logger.severe("Appointment in update is null");

    }

    @Override
    public void insert (Appointment s) throws AppointmentException {
        if(s!=null) {
            try {
                Connection connection = DAOMySQLSettings.getConnection();
                String sqlInsert = "insert into appointment (service, date, time, duration, staff_id, client_id) VALUES (?,?,?,?,?,?)";
                PreparedStatement preparedstatement = connection.prepareStatement(sqlInsert);
                preparedstatement.setString(1, s.getService());
                preparedstatement.setDate(2, Date.valueOf(s.getDate()));
                preparedstatement.setTime(3, Time.valueOf(s.getTime()));
                preparedstatement.setTime(4, Time.valueOf(s.getDuration()));
                preparedstatement.setInt(5, s.getStaffId());
                preparedstatement.setInt(6, s.getClientId());
                preparedstatement.execute();
                connection.close();
            } catch (SQLException e) {
                logger.severe("Something went wrong in inserting new appointment");
            }
        }
        else
            logger.severe("Inserted appointment is null");
    }

    /**
     * Set the cancellation field TRUE inside the database.
     * Thanks to the cancellation field the app can't see the appointments subject to soft deletion.
     * @param s: appointment to be "deleted"
     * @throws AppointmentException
     */
    @Override
    public void softDelete(int s) throws AppointmentException {
        if(s>0) {
            try {
                Connection connection = DAOMySQLSettings.getConnection();
                //String sqlInsert = "delete from appointment where id=?";
                String sqlInsert = "UPDATE appointment SET cancellation = 1 WHERE id = ?";
                PreparedStatement preparedstatement = connection.prepareStatement(sqlInsert);
                preparedstatement.setInt(1, s);
                preparedstatement.executeUpdate();
                connection.close();
            } catch (SQLException e) {
                logger.severe("Something went wrong in deleting (selected id might not exist)");
            }
        }
        else
            logger.severe("Id is invalid");
    }

    /**
     * Effectively deletes the appointment from the database.
     * @param s: appointment to be deleted.
     * @throws AppointmentException
     */
    @Override
    public void delete(int s) throws AppointmentException {
        if(s>0) {
            try {
                Connection connection = DAOMySQLSettings.getConnection();
                //String sqlInsert = "delete from appointment where id=?";
                String sqlInsert = "DELETE FROM appointment WHERE id = ?";
                PreparedStatement preparedstatement = connection.prepareStatement(sqlInsert);
                preparedstatement.setInt(1, s);
                preparedstatement.executeUpdate();
                connection.close();
            } catch (SQLException e) {
                logger.severe("Something went wrong in deleting (selected id might not exist)");
            }
        }
        else
            logger.severe("Id is invalid");
    }

    /**
     * Returns the last appointment added to the database.
     * If the database is empty, return NULL.
     * @return
     * @throws SQLException
     */
    public Appointment getLastApp() throws SQLException {
        Appointment a = new Appointment(0, null, null,null, null,0,0);
        Connection connection = DAOMySQLSettings.getConnection();
        //Define command
        String searchUser = "select * from appointment order by id desc limit 1";
        PreparedStatement command = connection.prepareStatement(searchUser);
        //Execute command
        ResultSet result = command.executeQuery();

        if(result.next()){
            a.setService(result.getString("service"));
            a.setDate(DataUtil.parseToDate(result.getString("date")));
            a.setTime(DataUtil.parseToTime(result.getString("time"),false));
            a.setDuration(DataUtil.parseToDuration(result.getString("duration"),false));
            a.setId(result.getInt("id"));
            a.setStaffId(result.getInt("staff_id"));
            a.setClientId(result.getInt("client_id"));
        }
        connection.close();
        return a;
    }

    /**
     * Returns an ArrayList of Appointment associated with a particular schedule.
     * @param s: schedule.
     * @return
     * @throws SQLException
     */
    public ArrayList<Appointment> getSchedApp(Schedule s) throws SQLException {
        ArrayList<Appointment> appointments = new ArrayList<>();
        Connection connection = DAOMySQLSettings.getConnection();
        //Define command
        String searchApp="select * from appointment where staff_id=? and date=? and cancellation is null";
        PreparedStatement preparedStatement = connection.prepareStatement(searchApp);
        preparedStatement.setInt(1, s.getStaffId());
        preparedStatement.setDate(2,Date.valueOf(s.getDay()));
        ResultSet result = preparedStatement.executeQuery();
        while(result.next()){
            // create an object with the result
            Appointment a1 = new Appointment(
                    result.getInt("id"),
                    result.getString("service"),
                    result.getDate("date").toLocalDate(),
                    result.getTime("time").toLocalTime(),
                    result.getTime("duration").toLocalTime(),
                    result.getInt("staff_id"),
                    result.getInt("client_id")
            );
            appointments.add(a1);  // add the object in the list
        }
        connection.close();
        if(appointments.isEmpty())
            return null;
        else
            return appointments;
    }

    /**
     * Returns a List of all past Appointment of a specific client.
     * @param client_id: id of the client.
     * @return
     * @throws SQLException
     */
    public List<Appointment> getHistoryApp(int client_id) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        if(client_id<=0)
            return null;
        Connection connection = DAOMySQLSettings.getConnection();
        String searchApp="select * from appointment where client_id=? and date<=?";
        PreparedStatement preparedStatement = connection.prepareStatement(searchApp);
        preparedStatement.setInt(1, client_id);
        preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
        ResultSet result = preparedStatement.executeQuery();
        while(result.next()){
            Appointment a = new Appointment(
                    result.getInt("id"),
                    result.getString("service"),
                    result.getDate("date").toLocalDate(),
                    result.getTime("time").toLocalTime(),
                    result.getTime("duration").toLocalTime(),
                    result.getInt("staff_id"),
                    result.getInt("client_id")
            );
            appointments.add(a);
        }
        if(appointments.isEmpty())
            return null;
        else
            return appointments;
    }

    /**
     * Returns a List of Appointments similar to the one passed by arguments.
     * The two LocalDate variables sets the time window in which the search is executed.
     * @param s: appointment.
     * @param startDate: start of time window.
     * @param endDate: end of time window.
     * @return
     * @throws AppointmentException
     */
    @Override
    public List<Appointment> select(Appointment s, LocalDate startDate, LocalDate endDate) throws AppointmentException {
        // If passed appointment is null, create a new one with null fields
        if (s == null)
            s = new Appointment(0, null, null, null, null, 0, 0);

        ArrayList<Appointment> list = new ArrayList<>();

        if (s.getId() <= 0 && s.getService() == null && s.getDate() == null && s.getTime() == null && s.getStaffId() == 0 && s.getClientId() == 0) {
            s = new Appointment(0, null, null, null, null, 0, 0);
        }

        String sqlSelect = "SELECT * FROM appointment WHERE 1=1";

        // Add dynamically the condition of the fields not null
        if (s.getId() > 0) {
            sqlSelect += " AND id = ?";
        }
        if (s.getService() != null && !s.getService().isEmpty()) {
            sqlSelect += " AND service LIKE ?";
        }
        if (s.getDate() != null) {
            sqlSelect += " AND date LIKE ?";
        }
        if (s.getTime() != null) {
            sqlSelect += " AND time LIKE ?";
        }
        if (s.getStaffId() > 0) {
            sqlSelect += " AND staff_id = ?";
        }
        if (s.getClientId() > 0) {
            sqlSelect += " AND client_id = ?";
        }
        if (startDate != null) {
            sqlSelect += " AND date >= ?";
        }
        if (endDate != null) {
            sqlSelect += " AND date <= ?";
        }

        // Log final query
        logger.info("SQL Query: " + sqlSelect);

        // Prepare PreparedStatement
        try (Connection con = DAOMySQLSettings.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlSelect)) {

            // Set the params dynamically
            int index = 1; // index of the params of the preparedStatement

            if (s.getId() > 0) {
                stmt.setInt(index++, s.getId()); // set ID
            }
            if (s.getService() != null && !s.getService().isEmpty()) {
                stmt.setString(index++, s.getService());
            }
            if (s.getDate() != null) {
                stmt.setDate(index++, Date.valueOf(s.getDate())); // set the day
            }
            if (s.getTime() != null) {
                stmt.setTime(index++, Time.valueOf(s.getTime())); // set start time
            }
            if (s.getStaffId() > 0) {
                stmt.setInt(index++, s.getStaffId()); // set staffId
            }
            if (s.getClientId() > 0) {
                stmt.setInt(index++, s.getClientId()); // set clientId
            }
            if (startDate != null) {
                stmt.setDate(index++, Date.valueOf(startDate)); // set startDate
            }
            if (endDate != null) {
                stmt.setDate(index++, Date.valueOf(endDate)); // set endDate
            }

            // execute the query
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // create an object with the result
                    Appointment a1 = new Appointment(
                            rs.getInt("id"),
                            rs.getString("service"),
                            rs.getDate("date").toLocalDate(),
                            rs.getTime("time").toLocalTime(),
                            rs.getTime("duration").toLocalTime(),
                            rs.getInt("staff_id"),
                            rs.getInt("client_id"),
                            rs.getBoolean("cancellation"),
                            rs.getBoolean("notice")
                    );
                    list.add(a1); // add the object in the list
                }
            }

            if (!list.isEmpty()) {
                logger.info("Query executed successfully: " + sqlSelect + " | Number of records found: " + list.size());
            } else {
                logger.info("Query executed successfully: " + sqlSelect + " | No records found.");
            }
        } catch (SQLException e) {
            logger.severe("SQL Error: " + e.getMessage());
            throw new AppointmentException("SQL: In select(): An error occurred while fetching appointment data");
        }

        return list;
    }

    /**
     * Returns a List of Appointment before the date passed by argument.
     * @param d: date.
     * @return
     * @throws RuntimeException
     */
    @Override
    public List<Appointment> getPastApp(LocalDate d) throws RuntimeException{
        String query = "SELECT * FROM appointment WHERE  date <= ?";
        List<Appointment> appList = new ArrayList<>();
        try (Connection con = DAOMySQLSettings.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(d));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Appointment a = new Appointment(
                            rs.getInt("id"),
                            rs.getString("service"),
                            rs.getDate("date").toLocalDate(),
                            rs.getTime("time").toLocalTime(),
                            rs.getTime("duration").toLocalTime(),
                            rs.getInt("staff_id"),
                            rs.getInt("client_id"),
                            rs.getBoolean("cancellation")
                    );
                    appList.add(a);
                }
            }
        } catch (SQLException e) {
            logger.severe("Error selecting old appointment: " + e.getMessage());
            throw new RuntimeException("Error selecting old appointment", e);
        }
        return appList;

    }
    @Override
    public List<Appointment> getfutureAppStaff(LocalDate d, int id) throws RuntimeException{
        String query = "SELECT * FROM appointment WHERE  date >= ? AND staff_id=?";
        List<Appointment> appList = new ArrayList<>();
        try (Connection con = DAOMySQLSettings.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(d));
            stmt.setInt(2, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Appointment a = new Appointment(
                            rs.getInt("id"),
                            rs.getString("service"),
                            rs.getDate("date").toLocalDate(),
                            rs.getTime("time").toLocalTime(),
                            rs.getTime("duration").toLocalTime(),
                            rs.getInt("staff_id"),
                            rs.getInt("client_id"),
                            rs.getBoolean("cancellation")
                    );
                    appList.add(a);
                }
            }
        } catch (SQLException e) {
            logger.severe("Error selecting old appointment: " + e.getMessage());
            throw new RuntimeException("Error selecting old appointment", e);
        }
        return appList;

    }

    @Override
    public List<Appointment> getfutureAppClient(LocalDate d, int id) throws RuntimeException{
        String query = "SELECT * FROM appointment WHERE  date >= ? AND client_id=?";
        List<Appointment> appList = new ArrayList<>();
        try (Connection con = DAOMySQLSettings.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(d));
            stmt.setInt(2, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Appointment a = new Appointment(
                            rs.getInt("id"),
                            rs.getString("service"),
                            rs.getDate("date").toLocalDate(),
                            rs.getTime("time").toLocalTime(),
                            rs.getTime("duration").toLocalTime(),
                            rs.getInt("staff_id"),
                            rs.getInt("client_id"),
                            rs.getBoolean("cancellation")
                    );
                    appList.add(a);
                }
            }
        } catch (SQLException e) {
            logger.severe("Error selecting future appointment: " + e.getMessage());
            throw new RuntimeException("Error selecting future appointment", e);
        }
        return appList;

    }


    @Override
    public ArrayList<Appointment> getTomorrowApp() throws SQLException {
        ArrayList<Appointment> apps = new ArrayList<>();
        Connection connection = DAOMySQLSettings.getConnection();
        String command = "select * from appointment where date=?";
        PreparedStatement preparedStatement = connection.prepareStatement(command);
        preparedStatement.setDate(1, Date.valueOf(LocalDate.now().plusDays(1)));
        ResultSet result = preparedStatement.executeQuery();
        while(result.next()){
            Appointment a = new Appointment(
                    result.getInt("id"),
                    result.getString("service"),
                    result.getDate("date").toLocalDate(),
                    result.getTime("time").toLocalTime(),
                    result.getTime("duration").toLocalTime(),
                    result.getInt("staff_id"),
                    result.getInt("client_id"),
                    result.getBoolean("cancellation"),
                    result.getBoolean("notice")
            );
            apps.add(a);
        }
        connection.close();
        if(apps.isEmpty())
            return null;
        else
            return apps;
    }
    @Override
    public void setNotice(Appointment appointment) throws SQLException {
        Connection connection = DAOMySQLSettings.getConnection();
        String command = "UPDATE appointment SET notice = TRUE WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(command);
        preparedStatement.setInt(1, appointment.getId());
        preparedStatement.executeUpdate();
        connection.close();
    }

    public static void main(String[] args) throws SQLException {

    }

}
