package Database.Actions;

import Database.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;

import static Database.DatabaseConnection.conn;

public class Report {

    public static String uniqueApptTypeByMonth(int month) {
        String monthName = new DateFormatSymbols().getMonths()[month - 1];
        String numOfAppointments = null;
        ResultSet result;
        String query = "SELECT\n" +
                "    COUNT(DISTINCT type)\n" +
                "FROM\n" +
                "    appointment\n" +
                "WHERE\n" +
                "    MONTH(start) = ?\n" +
                "AND\n" +
                "    YEAR(start) = YEAR(CURRENT_DATE())" ;

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, month);
            result = statement.executeQuery();
            result.next();
            int appts = result.getInt(1);
            String front = null;
            String back = null;
            if (appts == 1) {
                front = "There is ";
                back = " unique appointment in ";
            }
            else {
                front = "There are ";
                back = " unique appointments in ";
            }
            numOfAppointments = front + appts + back + monthName;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return numOfAppointments;
    }

    public static String uniqueApptTypeByMonth() {
        LocalDateTime curr = LocalDateTime.now();
        return uniqueApptTypeByMonth(curr.getMonthValue());
    }


    public static String activeCustomers() {
        String query = "SELECT COUNT(*) FROM customer WHERE active = 1";
        String activeCustomers = null;

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();
            int customers = result.getInt(1);
            String front = null;
            String back = null;
            if (customers == 1) {
                front = "There is ";
                back = " active customer";
            }
            else {
                front = "There are ";
                back = " active customers";
            }
            activeCustomers = front + String.valueOf(customers) + back;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return activeCustomers;
    }

    public static String activeUsers() {
        String query = "SELECT COUNT(*) FROM user WHERE active = 1";
        String activeUsers = null;

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();
            int users = result.getInt(1);
            String front = null;
            String back = null;
            if (users == 1) {
                front = "There is ";
                back = " active user";
            }
            else {
                front = "There are ";
                back = " active users";
            }
            activeUsers = front + String.valueOf(users) + back;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return activeUsers;
    }

    public static void main(String[] args) {
        // For debugging
        try {
            DatabaseConnection.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String appts = uniqueApptTypeByMonth();
        System.out.println(appts);
        System.out.println(activeCustomers());
        System.out.println(activeUsers());

        try {
            DatabaseConnection.closeDatabaseConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
