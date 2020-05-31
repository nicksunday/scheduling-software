package GUI.Schedule;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import Database.Actions.*;
import javafx.stage.Stage;

import static Database.Actions.Appointment.FORMAT;
import static Database.Actions.Appointment.startTime;
import static Database.Actions.Appointment.endTime;
import static GUI.Schedule.AppointmentObject.DISPLAYFORMAT;
import static GUI.Login.Login.username;

public class Schedule implements Initializable {


    public TextField typeField;
    public TextField startField;
    public TextField endField;
    public ComboBox userComboBox;
    public ComboBox customerComboBox;
    public RadioButton monthRadioBtn;
    public RadioButton weekRadioBtn;
    public TableColumn startColumn;
    public Text businessHours;
    public Text timeZoneTxt;
    public TextField editTypeField;
    public TextField editStartField;
    public TextField editEndField;
    public ComboBox editUserComboBox;
    public ComboBox editCustomerComboBox;
    public Button saveBtn;
    public ComboBox userReportCombo;
    private ZoneId timeZone = ZoneId.systemDefault();

    public ObservableList<CustomerObject> customerList= FXCollections.observableArrayList();
    public ObservableList<UserObject> userList = FXCollections.observableArrayList();

    private AppointmentObject currentSelection;

    public Schedule() {}

    public Label placeholderLabel;

    @FXML
    private Text welcomeTxt;

    @FXML
    private ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private TableView<AppointmentObject> tableView = new TableView<>();

    private void setError(Node node) {
        node.setStyle("-fx-border-color: red");
    }

    private void removeError(Node node) {
        node.setStyle(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        welcomeTxt.setText(resources.getString("schedule.welcomeTxt") + ", " + username + "!");
        timeZoneTxt.setText(resources.getString("schedule.timeZoneTxt") + " [" + timeZone + "]");
        ZonedDateTime startZDT = ZonedDateTime.of(LocalDate.now().atTime(startTime), ZoneOffset.UTC);
        ZonedDateTime endZDT = ZonedDateTime.of(LocalDate.now().atTime(endTime), ZoneOffset.UTC);
        businessHours.setText(resources.getString("schedule.businessHours") + " "
                + startZDT.withZoneSameInstant(timeZone).toLocalTime() + "-"
                + endZDT.withZoneSameInstant(timeZone).toLocalTime());

        placeholderLabel.setText(resources.getString("schedule.placeholderLabel"));
        try {
            getSchedule("month");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        /*
        Check for any appointments starting in the next 15 minutes
         */
        Alert fifteenMinuteAlert = new Alert(Alert.AlertType.WARNING);
        fifteenMinuteAlert.setTitle("Fifteen Minute Warning");
        fifteenMinuteAlert.setHeaderText("Fifteen Minute Warning");
        fifteenMinuteAlert.setContentText(
                "Check your schedule, you have one or more appointments within 15 minutes of now");
        boolean appointmentsSoon = GetSchedule.nearLoginTime();
        if (appointmentsSoon) {
            fifteenMinuteAlert.showAndWait();
        }

        updateCustomerList();

        updateUserList();
    }

    private void updateUserList() {
        ResultSet users = User.getActiveUsers();
        userList.clear();
        while (true) {
            try {
                if (!users.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                userList.add(
                    new UserObject(
                        users.getInt("userId"),
                        users.getString("userName"),
                        users.getInt("active")
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userComboBox.setItems(userList);
        userReportCombo.setItems(userList);
        editUserComboBox.setItems(userList);
    }

    private void updateCustomerList() {
        ResultSet customers = Customer.getAllCustomers();
        customerList.clear();
        while (true) {
            try {
                if (!customers.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                customerList.add(
                    new CustomerObject(
                        customers.getInt("customerId"),
                        customers.getString("customerName"),
                        customers.getInt("addressId"),
                        customers.getInt("active")
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        customerComboBox.setItems(customerList);
        editCustomerComboBox.setItems(customerList);
    }

    public void getSchedule(String userName, String scheduleType) throws SQLException {
        ResultSet result;
        tableView.getItems().clear();
        if (scheduleType.equals("week")) {
            result = GetSchedule.thisWeek(userName);
        }
        else {
            result = GetSchedule.thisMonth(userName);
        }

        try {
            while (result.next()) {
                LocalDateTime start = LocalDateTime.parse(result.getString("start"), FORMAT);
                LocalDateTime end = LocalDateTime.parse(result.getString("end"), FORMAT);
                tableView.getItems().add(
                    new AppointmentObject(
                        result.getInt("appointmentId"),
                        result.getString("type"),
                        start,
                        end,
                        result.getString("userName"),
                        result.getString("customerName")
                    )
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSchedule(String scheduleType) throws SQLException {
        getSchedule(username, scheduleType);
    }

    public void addAppointment(ActionEvent actionEvent) {
        Alert emptyFieldAlert = new Alert(Alert.AlertType.WARNING);
        emptyFieldAlert.setTitle("Add Appointment Warning");
        emptyFieldAlert.setHeaderText("Warning");
        emptyFieldAlert.setContentText("Please fill in all highlighted fields to add a new appointment");

        Alert errorAddingAppointment = new Alert(Alert.AlertType.ERROR);
        errorAddingAppointment.setTitle("Error Adding Appointment");
        errorAddingAppointment.setHeaderText("ERROR");
        errorAddingAppointment.setContentText("There was an error adding the appointment, check your input.");

        Alert appointmentOverlap = new Alert(Alert.AlertType.WARNING);
        appointmentOverlap.setTitle("Add Appointment Warning");
        appointmentOverlap.setHeaderText("Warning");
        appointmentOverlap.setContentText("New appointment overlaps with an existing appointment");

        Alert outsideBusinessHours = new Alert(Alert.AlertType.WARNING);
        outsideBusinessHours.setTitle("Add Appointment Warning");
        outsideBusinessHours.setHeaderText("Warning");
        outsideBusinessHours.setContentText("New appointment is outside of business hours");

        int errorCount = 0;
        removeError(typeField);
        removeError(startField);
        removeError(endField);
        removeError(userComboBox);
        removeError(customerComboBox);

        String type = null;
        LocalDateTime start = null;
        LocalDateTime end = null;
        UserObject selectedUser = (UserObject) userComboBox.getSelectionModel().getSelectedItem();
        CustomerObject selectedCustomer = (CustomerObject) customerComboBox.getSelectionModel().getSelectedItem();

        if (typeField.getText().isEmpty()) {
            setError(typeField);
            errorCount++;
        }
        else {
            type = typeField.getText();
        }
        try {
            start = LocalDateTime.parse(startField.getText(), DISPLAYFORMAT);
        }
        catch (DateTimeParseException e) {
            setError(startField);
            errorCount++;
        }
        try {
            end = LocalDateTime.parse(endField.getText(), DISPLAYFORMAT);
        } catch (DateTimeParseException e) {
            setError(endField);
            errorCount++;
        }
        if (selectedUser == null) {
            setError(userComboBox);
            errorCount++;
        }
        if (selectedCustomer == null) {
            setError(customerComboBox);
            errorCount++;
        }

        if (errorCount > 0) {
            emptyFieldAlert.showAndWait();
        }
        else {

            try {
                String appointmentAdded = Appointment.add(
                        selectedCustomer.getCustomerId(),
                        selectedUser.getUserId(),
                        type,
                        start,
                        end
                );
                //System.out.println(appointmentAdded);
                if (appointmentAdded.equals("SUCCESS")) {
                    typeField.clear();
                    startField.clear();
                    endField.clear();
                    userComboBox.getSelectionModel().clearSelection();
                    customerComboBox.getSelectionModel().clearSelection();
                } else if (appointmentAdded.equals("overlaps")) {
                    setError(startField);
                    setError(endField);
                    appointmentOverlap.showAndWait();
                } else if (appointmentAdded.equals("outside business hours")) {
                    setError(startField);
                    setError(endField);
                    outsideBusinessHours.showAndWait();
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorAddingAppointment.showAndWait();
            }
        }
        updateSchedule();
    }

    public void populateEditAppointment(ActionEvent actionEvent) {
        currentSelection = tableView.getSelectionModel().getSelectedItem();

        removeError(editTypeField);
        removeError(editStartField);
        removeError(editEndField);
        removeError(editUserComboBox);
        removeError(editCustomerComboBox);

        try {
            editTypeField.setText(currentSelection.getType());
            editStartField.setText(currentSelection.getStart());
            editEndField.setText(currentSelection.getEnd());
            editUserComboBox.getSelectionModel().select(currentSelection.getUserName());
            editCustomerComboBox.getSelectionModel().select(currentSelection.getCustomer());

            editTypeField.setVisible(true);
            editStartField.setVisible(true);
            editEndField.setVisible(true);
            editUserComboBox.setVisible(true);
            editCustomerComboBox.setVisible(true);
            saveBtn.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editAppointment(ActionEvent actionEvent) {
        Alert emptyFieldAlert = new Alert(Alert.AlertType.WARNING);
        emptyFieldAlert.setTitle("Edit Appointment Warning");
        emptyFieldAlert.setHeaderText("Warning");
        emptyFieldAlert.setContentText("Please fill in all highlighted fields to edit appointment");

        Alert errorAddingAppointment = new Alert(Alert.AlertType.ERROR);
        errorAddingAppointment.setTitle("Error Editing Appointment");
        errorAddingAppointment.setHeaderText("ERROR");
        errorAddingAppointment.setContentText("There was an error editing the appointment, check your input.");

        Alert appointmentOverlap = new Alert(Alert.AlertType.WARNING);
        appointmentOverlap.setTitle("Edit Appointment Warning");
        appointmentOverlap.setHeaderText("Warning");
        appointmentOverlap.setContentText("Appointment overlaps with an existing appointment");

        Alert outsideBusinessHours = new Alert(Alert.AlertType.WARNING);
        outsideBusinessHours.setTitle("Edit Appointment Warning");
        outsideBusinessHours.setHeaderText("Warning");
        outsideBusinessHours.setContentText("Appointment is outside of business hours");

        int errorCount = 0;

        LocalDateTime start = null;
        LocalDateTime end = null;
        //UserObject selectedUser = (UserObject) editUserComboBox.getSelectionModel().getSelectedItem();
        //CustomerObject selectedCustomer = (CustomerObject) editCustomerComboBox.getSelectionModel().getSelectedItem();

        if (editTypeField.getText().isEmpty()) {
            setError(editTypeField);
            errorCount++;
        }
        try {
            start = LocalDateTime.parse(editStartField.getText(), DISPLAYFORMAT);
        } catch (DateTimeParseException e) {
            setError(editStartField);
            errorCount++;
        }
        try {
            end = LocalDateTime.parse(editEndField.getText(), DISPLAYFORMAT);
        } catch (DateTimeParseException e) {
            setError(editEndField);
            errorCount++;
        }

        if (errorCount > 0) {
            emptyFieldAlert.showAndWait();
        }
        else {
            try {
                String appointmentEdited = Appointment.edit(
                        currentSelection.getAppointmentId(),
                        Database.Actions.Customer.getCustomerId(
                                String.valueOf(editCustomerComboBox.getSelectionModel().getSelectedItem())),
                        Database.Actions.User.getUserId(
                                String.valueOf(editUserComboBox.getSelectionModel().getSelectedItem())),
                        editTypeField.getText(),
                        start,
                        end
                );
                if (appointmentEdited.equals("SUCCESS")) {
                    editTypeField.setVisible(false);
                    editStartField.setVisible(false);
                    editEndField.setVisible(false);
                    editUserComboBox.setVisible(false);
                    editCustomerComboBox.setVisible(false);
                    saveBtn.setVisible(false);
                    updateSchedule();
                }
                else if (appointmentEdited.equals("overlaps")) {
                    setError(editStartField);
                    setError(editEndField);
                    appointmentOverlap.showAndWait();
                } else if (appointmentEdited.equals("outside business hours")) {
                    setError(editStartField);
                    setError(editEndField);
                    appointmentOverlap.showAndWait();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                errorAddingAppointment.showAndWait();
            }
        }
        updateSchedule();
    }

    public void deleteAppointment(ActionEvent actionEvent) {
        AppointmentObject selection = tableView.getSelectionModel().getSelectedItem();
        try {
            System.out.println(selection.getAppointmentId());
            Appointment.delete(selection.getAppointmentId());
            updateSchedule();
        }
        catch (NullPointerException e) {
            System.out.println("No appointment selected to delete");
        }
    }

    public void getMonthlySchedule(ActionEvent actionEvent) {
        try {
            getSchedule("month");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void getWeeklySchedule(ActionEvent actionEvent) {
        try {
            getSchedule("week");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateSchedule() {
        if (monthRadioBtn.isSelected()) {
            try {
                getSchedule("month");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            try {
                getSchedule("week");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void modifyUsers(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            String fxmlFile = "src/GUI/ModifyUsers/ModifyUsers.fxml";
            FileInputStream fxmlStream = new FileInputStream(fxmlFile);

            BorderPane root = loader.load(fxmlStream);
            root.maxHeight(Double.MAX_VALUE);
            root.maxWidth(Double.MAX_VALUE);
            Scene scene = new Scene (root);
            Stage stage = new Stage();
            stage.setTitle("Modify Users");
            stage.setScene(scene);
            stage.show();
            updateUserList();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void modifyCustomers(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            String fxmlFile = "src/GUI/ModifyCustomers/ModifyCustomers.fxml";
            FileInputStream fxmlStream = new FileInputStream(fxmlFile);

            BorderPane root = loader.load(fxmlStream);
            root.maxHeight(Double.MAX_VALUE);
            root.maxWidth(Double.MAX_VALUE);
            Scene scene = new Scene (root);
            Stage stage = new Stage();
            stage.setTitle("Modify Customers");
            stage.setScene(scene);
            stage.show();
            updateCustomerList();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reporting(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            String fxmlFile = "src/GUI/Reporting/Reporting.fxml";
            FileInputStream fxmlStream = new FileInputStream(fxmlFile);

            BorderPane root = loader.load(fxmlStream);
            root.maxHeight(Double.MAX_VALUE);
            root.maxWidth(Double.MAX_VALUE);
            Scene scene = new Scene (root);
            Stage stage = new Stage();
            stage.setTitle("Reporting");
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runReport(ActionEvent actionEvent) {
        String userName = String.valueOf(userReportCombo.getSelectionModel().getSelectedItem());
        try {
            if (weekRadioBtn.isSelected()) {
                getSchedule(userName, "week");
            } else {
                getSchedule(userName, "month");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
