package mainBank.subWindows;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainBank.managers.LanguageManager;
import mainBank.managers.ThemeManager;

import java.io.*;
import java.util.ResourceBundle;

public class HelpSubWindow {
    private Stage stage;
    private ResourceBundle resources;

    public HelpSubWindow() {
        this.stage = new Stage();
        this.resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());
        try {
            createWindow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createWindow() throws IOException {
        stage.setTitle(resources.getString("help.title"));

        TextArea textAreaAjustes = new TextArea();
        textAreaAjustes.setPrefSize(375, 725);
        textAreaAjustes.setEditable(false);
        textAreaAjustes.setFocusTraversable(false);
        textAreaAjustes.getStyleClass().addAll("custom-text-area2", "custom-font");
        textAreaAjustes.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");
        textAreaAjustes.setWrapText(true);

        String fileName = "/others/ajustes_" + LanguageManager.getLocale().getLanguage() + ".txt";
        InputStream inputStream = getClass().getResourceAsStream(fileName);

        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder contenido = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                contenido.append(line).append("\n");
            }
            textAreaAjustes.setText(contenido.toString());

            reader.close();

        } else {
            System.out.println("Archivo no encontrado: " + fileName);
            textAreaAjustes.setText(resources.getString("help.fileNotFound"));
        }

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
