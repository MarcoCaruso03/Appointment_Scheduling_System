package it.unicas.clinic.address.model.dao.mysql;

import it.unicas.clinic.address.model.Client;
import it.unicas.clinic.address.model.Schedule;
import it.unicas.clinic.address.model.Staff;
import it.unicas.clinic.address.model.dao.ScheduleException;
import it.unicas.clinic.address.model.dao.StaffDAO;
import it.unicas.clinic.address.model.dao.StaffException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Contains the implementation of the interaction between application and database for Staff table.
 */
public class StaffDAOMySQLImpl implements StaffDAO<Staff> {

    private static StaffDAO dao = null;
    private static Logger logger = null;

    private StaffDAOMySQLImpl(){}

    public static StaffDAO getInstance(){
        if (dao == null){
            dao = new StaffDAOMySQLImpl();
            logger = Logger.getLogger(StaffDAOMySQLImpl.class.getName());
        }
        return dao;
    }

    @Override
    public void update(Staff s) throws StaffException {
        verifyStaff(s);
        String sqlUpdate = "UPDATE staff SET name = ?, surname = ?, specialties = ? WHERE id = ?";
        try (Connection con = DAOMySQLSettings.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(sqlUpdate)) {
            preparedStatement.setString(1, s.getName());
            preparedStatement.setString(2, s.getSurname());
            preparedStatement.setString(3, s.getSpecialties());
            preparedStatement.setInt(4, s.getId());

            int rowAffected=preparedStatement.executeUpdate();
            logger.info("Query executed successfully: " + sqlUpdate);
            if(rowAffected==0){
                logger.info("No staff found with id " + s.getId());
            }
        } catch (SQLException e) {
            logger.severe(("SQL: In update(): An error occurred while updating staff data"));
            throw new StaffException("SQL: In update(): An error occurred while updating staff data");
        }
    }

    @Override
    public void insert(Staff s) throws StaffException {
        // Verifica l'oggetto Staff
        verifyStaff(s);

        // Creiamo la query per l'inserimento dello Staff
        String sqlInsertStaff = "INSERT INTO staff (name, surname, specialties) VALUES(?, ?, ?)";

        try (Connection con = DAOMySQLSettings.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(sqlInsertStaff, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, s.getName());
            preparedStatement.setString(2, s.getSurname());
            preparedStatement.setString(3, s.getSpecialties());

            preparedStatement.executeUpdate();

            logger.info("Query executed successfully: " + sqlInsertStaff);

        } catch (SQLException e) {
            logger.severe("SQL: In insert(): An error occurred while inserting staff data, connection problem");
            throw new StaffException("SQL: In insert(): An error occurred while inserting staff data, connection problem");
        }
    }

    /**
     * Effectively deletes the specified staff from database.
     * @param s: specific staff.
     * @throws StaffException
     */
    @Override
    public void delete(Staff s) throws StaffException {
        if(s == null || s.getId() <= 0){
            throw new StaffException("SQL: In delete(): Staff object cannot be null or with an invalid id ");
        }
        String sqlDelete = "DELETE FROM staff WHERE id = ? ";
        try (Connection con = DAOMySQLSettings.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(sqlDelete)) {
            preparedStatement.setInt(1, s.getId());
            int rowAffected=preparedStatement.executeUpdate();
            logger.info("Query executed successfully: " + sqlDelete);
            if(rowAffected==0){
                logger.info("No staff found with id " + s.getId());
            }
        }catch(SQLException e){
            logger.severe(("SQL: In delete(): An error occurred while deleting staff data"));
            //logger.severe("SQL: In delete(): Error - " + e.getMessage() + " | SQLState: " + e.getSQLState() + " | ErrorCode: " + e.getErrorCode());

            throw new StaffException("SQL: In delete(): An error occurred while deleting staff data");
        }
    }

    /**
     * Returns a List of Staff similar to the one passed by argument.
     * @param s: specific staff.
     * @return
     */
    @Override
    public List<Staff> select(Staff s) {
        // if the object is null, create a Stuff with default value (0, null, null, null, null)
        if (s == null) {
            s = new Staff(0, "", "", ""); //select all
        }

        ArrayList<Staff> list = new ArrayList<>();

        // If all the fields are null o 0 => select all
        if (s.getId() <= 0 && s.getName() == null && s.getSurname() == null && s.getSpecialties() == null) {
            s = new Staff(0, null, null, null);
        }

        String sqlSelect = "SELECT * FROM staff WHERE firedDate is null";

        // Add dynamicly the condition of the fields not null
        if (s.getId() > 0) {
            sqlSelect += " AND id = ?";
        }
        if (s.getName() != null && !s.getName().isEmpty()) {
            sqlSelect += " AND name LIKE ?";
        }
        if (s.getSurname() != null && !s.getSurname().isEmpty()) {
            sqlSelect += " AND surname LIKE ?";
        }
        if (s.getSpecialties() != null && !s.getSpecialties().isEmpty()) {
            sqlSelect += " AND specialties LIKE ?";
        }

        // Log final query
        logger.info("SQL Query: " + sqlSelect);

        // Prepare PreparedStatement
        try (Connection con = DAOMySQLSettings.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlSelect)) {

            // Set the params dinamicly
            int index = 1; // index of the parms of the preparedStatement

            if (s.getId() > 0) {
                stmt.setInt(index++, s.getId());  //set l'ID
            }
            if (s.getName() != null && !s.getName().isEmpty()) {
                stmt.setString(index++, "%" + s.getName() + "%");  // set name
            }
            if (s.getSurname() != null && !s.getSurname().isEmpty()) {
                stmt.setString(index++, "%" + s.getSurname() + "%");  // set surname
            }
            if (s.getSpecialties() != null && !s.getSpecialties().isEmpty()) {
                stmt.setString(index++, "%" + s.getSpecialties() + "%");  // set speciality
            }

            //  execute the query
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // create an object with the result
                    Staff s1 = new Staff(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("specialties")
                    );
                    list.add(s1);  // add the object in the list
                }
            }

            if (!list.isEmpty()) {
                logger.info("Query executed successfully: " + sqlSelect + " | Number of records found: " + list.size());

            } else {
                logger.info("Query executed successfully: " + sqlSelect + " | No records found.");
                return null;
            }
        } catch (SQLException e) {
            logger.severe("SQL Error: " + e.getMessage());
            throw new StaffException("SQL: In select(): An error occurred while fetching staff data");
        }

        return list;  // Ritorna la lista di risultati
    }

    private void verifyStaff(Staff s) throws StaffException {
        //we want all not null and with a "meaning"
            if (s == null || s.getName() == null || s.getSurname() == null
                    || s.getSpecialties() == null) {
                throw new StaffException("Can not to continue because that staff member has some null field");
            }
    }


    public static void main(String args[]) throws StaffException, SQLException{
    }

    public Staff getLastStaff() throws SQLException {
        Staff s = new Staff(0, null, null, null);
        Connection connection = DAOMySQLSettings.getConnection();
        //Define command
        String searchUser = "select * from staff order by id desc limit 1";
        PreparedStatement command = connection.prepareStatement(searchUser);
        //Execute command
        ResultSet result = command.executeQuery();

        if(result.next()){
            s.setName(result.getString("name"));
            s.setSurname(result.getString("surname"));
            s.setSpecialties(result.getString("specialties"));
            s.setId(result.getInt("id"));
        }
        connection.close();
        return s;
    }

    /**
     * Returns the Staff corresponding to the specified id.
     * If the id doesn't exist it returns null.
     * @param id: staff id.
     * @return
     * @throws SQLException
     */
    @Override
    public  Staff select(int id) throws SQLException {
        if(id<=0){
            return null;
        }
        else{
            Connection connection = DAOMySQLSettings.getConnection();
            String searchStaff = "select * from staff where id = ?";
            PreparedStatement command = connection.prepareStatement(searchStaff);
            command.setInt(1, id);
            ResultSet result = command.executeQuery();
            if(result.next()){
                Staff s = new Staff();
                s.setId(result.getInt("id"));
                s.setName(result.getString("name"));
                s.setSurname(result.getString("surname"));
                s.setSpecialties(result.getString("specialties"));
                return s;
            }
            else
                return null;
        }
    }

    /**
     * Sets the firedDate equal to the current date.
     * In this way the application
     * @param s: specific staff.
     * @throws StaffException
     */
    @Override
    public void softDelete(Staff s) throws StaffException {
        if (s.getId() <= 0) {
            throw new StaffException("Invalid staff ID.");
        }
        String sqlUpdateFiredDate = "UPDATE staff SET firedDate = ? WHERE id = ?";
        try (Connection con = DAOMySQLSettings.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(sqlUpdateFiredDate)) {

            // Set the firing data
            LocalDate today = LocalDate.now();
            preparedStatement.setDate(1, Date.valueOf(today));
            preparedStatement.setInt(2, s.getId());

            // to the update
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new StaffException("No staff found with ID " + s.getId());
            }



            logger.info("Successfully set firedDate for staff with ID: " + s.getId());
        } catch (SQLException e) {
            logger.severe("SQL: Error setting firedDate for staff with ID: " + s.getId() + ". " +
                    "Error: " + e.getMessage());
            throw new StaffException("SQL: Error setting firedDate for staff with ID: " + s.getId());
        }
    }

    /**
     * Returns a List of soft-deleted Staff before a specific date.
     * @param date: specific date.
     * @return
     */
    @Override
    public List<Staff> selectFiredBefore(LocalDate date) {
        String query = "SELECT * FROM staff WHERE firedDate IS NOT NULL AND firedDate <= ?";
        List<Staff> staffList = new ArrayList<>();
        try (Connection con = DAOMySQLSettings.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Staff staff = new Staff(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("specialties"),
                            rs.getDate("firedDate").toLocalDate()
                    );
                    staffList.add(staff);
                }
            }
        } catch (SQLException e) {
            logger.severe("Error selecting old staff: " + e.getMessage());
            throw new RuntimeException("Error selecting old staff", e);
        }
        return staffList;
    }
}
