package mainBank;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Clase que gestiona la ventana de detalles de transacciones.
 */
public class DetailsWindow {
    private Stage stage;
    private DataBaseManager dbmanager;
    private int userId;
    private double total;
    private DecimalFormat df;

    public DetailsWindow(int userId, double total, DataBaseManager dbmanager, DecimalFormat df) {
        this.userId = userId;
        this.total = total;
        this.dbmanager = dbmanager;
        this.df = df;
        this.stage = new Stage();
        createWindow();
    }

    private void createWindow() {
        stage.setTitle("Detalles de Transacciones");

        ArrayList<String[]> transactions = dbmanager.getUserTransactions(userId);

        TextArea areaDetalles = new TextArea();
        areaDetalles.setPrefSize(800, 400);
        areaDetalles.setEditable(false);
        areaDetalles.getStyleClass().addAll("custom-text-area", "custom-font");
        areaDetalles.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        String header = String.format("%-12s %-20s %-10s %-12s %-10s\n", "Cantidad", "Concepto", "Hora", "Fecha", "Total");
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

                String linea = String.format("%-12s %-20s %-10s %-12s %-10s\n", tipo + cantidadStr, concepto, hora, fecha, totalFormateado);

                areaDetalles.appendText(linea);
            } else {
                System.out.println("Formato de transacción inválido: " + transaction[0]);
            }
        }

        VBox vbox = new VBox(areaDetalles);
        vbox.setPadding(new Insets(20));
        Scene scene = new Scene(vbox, 580, 400);

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
