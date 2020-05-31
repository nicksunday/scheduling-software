package GUI;

import Database.DatabaseConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        //Locale.setDefault(new Locale("es", "ES"));
        try {
            DatabaseConnection.connectToDatabase();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ResourceBundle resourceBundle = ResourceBundle.getBundle("language_files/lo");

        try {
            loginScene(resourceBundle);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Scene scene = scheduleScene(resourceBundle);
            primaryStage.setScene(scene);
            primaryStage.setTitle(resourceBundle.getString("schedule.title"));
            primaryStage.show();

            //
            // Lambda
            // Used to close database connection and cleanly exit upon primary stage exit
            primaryStage.setOnCloseRequest( event -> {
                try {
                    DatabaseConnection.closeDatabaseConnection();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                Platform.exit();
                System.exit(0);
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loginScene(ResourceBundle resourceBundle) throws IOException {
        Stage stage = new Stage();
        stage.setTitle(resourceBundle.getString("login.title"));
        stage.initStyle(StageStyle.UTILITY);
        FXMLLoader loader = new FXMLLoader();
        String fxmlFile = "src/GUI/Login/Login.fxml";
        FileInputStream fxmlStream = new FileInputStream(fxmlFile);
        loader.setResources(resourceBundle);

        GridPane root = loader.load(fxmlStream);
        //root.setGridLinesVisible(true);
        root.maxHeight(Double.MAX_VALUE);
        root.maxWidth(Double.MAX_VALUE);

        stage.setScene(new Scene(root));
        //
        // Lambda
        // Used to close database connection in the event the login screen is closed before logging in
        // Also exits the program so that the schedule page is never opened
        stage.setOnCloseRequest(event -> {
                try {
                    DatabaseConnection.closeDatabaseConnection();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                Platform.exit();
                System.exit(0);
        });
        stage.showAndWait();
    }

    public Scene scheduleScene(ResourceBundle resourceBundle) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        String fxmlFile = "src/GUI/Schedule/Schedule.fxml";
        FileInputStream fxmlStream = new FileInputStream(fxmlFile);
        loader.setResources(resourceBundle);

        BorderPane root = loader.load(fxmlStream);
        root.maxHeight(Double.MAX_VALUE);
        root.maxWidth(Double.MAX_VALUE);
        return new Scene(root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
