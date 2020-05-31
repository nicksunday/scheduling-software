package Database.Actions;

import Database.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Database.Actions.City;
import static GUI.Login.Login.username;
import static Database.DatabaseConnection.conn;

public class Address {

    public static int getAddressId(String address, String address2, String postalCode) {
        if (address2 == null) {
            address2 = "";
        }

        String query = "SELECT addressId FROM address WHERE address=? AND address2=? AND postalCode=?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, address);
            statement.setString(2, address2);
            statement.setString(3, postalCode);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
            else {
                return 0;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getAddressId(String address, String postalCode) {
        return getAddressId(address, null, postalCode);
    }

    public static int addAddress(
            String address,
            String address2,
            String cityName,
            String postalCode,
            String phone,
            String countryName
            ) {
        int addressId = 0;

        if (address2 == null) {
            address2 = "";
        }

        int cityId = City.getCityId(cityName);
        if (cityId == 0) {
            cityId = City.addCity(cityName, countryName);
        }

        String user;
        if (username == null) {
            user = "test";
        }
        else {
            user = username;
        }



        String insert = "INSERT INTO address (" +
                "address, address2, cityId, postalCode," +
                "phone, createDate, createdBy, lastUpdateBy) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), ?, ?)";
        try {
            PreparedStatement statement = conn.prepareStatement(insert);
            statement.setString(1, address);
            statement.setString(2, address2);
            statement.setInt(3, cityId);
            statement.setString(4, postalCode);
            statement.setString(5, phone);
            statement.setString(6, user);
            statement.setString(7, user);
            statement.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return getAddressId(address, address2, postalCode);
    }

    public static int addAddress(
            String address,
            String cityName,
            String postalCode,
            String phone,
            String countryName
    ) {
        return addAddress(address, null, cityName, postalCode, phone, countryName);
    }

    public static void main(String[] args) {
        // For debugging
        try {
            DatabaseConnection.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int addressId = getAddressId("123 Main", "11111");
        /*int addressId = addAddress(
                "123 Fake", "San Antonio", "12345", "555-1215", "US"
        );*/
        System.out.println(addressId);

        try {
            DatabaseConnection.closeDatabaseConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
