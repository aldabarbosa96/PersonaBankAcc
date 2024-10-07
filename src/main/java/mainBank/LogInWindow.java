package mainBank;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Clase para la ventana de inicio de sesión y registro de usuarios.
 */
public class LogInWindow extends Application {
    private DataBaseManager dbmanager = new DataBaseManager();

    @Override
    public void start(Stage stage) {
        //conectamos con la DB y creamos tablas (si no existen)
        dbmanager.dbconnect();
        dbmanager.createTable();

        //creamos campos/etiquetas para usuario y contraseña
        Label usernameLabel = new Label("Username");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField();

        //botones signin y login
        Button loginButton = new Button("Iniciar Sesión");
        Button registerButton = new Button("Registrarse");
        loginButton.setMinWidth(90);
        registerButton.setMinWidth(90);

        //organizamos en una VBOX
        VBox vbox = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, registerButton);
        vbox.setPadding(new Insets(20));

        //acción botón inicio sesión
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            int userId = dbmanager.verifyUser(username, password);
            if (userId != -1) {
                showMessage("Inicio de sesión correcto", "Bienvenid@ " + username);
                // Iniciar la aplicación principal pasando el userId
                new MainBank(dbmanager, userId).start(stage);
            } else {
                showMessage("Error", "Usuario o contraseña incorrectos");
            }
        });

        //acción botón registro
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (dbmanager.insertUser(username, password)) {
                showMessage("Éxito", "Usuario registrado con éxito");
            } else {
                showMessage("Error", "Nombre de usuario ya existente");
            }
        });

        //instanciamos y configuramos la escena
        Scene scene = new Scene(vbox, 300, 220);
        stage.setScene(scene);
        stage.setTitle("Iniciar sesión");
        stage.show();

        //descontectamos de la DB al cerrar ventana
        stage.setOnCloseRequest(event -> dbmanager.dbdisconnect());
    }

    /**
     * Muestra mensajes de depuración.
     *
     * @param title   Título del cuadro de diálogo.
     * @param message Mensaje a mostrar.
     */
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Método principal para iniciar la aplicación.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
