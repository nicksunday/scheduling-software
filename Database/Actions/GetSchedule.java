package Database.Actions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Database.DatabaseConnection;

import static Database.DatabaseConnection.conn;

public class GetSchedule {
    static String baseQuery = "SELECT \n" +
            "    a.appointmentId, \n" +
            "    a.type, \n" +
            "    a.start, \n" +
            "    a.end, \n" +
            "    u.userName, \n" +
            "    c.customerName \n" +
            "FROM \n" +
            "    appointment a \n" +
            "INNER JOIN \n" +
            "    customer c on a.customerId = c.customerId \n" +
            "INNER JOIN \n" +
            "    user u on a.userId = u.userId \n";

    public static ResultSet thisWeek(String username) {
        String query = baseQuery + " \n" +
                "WHERE \n" +
                "    u.userName = ? \n" +
                "AND \n" +
                "    YEARWEEK(a.start, 0) = YEARWEEK(CURDATE(), 0)" +
                "ORDER BY " +
                "    a.start ASC;";

        //System.out.println(query);
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            //Statement statement = conn.createStatement();
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet thisMonth(String username) {
        String query = baseQuery + " \n" +
                "WHERE \n" +
                "   u.userName = ? \n" +
                "AND \n" +
                "   MONTH(a.start) = MONTH(CURRENT_DATE()) \n" +
                "AND \n" +
                "   YEAR(a.start) = YEAR(CURRENT_DATE())" +
                "ORDER BY" +
                "   a.start ASC;";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            //Statement statement = conn.createStatement();
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean nearLoginTime () {
        String query = "SELECT * FROM appointment WHERE start " +
                "BETWEEN NOW() - INTERVAL 15 MINUTE AND NOW() + INTERVAL 15 MINUTE;";
        ResultSet result = null;
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            result = statement.executeQuery();
            return result.isBeforeFirst();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        // For debugging
        try {
            DatabaseConnection.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResultSet results = thisWeek("test");
        try {
            System.out.println("TYPE\tSTART\tEND\tUSER\tCUSTOMER");
            while (results.next()) {
                System.out.println(
                        results.getString("type") + "\t" +
                        results.getDate("start") + "\t" +
                        results.getDate("end") + "\t" +
                        results.getString("userName") + "\t" +
                        results.getString("customerName")
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                DatabaseConnection.closeDatabaseConnection();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        try {
            DatabaseConnection.closeDatabaseConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
