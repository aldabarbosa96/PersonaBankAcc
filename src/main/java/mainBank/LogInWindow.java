package mainBank;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Clase que gestiona la ventana de inicio de sesión y
 * registro de usuarios (primera ventana en mostrarse).
 */
public class LogInWindow extends Application {
    private DataBaseManager dbmanager = new DataBaseManager();

    @Override
    public void start(Stage stage) {
        dbmanager.dbconnect();
        dbmanager.createTable();

        Label usernameLabel = new Label("Username");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Iniciar Sesión");
        Button registerButton = new Button("Registrarse");
        loginButton.setMinWidth(90);
        registerButton.setMinWidth(90);

        VBox vbox = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, registerButton);
        vbox.setPadding(new Insets(20));

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            int userId = dbmanager.verifyUser(username, password);
            if (userId != -1) {
                showMessage("Inicio de sesión correcto", "Bienvenido/a " + username);
                new MainBankWindow(dbmanager, userId).start(stage);
            } else {
                showError("Error", "Usuario o contraseña incorrectos");
            }
        });

        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Error de Registro", "El nombre de usuario y/o la contraseña no pueden estar vacíos.");
                return;
            }

            boolean success = dbmanager.insertUser(username, password);
            if (success) {
                showMessage("Éxito", "Usuario registrado con éxito");
            } else {
                if (!dbmanager.userIsValid(username)) {
                    showError("Error de Registro", "Nombre de usuario inválido. Solo se permiten letras, números, guiones bajos y puntos.");
                } else {
                    showError("Error de Registro", "Nombre de usuario ya existente");
                }
            }
        });

        Scene scene = new Scene(vbox, 300, 220);
        stage.setScene(scene);
        stage.setTitle("Iniciar sesión");
        stage.show();

        stage.setOnCloseRequest(event -> dbmanager.dbdisconnect());
    }

    /**
     * (Ambos métodos muestran mensajes de depuración.
     *
     * @param title   Título del cuadro de diálogo.
     * @param message Mensaje a mostrar (formato estándard o formato error).
     */
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
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
