package GUI.ModifyUsers;

import Database.Actions.User;
import GUI.Schedule.UserObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifyUsers implements Initializable {

    public TableView<UserObject> userTable;

    public TextField idField;
    public TextField userNameField;
    public ComboBox activeCombo;
    public PasswordField passwordField1;
    public PasswordField passwordField2;

    public TextField editIdField;
    public TextField editUserNameField;
    public ComboBox editActiveCombo;
    public PasswordField editPasswordField1;
    public PasswordField editPasswordField2;

    ObservableList<Boolean> activeList = FXCollections.observableArrayList(true,false);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activeCombo.setItems(activeList);
        editActiveCombo.setItems(activeList);
        updateUserList();

    }

    private void setError(Node node) {
        node.setStyle("-fx-border-color: red");
    }

    private void removeError(Node node) {
        node.setStyle(null);
    }

    public void addUser(ActionEvent actionEvent) {
        Alert emptyFieldAlert = new Alert(Alert.AlertType.WARNING);
        emptyFieldAlert.setTitle("Add User Warning");
        emptyFieldAlert.setHeaderText("Warning");
        emptyFieldAlert.setContentText("Please fill in all highlighted fields to add a new user");

        Alert passwordMatchAlert = new Alert(Alert.AlertType.WARNING);
        passwordMatchAlert.setTitle("Password Mismatch");
        passwordMatchAlert.setHeaderText("PASSWORD MISMATCH");
        passwordMatchAlert.setContentText("THe passwords entered do not match, try again");

        int errorCount = 0;

        if (userNameField.getText().isEmpty()) {
            setError(userNameField);
            errorCount++;
        }
        if (activeCombo.getSelectionModel().isEmpty()) {
            setError(activeCombo);
            errorCount++;
        }
        if (!passwordField1.getText().matches(passwordField2.getText())) {
            setError(passwordField1);
            setError(passwordField2);
            errorCount++;
            passwordMatchAlert.showAndWait();
        }
        if (passwordField1.getText().isEmpty()) {
            setError(passwordField1);
            setError(passwordField2);
            errorCount++;
        }

        if (errorCount > 0) {
            emptyFieldAlert.showAndWait();
        }
        else {
            try {
                boolean userAdded = Database.Actions.User.add(
                        userNameField.getText(),
                        passwordField1.getText(),
                        (Boolean) activeCombo.getSelectionModel().getSelectedItem()
                );
                if (userAdded) {
                    removeError(userNameField);
                    removeError(activeCombo);
                    removeError(passwordField1);
                    removeError(passwordField2);
                    updateUserList();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void populateEditFields(ActionEvent actionEvent) {
    }

    public void editUser(ActionEvent actionEvent) {
    }

    private void updateUserList() {
        userTable.getItems().clear();
        ResultSet users = User.getAllUsers();
        while (true) {
            try {
                if (!users.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                userTable.getItems().add(
                        new UserObject(
                                users.getInt("userId"),
                                users.getString("userName"),
                                users.getInt("active")
                        ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
