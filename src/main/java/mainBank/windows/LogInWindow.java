package mainBank.windows;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
        usernameLabel.getStyleClass().add("login-label");
        TextField usernameField = new TextField();
        usernameField.getStyleClass().add("login-text-field");

        Label passwordLabel = new Label(resources.getString("login.password"));
        passwordLabel.getStyleClass().add("login-label");
        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("login-text-field");

        Button loginButton = new Button(resources.getString("login.loginButton"));
        Button registerButton = new Button(resources.getString("login.registerButton"));
        loginButton.getStyleClass().add("login-button");
        registerButton.getStyleClass().add("login-button");
        loginButton.setPrefWidth(146);
        registerButton.setPrefWidth(146);

        VBox usernameBox = new VBox(4, usernameLabel, usernameField);
        usernameBox.setAlignment(Pos.TOP_LEFT);
        usernameBox.setPadding(new Insets(0,20,0,20));

        VBox passwordBox = new VBox(4, passwordLabel, passwordField);
        passwordBox.setAlignment(Pos.TOP_LEFT);
        passwordBox.setPadding(new Insets(0,20,0,20));

        VBox buttonBox = new VBox(8, loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(25, 0, 0, 0));

        VBox vbox = new VBox(25, usernameBox, passwordBox, buttonBox);
        vbox.setPadding(new Insets(40));
        vbox.getStyleClass().add("login-vbox");
        vbox.setAlignment(Pos.CENTER);


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
                String successMessageTemplate = resources.getString("login.successRegister");
                String successMessage = MessageFormat.format(successMessageTemplate, username);
                showMessage(resources.getString("login.success"), successMessage);
            } else {
                if (!dbmanager.userIsValid(username)) {
                    showError(resources.getString("login.error"), resources.getString("login.errorMessage"));
                } else {
                    showError(resources.getString("login.error"), resources.getString("login.errorMessage"));
                }
            }
        });

        Scene scene = new Scene(vbox, 300, 375);
        scene.getStylesheets().add(getClass().getResource("/cssThemes/light-theme.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/cssThemes/login-theme.css").toExternalForm());

        stage.setScene(scene);
        stage.setResizable(false);
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
