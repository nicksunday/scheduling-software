package Database.Actions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.*;
import java.time.format.DateTimeFormatter;
import Database.DatabaseConnection;

import static GUI.Login.Login.username;
import static Database.DatabaseConnection.conn;
import static java.time.LocalDateTime.*;

public class Appointment {

    public static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static LocalTime startTime = LocalTime.of(14,00);
    public static LocalTime endTime = LocalTime.of(22, 00);


    public static LocalDateTime convertToUtc(LocalDateTime timeToConvert) {
        ZoneId localTimeZone = ZoneId.systemDefault();
        ZonedDateTime zonedLocalTime = timeToConvert.atZone(localTimeZone);
        ZonedDateTime convertedTime = zonedLocalTime.withZoneSameInstant(ZoneId.ofOffset("UTC", ZoneOffset.UTC));
        return convertedTime.toLocalDateTime();
    }

    public static String add(
            int customerId,
            int userId,
            String type,
            LocalDateTime localStart,
            LocalDateTime localEnd
    ) {
        LocalDateTime start = convertToUtc(localStart);
        LocalDateTime end = convertToUtc(localEnd);
        String title = "not needed";
        String description = "not needed";
        String location = "not needed";
        String contact = "not needed";
        String url = "not needed";
        boolean overlaps = getOverlaps(start, end, userId, customerId);
        boolean insideBusinessHours = isInsideBusinessHours(start, end);

        if (overlaps) {
            return "overlaps";
        }
        else if (!insideBusinessHours) {
            return "outside business hours";
        }
        else {

            String insert = "INSERT INTO \n" +
                    "appointment " +
                    "(" +
                    "customerId, " +
                    "userId, " +
                    "title, " +
                    "description, " +
                    "location, " +
                    "contact, " +
                    "type, " +
                    "url, " +
                    "start, " +
                    "end, " +
                    "createDate, " +
                    "createdBy, " +
                    "lastUpdate, " +
                    "lastUpdateBy" +
                    ") " +
                    "VALUES " +
                    "(" +
                    customerId + "," +
                    userId + "," +
                    "'" + title + "'" + "," +
                    "'" + description + "'" + "," +
                    "'" + location + "'" + "," +
                    "'" + contact + "'" + "," +
                    "'" + type + "'" + "," +
                    "'" + url + "'" + "," +
                    "'" + start.format(FORMAT) + "'" + "," +
                    "'" + end.format(FORMAT) + "'" + "," +
                    "NOW()," +
                    "'" + username + "'" + "," +
                    "NOW()," +
                    "'" + username + "'" +
                    ");";

            try {
                PreparedStatement statement = conn.prepareStatement(insert);
                statement.execute();
                return "SUCCESS";
            } catch (Exception e) {
                e.printStackTrace();
                return "ERROR";
            }
        }
    }

    private static boolean getOverlaps(LocalDateTime start, LocalDateTime end, int userId, int customerId) {
        String query = "SELECT * FROM appointment " +
                "WHERE (start BETWEEN ? AND ? " +
                "OR end BETWEEN ? AND ?) AND (userId=? OR customerId=?)";
        ResultSet overlaps;
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, start.format(FORMAT));
            statement.setString(2, end.format(FORMAT));
            statement.setString(3, start.format(FORMAT));
            statement.setString(4, end.format(FORMAT));
            statement.setInt(5, userId);
            statement.setInt(6, customerId);
            overlaps = statement.executeQuery();
            return overlaps.isBeforeFirst();
            /*
            if (overlaps.isBeforeFirst()) {
                return true;
            }
            else {
                return false;
            }
             */
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean getOverlaps(int appointmentId, LocalDateTime start, LocalDateTime end, int userId, int customerId) {
        String query = "SELECT * FROM appointment " +
                "WHERE (start BETWEEN ? AND ? " +
                "OR end BETWEEN ? AND ?) AND (userId=? OR customerId=?) AND (appointmentId!=?)";
        ResultSet overlaps;
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, start.format(FORMAT));
            statement.setString(2, end.format(FORMAT));
            statement.setString(3, start.format(FORMAT));
            statement.setString(4, end.format(FORMAT));
            statement.setInt(5, userId);
            statement.setInt(6, customerId);
            statement.setInt(7, appointmentId);
            //System.out.println(statement);
            overlaps = statement.executeQuery();
            //System.out.println(overlaps.isBeforeFirst());
            return overlaps.isBeforeFirst();
            /*
            if (overlaps.isBeforeFirst()) {
                return true;
            }
            else {
                return false;
            }
             */
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isInsideBusinessHours(LocalDateTime start, LocalDateTime end) {
        if (
                // Make sure end comes after start
                end.isAfter(start) &&
                // Make sure start is not before start time
                !start.toLocalTime().isBefore(startTime) &&
                // Make sure start is not after end time
                !start.toLocalTime().isAfter(endTime) &&
                // Make sure end is not before start time
                !end.toLocalTime().isBefore(startTime) &&
                // Make sure end is not after end time
                !end.toLocalTime().isAfter(endTime)
        ) {
            return true;
        }
        else {
            return false;
        }
    }

    public static String edit(
            int appointmentId,
            int customerId,
            int userId,
            String type,
            LocalDateTime localStart,
            LocalDateTime localEnd
    ) {

        LocalDateTime start = convertToUtc(localStart);
        LocalDateTime end = convertToUtc(localEnd);

        boolean overlaps = getOverlaps(appointmentId, start, end, userId, customerId);
        boolean insideBusinessHours = isInsideBusinessHours(start, end);

        if (overlaps) {
            return "overlaps";
        }
        else if (!insideBusinessHours) {
            return "outside business hours";
        }
        else {
            String insert = "UPDATE appointment " +
                    "SET " +
                    "customerId=?," +
                    "userId=?," +
                    "type=?," +
                    "start=?," +
                    "end=?," +
                    "lastUpdate=NOW()," +
                    "lastUpdateBy=?" +
                    " WHERE appointmentId=?;";
            //System.out.println(insert);
            try {
                PreparedStatement statement = conn.prepareStatement(insert);
                statement.setInt(1, customerId);
                statement.setInt(2, userId);
                statement.setString(3, type);
                statement.setString(4, start.format(FORMAT));
                statement.setString(5, end.format(FORMAT));
                statement.setString(6, username);
                statement.setInt(7, appointmentId);
                System.out.println(statement);
                statement.execute();
                return "SUCCESS";
            } catch (Exception e) {
                e.printStackTrace();
                return "ERROR";
            }
        }
    }

    public static boolean delete(int appointmentId) {
        boolean result = false;

        String delete = "DELETE FROM appointment WHERE appointmentId=?;";
        try {
            PreparedStatement statement = conn.prepareStatement(delete);
            statement.setInt(1, appointmentId);
            result = statement.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        // For debugging
        try {
            DatabaseConnection.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalDateTime start = of(2020, 5, 17, 21, 00, 00);
        LocalDateTime end = of(2020, 5, 17, 22, 00, 00);
        add(2, 1, "Scrum", start, end);
        edit(3,1,1,"Fanciful Foray", start, end);
        try {
            DatabaseConnection.closeDatabaseConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
