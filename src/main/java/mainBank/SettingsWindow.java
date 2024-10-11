package mainBank;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;

public class SettingsWindow {
    private Stage stage;

    public SettingsWindow() {
        this.stage = new Stage();
        try {
            createWindow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createWindow() throws IOException { //por ahora esta ventana solo muestra el futuro apartado "Ayuda"
        stage.setTitle("Ajustes");

        TextArea textAreaAjustes = new TextArea();
        textAreaAjustes.setPrefSize(400, 480);
        textAreaAjustes.setEditable(false);
        textAreaAjustes.getStyleClass().addAll("custom-text-area", "custom-font");
        textAreaAjustes.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");
        textAreaAjustes.setWrapText(true);

        InputStream inputStream = getClass().getResourceAsStream("/others/ajustes.txt");

        if (inputStream != null) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder contentenido = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                contentenido.append(line).append("\n");
            }
            textAreaAjustes.setText(contentenido.toString());

            reader.close();

        } else System.out.println("Archivo no encontrado");

        VBox vBox = new VBox(textAreaAjustes);
        Scene scene = new Scene(vBox, 860, 500);

        String lightTheme = Objects.requireNonNull(getClass().getResource("/cssThemes/light-theme.css")).toExternalForm();
        String darkTheme = Objects.requireNonNull(getClass().getResource("/cssThemes/dark-theme.css")).toExternalForm();

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

    public Stage getStage() {
        return stage;
    }
}
