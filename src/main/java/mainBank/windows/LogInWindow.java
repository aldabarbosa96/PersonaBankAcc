package mainBank.windows;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainBank.managers.DataBaseManager;
import mainBank.managers.LanguageManager;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class LogInWindow extends Application {
    private DataBaseManager dbmanager = new DataBaseManager();

    @Override
    public void start(Stage stage) {
        LanguageManager.loadLocalePreference();
        dbmanager.dbconnect();
        dbmanager.createTable();

        ResourceBundle resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());

        Label usernameLabel = new Label(resources.getString("login.username"));
        TextField usernameField = new TextField();

        Label passwordLabel = new Label(resources.getString("login.password"));
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button(resources.getString("login.loginButton"));
        Button registerButton = new Button(resources.getString("login.registerButton"));
        loginButton.setMinWidth(90);
        registerButton.setMinWidth(90);

        VBox vbox = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, registerButton);
        vbox.setPadding(new Insets(20));

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            int userId = dbmanager.verifyUser(username, password);
            if (userId != -1) {
                String successMessageTemplate = resources.getString("login.successMessage");
                String successMessage = MessageFormat.format(successMessageTemplate, username);
                showMessage(resources.getString("login.success"), successMessage);

                new MainBankWindow(dbmanager, userId).start(stage);
            } else {
                showError(resources.getString("login.error"), resources.getString("login.errorMessage"));
            }
        });

        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showError(resources.getString("login.error"), resources.getString("login.errorMessage"));
                return;
            }

            boolean success = dbmanager.insertUser(username, password);
            if (success) {
                showMessage(resources.getString("login.success"), resources.getString("login.successMessage"));
            } else {
                if (!dbmanager.userIsValid(username)) {
                    showError(resources.getString("login.error"), resources.getString("login.errorMessage"));
                } else {
                    showError(resources.getString("login.error"), resources.getString("login.errorMessage"));
                }
            }
        });

        Scene scene = new Scene(vbox, 300, 220);
        stage.setScene(scene);
        stage.setTitle(resources.getString("main.title"));
        stage.show();

        stage.setOnCloseRequest(event -> dbmanager.dbdisconnect());
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
