package GUI.ModifyCustomers;

import Database.Actions.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ModifyCustomers implements Initializable {

    public TextField nameField;
    public TextField address1Field;
    public TextField address2Field;
    public TextField cityField;
    public TextField countryField;
    public TextField postalCodeField;
    public TextField phoneField;
    public ComboBox activeCombo;

    public TextField editIdField;
    public TextField editNameField;
    public TextField editAddress1Field;
    public TextField editAddress2Field;
    public TextField editCityField;
    public TextField editCountryField;
    public TextField editPostalCodeField;
    public TextField editPhoneField;
    public ComboBox editActiveCombo;
    public Button saveBtn;

    @FXML
    private TableView<ExpandedCustomerObject> customerTable = new TableView<>();
    private ObservableList<ExpandedCustomerObject> expandedCustomerList = FXCollections.observableArrayList();
    private ObservableList<Boolean> activeList = FXCollections.observableArrayList(true,false);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editIdField.setStyle("-fx-background-color: lightgrey");
        activeCombo.setItems(activeList);
        editActiveCombo.setItems(activeList);
        getCustomers();
    }

    private void setError(Node node) {
        node.setStyle("-fx-border-color: red");
    }

    private void removeError(Node node) {
        node.setStyle(null);
    }


    public void addCustomer(ActionEvent actionEvent) {
        Alert emptyFieldAlert = new Alert(Alert.AlertType.WARNING);
        emptyFieldAlert.setTitle("Add Customer Warning");
        emptyFieldAlert.setHeaderText("Warning");
        emptyFieldAlert.setContentText("Please fill in all highlighted fields to add a new customer");

        Alert errorAddingCustomer = new Alert(Alert.AlertType.ERROR);
        errorAddingCustomer.setTitle("Error Adding Customer");
        errorAddingCustomer.setHeaderText("ERROR");
        errorAddingCustomer.setContentText("There was an error adding the customer, check your input.");

        removeError(nameField);
        removeError(address1Field);
        removeError(address2Field);
        removeError(cityField);
        removeError(postalCodeField);
        removeError(phoneField);
        removeError(countryField);
        removeError(activeCombo);

        int errorCount = 0;

        if (nameField.getText().isEmpty()) {
            setError(nameField);
            errorCount++;
        }

        if (address1Field.getText().isEmpty()) {
            setError(address1Field);
            errorCount++;
        }

        if (cityField.getText().isEmpty()) {
            setError(cityField);
            errorCount++;
        }

        if (postalCodeField.getText().isEmpty()) {
            setError(postalCodeField);
            errorCount++;
        }
        String regex = "\\d{5}";
        if (!postalCodeField.getText().matches(regex)) {
            setError(postalCodeField);
            Alert postalMismatch = new Alert(Alert.AlertType.ERROR);
            postalMismatch.setContentText("Postal code must be numerical and 5 digits only");
            postalMismatch.showAndWait();
            errorCount++;
        }
        if (phoneField.getText().isEmpty()) {
            setError(phoneField);
            errorCount++;
        }

        if (countryField.getText().isEmpty()) {
            setError(countryField);
            errorCount++;
        }

        if (activeCombo.getSelectionModel().isEmpty()) {
            setError(activeCombo);
            errorCount++;
        }

        if (activeCombo.getSelectionModel().isEmpty()) {
            setError(activeCombo);
            errorCount++;
        }

        if (errorCount > 0) {
            emptyFieldAlert.showAndWait();
        }
        else {
            try {
                boolean customerAdded = Customer.add(
                        nameField.getText(),
                        address1Field.getText(),
                        address2Field.getText(),
                        cityField.getText(),
                        postalCodeField.getText(),
                        phoneField.getText(),
                        countryField.getText(),
                        (Boolean) activeCombo.getSelectionModel().getSelectedItem()
                );

                if (customerAdded) {
                    nameField.clear();
                    address1Field.clear();
                    address2Field.clear();
                    cityField.clear();
                    countryField.clear();
                    postalCodeField.clear();
                    phoneField.clear();
                    activeCombo.getSelectionModel().clearSelection();
                    getCustomers();
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorAddingCustomer.showAndWait();
            }
        }
    }

    public void populateEditFields(ActionEvent actionEvent) {
        ExpandedCustomerObject selection = customerTable.getSelectionModel().getSelectedItem();
        try {
            editIdField.setText(String.valueOf(selection.getCustomerId()));
            editNameField.setText(selection.getCustomerName());
            editAddress1Field.setText(selection.getAddress1());
            editAddress2Field.setText(selection.getAddress2());
            editCityField.setText(selection.getCity());
            editCountryField.setText(selection.getCountry());
            editPostalCodeField.setText(selection.getPostalCode());
            editPhoneField.setText(selection.getPhone());
            editActiveCombo.getSelectionModel().select(selection.isActive());

            editIdField.setVisible(true);
            editNameField.setVisible(true);
            editAddress1Field.setVisible(true);
            editAddress2Field.setVisible(true);
            editCityField.setVisible(true);
            editCountryField.setVisible(true);
            editPostalCodeField.setVisible(true);
            editPhoneField.setVisible(true);
            editActiveCombo.setVisible(true);
            saveBtn.setVisible(true);
        }
        catch (NullPointerException e) {
            System.out.println("No customer selected to edit");
        }
    }

    public void editCustomer(ActionEvent actionEvent) {
        Alert emptyFieldAlert = new Alert(Alert.AlertType.WARNING);
        emptyFieldAlert.setTitle("Edit Customer Warning");
        emptyFieldAlert.setHeaderText("Warning");
        emptyFieldAlert.setContentText("Please fill in all highlighted fields to edit a customer");

        Alert errorEditingCustomer = new Alert(Alert.AlertType.ERROR);
        errorEditingCustomer.setTitle("Error Editing Customer");
        errorEditingCustomer.setHeaderText("ERROR");
        errorEditingCustomer.setContentText("There was an error editing the customer, check your input.");

        removeError(editIdField);
        removeError(editNameField);
        removeError(editAddress1Field);
        removeError(editAddress2Field);
        removeError(editCityField);
        removeError(editCountryField);
        removeError(editPostalCodeField);
        removeError(editPhoneField);
        removeError(editActiveCombo);

        int errorCount = 0;

        if (editIdField.getText().isEmpty()) {
            setError(editIdField);
            errorCount++;
        }
        if (editNameField.getText().isEmpty()) {
            setError(editNameField);
            errorCount++;
        }
        if (editAddress1Field.getText().isEmpty()) {
            setError(editAddress1Field);
            errorCount++;
        }
        if (editCityField.getText().isEmpty()) {
            setError(editCityField);
            errorCount++;
        }
        if (editPostalCodeField.getText().isEmpty()) {
            setError(editPostalCodeField);
            errorCount++;
        }
        String regex = "\\d{5}";
        if (!editPostalCodeField.getText().matches(regex)) {
            setError(editPostalCodeField);
            Alert postalMismatch = new Alert(Alert.AlertType.ERROR);
            postalMismatch.setContentText("Postal code must be numerical and 5 digits only");
            postalMismatch.showAndWait();
            errorCount++;
        }
        if (editPhoneField.getText().isEmpty()) {
            setError(editPhoneField);
            errorCount++;
        }
        if (editCountryField.getText().isEmpty()) {
            setError(editCountryField);
            errorCount++;
        }
        if (editActiveCombo.getSelectionModel().isEmpty()) {
            setError(editActiveCombo);
            errorCount++;
        }

        if (errorCount > 0) {
            emptyFieldAlert.showAndWait();
        }
        else {
            try {

                boolean customerEdited = Customer.edit(
                        Integer.parseInt(editIdField.getText()),
                        editNameField.getText(),
                        editAddress1Field.getText(),
                        editAddress2Field.getText(),
                        editCityField.getText(),
                        editPostalCodeField.getText(),
                        editPhoneField.getText(),
                        editCountryField.getText(),
                        (Boolean) editActiveCombo.getSelectionModel().getSelectedItem()
                );

                if (customerEdited) {
                    editIdField.setVisible(false);
                    editNameField.setVisible(false);
                    editAddress1Field.setVisible(false);
                    editAddress2Field.setVisible(false);
                    editCityField.setVisible(false);
                    editCountryField.setVisible(false);
                    editPostalCodeField.setVisible(false);
                    editPhoneField.setVisible(false);
                    editActiveCombo.setVisible(false);
                    saveBtn.setVisible(false);
                    getCustomers();
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorEditingCustomer.showAndWait();
            }
        }
    }

    public void deleteCustomer(ActionEvent actionEvent) {
        ExpandedCustomerObject selection = customerTable.getSelectionModel().getSelectedItem();
        try {
            Customer.delete(selection.getCustomerId());
            getCustomers();
        }
        catch (NullPointerException e) {
            System.out.println("No customer selected to delete");
        }
    }

    public void getCustomers() {
        ResultSet result;
        customerTable.getItems().clear();
        try {
            result = Customer.getAll();
            while (result.next()) {
                customerTable.getItems().add(
                        new ExpandedCustomerObject(
                                result.getInt("customerId"),
                                result.getString("customerName"),
                                result.getInt("addressId"),
                                result.getInt("active"),
                                result.getString("address"),
                                result.getString("address2"),
                                result.getString("city"),
                                result.getString("country"),
                                result.getString("postalCode"),
                                result.getString("phone")
                        )
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
