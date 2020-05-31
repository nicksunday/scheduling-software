package Database.Actions;

import Database.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static GUI.Login.Login.username;
import static Database.DatabaseConnection.conn;

public class Customer {

    public static ResultSet getAllCustomers() {
        ResultSet result = null;

        String query = "SELECT * FROM customer";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            result = statement.executeQuery();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ResultSet getAll() {

        ResultSet result = null;

        String query = "SELECT\n " +
        "        c.customerId,\n" +
        "        c.customerName,\n" +
        "        c.addressId,\n" +
        "        c.active,\n" +
        "        a.address,\n" +
        "        a.address2,\n" +
        "        ci.city,\n" +
        "        co.country,\n" +
        "        a.postalCode,\n" +
        "        a.phone\n" +
        "FROM\n" +
        "        customer c\n" +
        "INNER JOIN\n" +
        "        address a ON c.addressId = a.addressId\n" +
        "INNER JOIN\n" +
        "        city ci ON a.cityId = ci.cityId\n" +
        "INNER JOIN\n" +
        "        country co ON ci.countryId = co.countryId\n" +
        "ORDER BY\n" +
        "        c.customerId ASC";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            result = statement.executeQuery();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean add(
            String customerName,
            String address,
            String address2,
            String cityName,
            String postalCode,
            String phone,
            String countryName,
            boolean active
    ) {
        int addressId = Address.getAddressId(address, address2, postalCode);
        if (addressId == 0) {
            addressId = Address.addAddress(address, address2, cityName, postalCode, phone, countryName);
        }

        String user;
        if (username == null) {
            user = "test";
        }
        else {
            user = username;
        }

        String insert = "INSERT INTO customer (" +
                "customerName," +
                "addressId," +
                "active," +
                "createDate," +
                "createdBy," +
                "lastUpdateBy" +
                ") VALUES (?, ?, ?, NOW(), ?, ?)";

        try {
            PreparedStatement statement = conn.prepareStatement(insert);
            statement.setString(1, customerName);
            statement.setInt(2, addressId);
            if (active) {
                statement.setInt(3, 1);
            }
            else {
                statement.setInt(3, 0);
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

    public static boolean add(
            String customerName,
            String address,
            String cityName,
            String postalCode,
            String phone,
            String countryName,
            boolean active
    ) {
        return add(
                customerName, address, null, cityName, postalCode, phone, countryName, active
        );
    }

    public static boolean edit(
        int customerId,
        String customerName,
        String address,
        String address2,
        String cityName,
        String postalCode,
        String phone,
        String countryName,
        boolean active
    ) {

        String user;
        if (username == null) {
            user = "test";
        }
        else {
            user = username;
        }

        int addressId = Address.getAddressId(address, address2, postalCode);
        if (addressId == 0) {
            addressId = Address.addAddress(address, address2, cityName, postalCode, phone, countryName);
        }

        String update = "UPDATE customer SET customerName=?,addressId=?,active=?,lastUpdateBy=? WHERE customerId=?";

        try {
            PreparedStatement statement = conn.prepareStatement(update);
            statement.setString(1, customerName);
            statement.setInt(2, addressId);
            if (active) {
                statement.setInt(3, 1);
            }
            else {
                statement.setInt(3, 0);
            }
            statement.setString(4, user);
            statement.setInt(5, customerId);
            System.out.println(statement);
            statement.execute();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean edit(
        int customerId,
        String customerName,
        String address,
        String cityName,
        String postalCode,
        String phone,
        String countryName,
        boolean active
    ) {
        return edit(
                customerId, customerName, address, null, cityName, postalCode, phone, countryName, active
        );
    }

    public static boolean delete(int customerId) {
        String delete = "DELETE FROM customer WHERE customerId=?";
        try {
            PreparedStatement statement = conn.prepareStatement(delete);
            statement.setInt(1, customerId);
            statement.execute();
            return true;
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

        ResultSet result = getAll();
        try {
            while (result.next()) {
                String cName = result.getString(1);
                String address = result.getString(2);
                String city = result.getString(3);
                String country = result.getString(4);
                String postalCode = result.getString(5);
                String phone = result.getString(6);
                System.out.println(cName + " " + address + " " + city + " " + country + " " + postalCode + " " + phone);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        boolean deleted = delete(4);
        System.out.println(deleted);

        try {
            DatabaseConnection.closeDatabaseConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getCustomerId(String customerName) {
        String query = "SELECT customerId FROM customer WHERE customerName=?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, customerName);
            //System.out.println(statement);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                return result.getInt("customerId");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }
}
