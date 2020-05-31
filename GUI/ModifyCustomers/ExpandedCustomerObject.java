package GUI.ModifyCustomers;

import GUI.Schedule.CustomerObject;

public class ExpandedCustomerObject extends CustomerObject {
    String address1;
    String address2;
    String city;
    String country;
    String postalCode;
    String phone;

    public ExpandedCustomerObject(int customerId, String customerName, int addressId, int active,
                                  String address1, String address2, String city, String country,
                                  String postalCode, String phone) {
        super(customerId, customerName, addressId, active);
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
        this.phone = phone;
    }

    public String getAddress1() {
        return this.address1;
    }

    public String getAddress2() {
        return this.address2;
    }

    public String getCity() {
        return this.city;
    }

    public String getCountry() {
        return this.country;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public String getPhone() {
        return this.phone;
    }
}
