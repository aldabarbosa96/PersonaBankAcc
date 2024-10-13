package mainBank.subWindows;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mainBank.DataBaseManager;

import javafx.scene.control.*;
import mainBank.ThemeManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class AccountSubWindow {
    private Stage stage;
    private DataBaseManager dataBaseManager;
    private int userID;
    private Label username;
    private Label password;
    private Connection connection;

    public AccountSubWindow(DataBaseManager dataBaseManager, int userID) {
        this.stage = new Stage();
        this.userID = userID;
        this.dataBaseManager = dataBaseManager;
        this.connection = dataBaseManager.getConnection();
        createWindow();
    }

    private void createWindow() { //ventana cuenta

        String lightTheme = ThemeManager.getLIGHTHEME();
        String darkTheme = ThemeManager.getDARKTHEME();

        stage.setTitle("Cuenta");

        username = new Label("Nombre Usuario");
        password = new Label("Contraseña");

        TextField textFieldUsername = new TextField(getUserName(userID));
        TextField textFieldPassword = new TextField(getPassword(userID));
        textFieldUsername.setMaxWidth(100);
        textFieldPassword.setMaxWidth(100);

        textFieldUsername.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        textFieldPassword.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        textFieldUsername.getStyleClass().add("custom-text-field");
        textFieldPassword.getStyleClass().add("custom-text-field");
        username.getStyleClass().add("custom-label");
        password.getStyleClass().add("custom-label");

        VBox vBox = new VBox(username, textFieldUsername,password,textFieldPassword);
        vBox.setSpacing(15);
        vBox.setAlignment(Pos.BASELINE_CENTER);

        Scene scene = new Scene(vBox, 400, 450);

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
