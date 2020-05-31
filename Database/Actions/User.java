package Database.Actions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static GUI.Login.Login.username;
import static Database.DatabaseConnection.conn;

public class User {

    public static ResultSet getAllUsers() {
        ResultSet result = null;
        String query = "SELECT * FROM user";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            result = statement.executeQuery();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ResultSet getActiveUsers() {
        ResultSet result = null;
        String query = "SELECT * FROM user WHERE active = 1";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            result = statement.executeQuery();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean add(String userName, String password, boolean active) {
        String user;
        if (username == null) {
            user = userName;
        }
        else {
            user = username;
        }

        String insert = "INSERT INTO user (" +
                "userName," +
                "password," +
                "active," +
                "createDate," +
                "createdBy," +
                "lastUpdate," +
                "lastUpdateBy" +
                ") " +
                "VALUES (?, ?, ?, NOW(), ?, NOW(), ?)";

        try {
            PreparedStatement statement = conn.prepareStatement(insert);
            statement.setString(1, userName);
            statement.setString(2, password);
            if (active) {
                statement.setInt(3, 1);
            }
            else {
                statement.setInt(3,0);
            }
            statement.setString(4, user);
            statement.setString(5, user);
            statement.execute();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean add(String userName, String password) {
        return add(userName, password, true);
    }

    public static int getUserId(String userName) {
        String query = "SELECT userId FROM user WHERE userName=?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, userName);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                return result.getInt("userId");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }
}
