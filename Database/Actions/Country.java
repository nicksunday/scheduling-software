package Database.Actions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Database.DatabaseConnection;

import static GUI.Login.Login.username;
import static Database.DatabaseConnection.conn;

public class Country {

    public static int getCountryId(String countryName) {
        int countryId = 0;
        String query = "SELECT countryId FROM country WHERE country=?";
        ResultSet result;

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, countryName);
            result = statement.executeQuery();
            result.next();
            countryId = result.getInt(1);

        }
        catch (Exception e) {
            System.out.println("Country '" + countryName + "' not found");
        }

        return countryId;
    }

    public static int addCountry(String countryName) {
        String user;
        if (username == null) {
            user = "test";
        }
        else {
            user = username;
        }
        String insert = "INSERT INTO country (country, createDate, createdBy, lastUpdateBy) " +
                "VALUES (?, NOW(), ?, ?)";
        try {
            PreparedStatement statement = conn.prepareStatement(insert);
            statement.setString(1, countryName);
            statement.setString(2, user);
            statement.setString(3, user);
            statement.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return getCountryId(countryName);
    }

    public static void main(String[] args) {
        // FOr debugging
        try {
            DatabaseConnection.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //int countryId = getCountryId("US");
        int countryId = getCountryId("Sweden");
        //int countryId = addCountry("Sweden");
        System.out.println(countryId);
        try {
            DatabaseConnection.closeDatabaseConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
