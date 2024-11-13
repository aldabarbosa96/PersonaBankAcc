package mainBank.subWindows;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mainBank.managers.DataBaseManager;
import mainBank.managers.LanguageManager;
import mainBank.managers.ThemeManager;

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
    private VBox vBox;
    private Button changeUsername, changePassword;

    public AccountSubWindow(DataBaseManager dataBaseManager, int userID) {
        this.stage = new Stage();
        this.userID = userID;
        this.dataBaseManager = dataBaseManager;
        this.connection = dataBaseManager.getConnection();
        this.resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());
        createWindow();
    }

    private void createWindow() {
        if (vBox == null) {
            vBox = new VBox();
            vBox.setPadding(new Insets(30));
            vBox.setAlignment(Pos.BASELINE_CENTER);

            Scene scene = new Scene(vBox, 275, 350);

            String lightTheme = ThemeManager.getLIGHTHEME();
            String darkTheme = ThemeManager.getDARKTHEME();

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

        setMainView();
    }

    private void setMainView() {
        vBox.getChildren().clear();

        stage.setTitle(resources.getString("account.title"));

        usernameLabel = new Label(resources.getString("account.username"));
        passwordLabel = new Label(resources.getString("account.password"));

        changeUsername = new Button(resources.getString("account.changeUsername"));
        changePassword = new Button(resources.getString("account.changePassword"));
        changeUsername.setMaxWidth(150);
        changePassword.setMaxWidth(150);

        TextField textFieldUsername = new TextField(getUserName(userID));
        TextField textFieldPassword = new TextField(getPassword(userID));
        textFieldUsername.setMaxWidth(125);
        textFieldPassword.setMaxWidth(125);
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

        vBox.getChildren().addAll(usernameLabel, textFieldUsername, passwordLabel, textFieldPassword, changeUsername, changePassword);

        VBox.setMargin(usernameLabel, new Insets(0, 0, 10, 0));
        VBox.setMargin(textFieldUsername, new Insets(0, 0, 5, 0));

        VBox.setMargin(passwordLabel, new Insets(10, 0, 25, 0));
        VBox.setMargin(textFieldPassword, new Insets(0, 0, 5, 0));
        VBox.setMargin(changeUsername, new Insets(35, 0, 10, 0));

        toggleView();
    }

    public void toggleView() {
        changeUsername.setOnAction(event -> showChangeUsernameView());
        changePassword.setOnAction(event -> showChangePasswordView());
    }


    private void showChangeUsernameView() {
        vBox.getChildren().clear();

        Label newUsernameLabel = new Label(resources.getString("account.newUsername"));
        TextField newUsernameField = new TextField();
        Label newUsernameLabel1 = new Label(resources.getString("account.newUsernameConfirmation"));
        TextField newUsernameField1 = new TextField();
        Button saveButton = new Button(resources.getString("account.save"));
        Button cancelButton = new Button(resources.getString("account.cancel"));

        newUsernameField.setMaxWidth(200);
        newUsernameField1.setMaxWidth(200);
        saveButton.setMaxWidth(150);
        cancelButton.setMaxWidth(150);

        VBox.setMargin(newUsernameLabel, new Insets(0, 0, 10, 0));
        VBox.setMargin(newUsernameField, new Insets(0, 0, 10, 0));
        VBox.setMargin(newUsernameLabel1, new Insets(10, 0, 10, 0));
        VBox.setMargin(newUsernameField1, new Insets(0, 0, 10, 0));
        VBox.setMargin(saveButton, new Insets(32));

        saveButton.setOnAction(event -> setMainView());
        cancelButton.setOnAction(event -> setMainView());

        vBox.getChildren().addAll(newUsernameLabel, newUsernameField, newUsernameLabel1, newUsernameField1, saveButton, cancelButton);
    }

    private void showChangePasswordView() {//falta modificar este método
        vBox.getChildren().clear();

        Label oldPasswordLabel = new Label(resources.getString("account.newPasswordOld"));
        PasswordField oldPasswordField = new PasswordField();
        Label newPasswordLabel = new Label(resources.getString("account.newPassword"));
        PasswordField newPasswordField = new PasswordField();
        Label confirmNewPasswordLabel = new Label(resources.getString("account.newPasswordRepeat"));
        PasswordField confirmNewPasswordField = new PasswordField();
        Button saveButton = new Button(resources.getString("account.save"));
        Button cancelButton = new Button(resources.getString("account.cancel"));

        oldPasswordField.setMaxWidth(200);
        newPasswordField.setMaxWidth(200);
        confirmNewPasswordField.setMaxWidth(200);
        saveButton.setMaxWidth(150);
        cancelButton.setMaxWidth(150);

        VBox.setMargin(oldPasswordField, new Insets(2));
        VBox.setMargin(newPasswordLabel, new Insets(8));
        VBox.setMargin(newPasswordField, new Insets(2));
        VBox.setMargin(confirmNewPasswordLabel, new Insets(8));
        VBox.setMargin(confirmNewPasswordField, new Insets(2));
        VBox.setMargin(saveButton, new Insets(15));


        saveButton.setOnAction(event -> setMainView());
        cancelButton.setOnAction(event -> setMainView());

        vBox.getChildren().addAll(oldPasswordLabel, oldPasswordField, newPasswordLabel, newPasswordField, confirmNewPasswordLabel, confirmNewPasswordField, saveButton, cancelButton);
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
