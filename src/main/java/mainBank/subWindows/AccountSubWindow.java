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
        changeUsername.setMaxWidth(166);
        changePassword.setMaxWidth(166);

        TextField textFieldUsername = new TextField(getUserName(userID));
        TextField textFieldPassword = new TextField(getPasswordforShow(userID));
        textFieldUsername.setMaxWidth(125);
        textFieldPassword.setMaxWidth(125);
        textFieldUsername.setEditable(false);
        textFieldPassword.setEditable(false);
        textFieldUsername.setFocusTraversable(false);
        textFieldPassword.setFocusTraversable(false);

        textFieldUsername.getStyleClass().add("custom-text-field");
        textFieldPassword.getStyleClass().add("custom-text-field");
        usernameLabel.getStyleClass().add("custom-label2");
        passwordLabel.getStyleClass().add("custom-label2");

        vBox.getChildren().addAll(usernameLabel, textFieldUsername, passwordLabel, textFieldPassword, changeUsername, changePassword);

        VBox.setMargin(usernameLabel, new Insets(0, 0, 10, 0));
        VBox.setMargin(textFieldUsername, new Insets(0, 0, 5, 0));

        VBox.setMargin(passwordLabel, new Insets(10, 0, 25, 0));
        VBox.setMargin(textFieldPassword, new Insets(0, 0, 5, 0));
        VBox.setMargin(changeUsername, new Insets(50, 0, 10, 0));

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

        newUsernameField.getStyleClass().addAll("custom-text-field");
        newUsernameField1.getStyleClass().addAll("custom-text-field");
        newUsernameLabel.getStyleClass().addAll("custom-label2");
        newUsernameLabel1.getStyleClass().addAll("custom-label2");

        newUsernameField.setMaxWidth(200);
        newUsernameField1.setMaxWidth(200);
        saveButton.setMaxWidth(150);
        cancelButton.setMaxWidth(150);

        VBox.setMargin(newUsernameLabel, new Insets(0, 0, 10, 0));
        VBox.setMargin(newUsernameField, new Insets(0, 0, 10, 0));
        VBox.setMargin(newUsernameLabel1, new Insets(10, 0, 10, 0));
        VBox.setMargin(newUsernameField1, new Insets(0, 0, 10, 0));
        VBox.setMargin(saveButton, new Insets(32));

        saveButton.setOnAction(event -> {
            String newUsername = newUsernameField.getText();
            String confirmUsername = newUsernameField1.getText();
            saveNewUsername(newUsername, confirmUsername);
            setMainView();
        });


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

        oldPasswordField.getStyleClass().addAll("custom-text-field");
        newPasswordField.getStyleClass().addAll("custom-text-field");
        confirmNewPasswordField.getStyleClass().addAll("custom-text-field");
        oldPasswordLabel.getStyleClass().addAll("custom-label2");
        newPasswordLabel.getStyleClass().addAll("custom-label2");
        confirmNewPasswordLabel.getStyleClass().addAll("custom-label2");

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
        VBox.setMargin(saveButton, new Insets(13));


        saveButton.setOnAction(event -> {
            String currentPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmNewPasswordField.getText();
            saveNewPassword(currentPassword, newPassword, confirmPassword);
            setMainView();
        });
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

    private String getPasswordforShow(int userID) {
        String password = getPassword(userID);
        return "*".repeat(password.length());
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
        return password;
    }

    public void saveNewUsername(String newUsername, String confirmUsername) {
        if (newUsername.isEmpty() || confirmUsername.isEmpty()) {
            System.out.println("Error: campo/s de texto vacío/s");
            showError(resources.getString("login.error"), resources.getString("account.userError1"));
            return;
        }
        if (!newUsername.equals(confirmUsername)) {
            System.out.println("Error: confirmación de usuario incorrecta");
            showError(resources.getString("login.error"),resources.getString("account.userError2"));
            return;
        }

        String currentUsername = getUserName(userID);
        if (newUsername.equals(currentUsername)) {
            System.out.println("Error: el nuevo nombre de usuario es igual al actual.");
            showError(resources.getString("login.error"), resources.getString("account.userError3"));
            return;
        }

        String sqlCheckUsername = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlCheckUsername)) {
            statement.setString(1, newUsername);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                System.out.println("Error: el nombre de usuario ya existe.");
                showError(resources.getString("login.error"), resources.getString("account.userError4"));
                return;
            }
        } catch (SQLException e) {
            System.out.println("Error al comprobar el nombre de usuario: " + e.getMessage());
            return;
        }

        String sqlUpdateUsername = "UPDATE users SET username = ? WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(sqlUpdateUsername)) {
            updateStatement.setString(1, newUsername);
            updateStatement.setInt(2, userID);
            updateStatement.executeUpdate();
            System.out.println("Nombre de usuario actualizado con éxito.");
            showMessage(resources.getString("login.success"), resources.getString("account.userUpdated"));
        } catch (SQLException e) {
            System.out.println("Error al actualizar el nombre de usuario: " + e.getMessage());
        }
    }


    public void saveNewPassword(String currentPassword, String newPassword, String confirmPassword) {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("Error: campo/s de texto vacío/s");
            showError(resources.getString("login.error"), resources.getString("account.userError1"));
            return;
        }
        String actualPassword = getPassword(userID); //si no funciona debería revisar como se gestionan los userID en los métodos getPassword y getPasswordforShow
        if (!currentPassword.equals(actualPassword)) {
            System.out.println("Error: la contraseña actual es incorrecta.");
            showError(resources.getString("login.error"), resources.getString("account.passwordError1"));
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Error: la confirmación de la nueva contraseña no coincide.");
            showError(resources.getString("login.error"), resources.getString("account.passwordError2"));
            return;
        }
        String sqlUpdatePassword = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(sqlUpdatePassword)) {
            updateStatement.setString(1, newPassword);
            updateStatement.setInt(2, userID);
            updateStatement.executeUpdate();
            System.out.println("Contraseña actualizada con éxito.");
            showMessage(resources.getString("login.success"), resources.getString("account.passwordUpdated"));
        } catch (SQLException e) {
            System.out.println("Error al actualizar la contraseña: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {//en un futuro debería crear una clase que gestione los mensajes pop up
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Stage getStage() {
        return stage;
    }
}
