package it.unicas.clinic.address.model.dao.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
/**
 * Database settings
 */
public class DAOMySQLSettings {
    public final static String DRIVERNAME = "com.mysql.cj.jdbc.Driver";
    public final static String HOST = "localhost";
    public final static String USERNAME = "staff_manager";
    public final static String PWD = "PasswordBella123";
    public final static String SCHEMA = "clinic";
    public final static String PARAMETERS = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";


    //String url = "jdbc:mysql://localhost:3306/amici?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";


    //private String driverName = "com.mysql.cj.jdbc.Driver";

    //Default SQL settings fields
    private String host = "localhost";
    private String userName = "staff_manager";
    private String pwd = "PasswordBella123!";
    private String schema = "clinic";

    /**
     * Getter of host
     */
    public String getHost() {
        return host;
    }

    /**
     * Getter of userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Getter of pwd
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * Getter of schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Setter of host:
     * it makes it equal to the one passed by argument
     * @param host: wanted database host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Setter of userName:
     * it makes it equal to the one passed by argument
     * @param userName: wanted username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    /**
     * Setter of pwd:
     * it makes it equal to the one passed by argument
     * @param pwd: wanted database password
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    /**
     * Setter of schema:
     * it makes it equal to the one passed by argument
     * @param schema: wanted schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    static {
        try {
            Class.forName(DRIVERNAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static DAOMySQLSettings currentDAOMySQLSettings = null;

    /**
     * Getter of the SQL database settings
     */
    public static DAOMySQLSettings getCurrentDAOMySQLSettings() {
        if (currentDAOMySQLSettings == null) {
            currentDAOMySQLSettings = getDefaultDAOSettings();
        }
        return currentDAOMySQLSettings;
    }

    /**
     * Set default settings for the database and returns them
     */
    public static DAOMySQLSettings getDefaultDAOSettings() {
        DAOMySQLSettings daoMySQLSettings = new DAOMySQLSettings();
        daoMySQLSettings.host = HOST;
        daoMySQLSettings.userName = USERNAME;
        daoMySQLSettings.schema = SCHEMA;
        daoMySQLSettings.pwd = PWD;
        return daoMySQLSettings;
    }

    /**
     * Set the database settings equal to the ones passed by argument
     */
    public static void setCurrentDAOMySQLSettings(DAOMySQLSettings daoMySQLSettings) {
        currentDAOMySQLSettings = daoMySQLSettings;
    }

    /**
     * Establish a connection to the server based on the default settings.
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        if (currentDAOMySQLSettings == null) {
            currentDAOMySQLSettings = getDefaultDAOSettings();
        }
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/clinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", currentDAOMySQLSettings.userName, currentDAOMySQLSettings.pwd);
    }

    /**
     * Close the connection passed by argument.
     * @param connection: connection to be closed
     * @throws SQLException
     */
    public static void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    /**
     * Returns a String corresponding to the hashed version of the password passed by argument.
     * @param pass: normal password.
     * @return
     */
    public static String hashPassword(String pass){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(pass);
    }
    public static void main(String[] args) throws SQLException {
        DAOMySQLSettings dao = DAOMySQLSettings.getDefaultDAOSettings();
    }
}