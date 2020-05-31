package GUI.Login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import Database.Actions.VerifyLogin;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static java.time.ZonedDateTime.now;

public class Login implements Initializable {
    public static boolean authenticated = false;
    public static String username;

    @FXML
    private Text welcomeTxt;
    @FXML
    private Label userNameLbl;
    @FXML
    private TextField userName;
    @FXML
    private Label passwordLbl;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginBtn;
    @FXML
    private Label loginError;

    public Login() {}

    @FXML
    private void authenticateLogin() {
        loginError.setVisible(false);
        username = userName.getText();
        String password = passwordField.getText();
        //System.out.println("Verifying Login");
        //System.out.println(username + " " + password);
        if (!username.isEmpty() & !password.isEmpty()) {
            authenticated = VerifyLogin.authenticate(username, password);
            if (authenticated) {
                System.out.println("User '" + username + "' authenticated successfully!");
                Stage stage = (Stage) userName.getScene().getWindow();
                stage.close();
                logAuthTimes(username);
            } else {
                System.out.println("User '" + username + "' not authenticated");
                loginError.setVisible(true);
            }
        }
    }

    private void logAuthTimes(String username) {
        Logger authLogger = Logger.getLogger("AuthLog");
        FileHandler logFileHandler;
        try {
            logFileHandler = new FileHandler("src/scheduling-software-auth.log", true);
            authLogger.addHandler(logFileHandler);
            SimpleFormatter authLogFormatter = new SimpleFormatter();
            logFileHandler.setFormatter(authLogFormatter);
            Instant now = Instant.now();
            authLogger.setUseParentHandlers(false);
            authLogger.log(Level.INFO, now + ": User '" + username + "' logged in successfully.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEnter(ActionEvent ae) {
        authenticateLogin();
    }

    @Override
    public void initialize(URL location, ResourceBundle resourcesBundle) {
        welcomeTxt.setText(resourcesBundle.getString("login.welcomeTxt"));
        userNameLbl.setText(resourcesBundle.getString("login.userNameLbl"));
        passwordLbl.setText(resourcesBundle.getString("loin.passwordLbl"));
        loginBtn.setText(resourcesBundle.getString("login.loginBtn"));
        loginError.setText(resourcesBundle.getString("login.loginError"));
    }
}

