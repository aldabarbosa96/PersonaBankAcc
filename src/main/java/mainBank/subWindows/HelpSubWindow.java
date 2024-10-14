package mainBank.subWindows;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainBank.ThemeManager;

import java.io.*;

public class HelpSubWindow {
    private Stage stage;

    public HelpSubWindow() { //ventana ayuda
        this.stage = new Stage();
        try {
            createWindow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createWindow() throws IOException {
        stage.setTitle("Ayuda");

        TextArea textAreaAjustes = new TextArea();
        textAreaAjustes.setPrefSize(375, 725);
        textAreaAjustes.setEditable(false);
        textAreaAjustes.setFocusTraversable(false);
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
        Scene scene = new Scene(vBox, 380, 730);

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
    public Stage getStage() {
        return stage;
    }
}
