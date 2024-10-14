package mainBank.subWindows;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mainBank.DataBaseManager;
import mainBank.LanguageManager;
import mainBank.ThemeManager;

import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AccountSubWindow {
    private Stage stage;
    private DataBaseManager dataBaseManager;
    private int userID;
    private Label usernameLabel;
    private Label passwordLabel;
    private Connection connection;
    private ResourceBundle resources;

    public AccountSubWindow(DataBaseManager dataBaseManager, int userID) {
        this.stage = new Stage();
        this.userID = userID;
        this.dataBaseManager = dataBaseManager;
        this.connection = dataBaseManager.getConnection();
        this.resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());
        createWindow();
    }

    private void createWindow() {

        String lightTheme = ThemeManager.getLIGHTHEME();
        String darkTheme = ThemeManager.getDARKTHEME();

        stage.setTitle(resources.getString("account.title"));

        usernameLabel = new Label(resources.getString("account.username"));
        passwordLabel = new Label(resources.getString("account.password"));

        Button changeUsername = new Button(resources.getString("account.changeUsername"));
        Button changePassword = new Button(resources.getString("account.changePassword"));
        changeUsername.setMaxWidth(150);
        changePassword.setMaxWidth(150);

        TextField textFieldUsername = new TextField(getUserName(userID));
        TextField textFieldPassword = new TextField(getPassword(userID));
        textFieldUsername.setMaxWidth(100);
        textFieldPassword.setMaxWidth(100);
        textFieldUsername.setEditable(false);
        textFieldPassword.setEditable(false);
        textFieldUsername.setFocusTraversable(false);
        textFieldPassword.setFocusTraversable(false);

        textFieldUsername.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        textFieldPassword.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        textFieldUsername.getStyleClass().add("custom-text-field");
        textFieldPassword.getStyleClass().add("custom-text-field");
        usernameLabel.getStyleClass().add("custom-label");
        passwordLabel.getStyleClass().add("custom-label");

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(30));
        vBox.setAlignment(Pos.BASELINE_CENTER);

        vBox.getChildren().addAll(usernameLabel, textFieldUsername, passwordLabel, textFieldPassword, changeUsername, changePassword);

        VBox.setMargin(usernameLabel, new Insets(0, 0, 10, 0));
        VBox.setMargin(textFieldUsername, new Insets(0, 0, 10, 0));

        VBox.setMargin(passwordLabel, new Insets(0, 0, 25, 0));
        VBox.setMargin(textFieldPassword, new Insets(0, 0, 10, 0));
        VBox.setMargin(changeUsername, new Insets(35, 0, 10, 0));
        VBox.setMargin(changePassword, new Insets(0, 0, 10, 0));

        Scene scene = new Scene(vBox, 275, 350);

        if (ThemeManager.getCurrentTheme().equals("light")) {
            scene.getStylesheets().add(lightTheme);
        } else {
            scene.getStylesheets().add(darkTheme);
        }

        ThemeManager.currentThemeProperty().addListener((observable, oldValue, newValue) -> {
            scene.getStylesheets().clear();
            if (newValue.equals("light")) {
                scene.getStylesheets().add(lightTheme);
            } else {
                scene.getStylesheets().add(darkTheme);
            }
        });

        stage.setScene(scene);
        stage.setResizable(false);
    }

    private String getUserName(int userID) {
        String sqlUsername = "SELECT username FROM users WHERE id = ?";
        String username = "";
        try (PreparedStatement statement = connection.prepareStatement(sqlUsername)) {
            statement.setInt(1, userID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    username = resultSet.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

    private String getPassword(int userID) {
        String sqlPassword = "SELECT password FROM users WHERE id = ?";
        String password = "";
        try (PreparedStatement statement = connection.prepareStatement(sqlPassword)) {
            statement.setInt(1, userID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    password = resultSet.getString("password");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "*".repeat(password.length());
    }

    public Stage getStage() {
        return stage;
    }
}
