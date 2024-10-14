package mainBank;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainBank.LanguageManager;
import mainBank.ThemeManager;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DetailsWindow {
    private Stage stage;
    private DataBaseManager dbmanager;
    private int userId;
    private double total;
    private DecimalFormat df;
    private ResourceBundle resources;

    public DetailsWindow(int userId, double total, DataBaseManager dbmanager, DecimalFormat df) {
        this.userId = userId;
        this.total = total;
        this.dbmanager = dbmanager;
        this.df = df;
        this.stage = new Stage();
        this.resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());
        createWindow();
    }

    private void createWindow() {
        stage.setTitle(resources.getString("details.title"));

        ArrayList<String[]> transactions = dbmanager.getUserTransactions(userId);

        TextArea areaDetalles = new TextArea();
        areaDetalles.setPrefSize(800, 540);
        areaDetalles.setEditable(false);
        areaDetalles.getStyleClass().addAll("custom-text-area", "custom-font");
        areaDetalles.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");

        String header = String.format("%-12s %-20s %-10s %-12s %-10s\n",
                resources.getString("details.amount"),
                resources.getString("details.concept"),
                resources.getString("details.time"),
                resources.getString("details.date"),
                resources.getString("details.total"));
        areaDetalles.appendText(header);
        areaDetalles.appendText("---------------------------------------------------" +
                "----------------------------------------------------\n");

        double runningTotal = 0.0;

        for (String[] transaction : transactions) {
            String formattedLine = transaction[0];
            String timestamp = transaction[1];

            String[] parts = formattedLine.trim().split("\\s+", 3);
            if (parts.length >= 2) {
                String tipo = parts[0]; //"+" o "-"
                String cantidadStr = parts[1];
                String concepto = (parts.length > 2) ? parts[2] : "";

                cantidadStr = cantidadStr.replace(".", "").replace(",", ".");

                double cantidad = Double.parseDouble(cantidadStr);

                if (tipo.equals("+")) {
                    runningTotal += cantidad;
                } else if (tipo.equals("-")) {
                    runningTotal -= cantidad;
                }

                String totalFormateado = df.format(runningTotal) + " €";

                String hora = timestamp.substring(0, 8); //HH:mm:ss
                String fecha = timestamp.substring(9);    //yyyy-MM-dd

                String linea = String.format("%-12s %-20s %-10s %-12s %-10s\n",
                        tipo + cantidadStr, concepto, hora, fecha, totalFormateado);

                areaDetalles.appendText(linea);
            } else {
                System.out.println("Formato de transacción inválido: " + transaction[0]);
            }
        }

        VBox vbox = new VBox(areaDetalles);
        vbox.setPadding(new Insets(20));
        Scene scene = new Scene(vbox, 620, 550);

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
