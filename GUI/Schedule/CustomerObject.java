package GUI.Schedule;

public class CustomerObject {

    private int customerId;
    private String customerName;
    private int addressId;
    private boolean active;

    public CustomerObject(int customerId, String customerName, int addressId, int active) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.addressId = addressId;
        if (active == 1) {
            this.active = true;
        }
        else {
            this.active = false;
        }
    }

    @Override
    public String toString() {
        return this.getCustomerName();
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getAddressId() {
        return addressId;
    }

    public boolean isActive() {
        return active;
    }

}
