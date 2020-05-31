package Database.Actions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Database.Actions.Country;
import Database.DatabaseConnection;

import static GUI.Login.Login.username;
import static Database.DatabaseConnection.conn;

public class City {

    public static int getCityId(String cityName) {
        int cityId = 0;
        String query = "SELECT cityId FROM city WHERE city=?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, cityName);
            ResultSet result = statement.executeQuery();
            result.next();
            cityId = result.getInt(1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return cityId;
    }

    public static int addCity(String cityName, String countryName) {

        int countryId = Country.getCountryId(countryName);
        if (countryId == 0) {
            countryId = Country.addCountry(countryName);
        }

        String user;
        if (username == null) {
            user = "test";
        }
        else {
            user = username;
        }

        String insert = "INSERT INTO city (city, countryId, createDate, createdBy, lastUpdateBy) " +
                "VALUES (?, ?, NOW(), ?, ?)";
        try {
            PreparedStatement statement = conn.prepareStatement(insert);
            statement.setString(1, cityName);
            statement.setInt(2, countryId);
            statement.setString(3, user);
            statement.setString(4, user);
            statement.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return getCityId(cityName);
    }

    public static void main(String[] args) {
        // For debugging
        try {
            DatabaseConnection.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int cityId = getCityId("Oslo");
        //int cityId = addCity("San Antonio", "US");
        System.out.println(cityId);

        try {
            DatabaseConnection.closeDatabaseConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
