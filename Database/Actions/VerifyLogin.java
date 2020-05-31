package Database.Actions;

import java.sql.ResultSet;
import java.sql.Statement;

import static Database.DatabaseConnection.conn;

public class VerifyLogin {

    public static boolean authenticate (String username, String password) {
        Statement statement;
        ResultSet result;

        String userQuery = "SELECT COUNT(*) FROM user WHERE userName = '" + username + "' and password = '" + password + "'";
        // Debugging
        // System.out.println(userQuery);
        try {
            statement = conn.createStatement();
            result = statement.executeQuery(userQuery);
            result.next();
            // Debugging
            // System.out.println(result.getInt(1));
            if (result.getInt(1) > 0) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
