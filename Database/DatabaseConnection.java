package Database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    /*
    private static final String URL = "jdbc:mysql:/URL/DATABASE";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    */
    public static Connection conn;

    public static void connectToDatabase() throws ClassNotFoundException, SQLException, Exception {
        Class.forName(DRIVER_CLASS);
        conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        System.out.println("Connection Successful");
    }

    public static void closeDatabaseConnection() throws ClassNotFoundException, SQLException, Exception {
        conn.close();
        System.out.println("Connection Closed");
    }
}
