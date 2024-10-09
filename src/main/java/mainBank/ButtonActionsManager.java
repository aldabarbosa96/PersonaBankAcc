package mainBank;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Clase que gestiona las acciones de los botones en la app.
 */
public class ButtonActionsManager {
    private double total;
    private ArrayList<String> historial;
    private ArrayList<String> fechas;
    private Label label1;
    private Label label2;
    private TextArea historialArea;
    private TextArea fechaHoraArea;
    private DecimalFormat df;
    private DataBaseManager dbmanager;
    private int userId;
    private boolean isDarkTheme = false;
    private Scene scene;
    private String lightTheme;
    private String darkTheme;

    /**
     * Constructor de la clase ButtonActionsManager.
     *
     * @param totalInicial  Total inicial del usuario.
     * @param historial     Lista del historial de transacciones.
     * @param fechas        Lista de fechas y horas de las transacciones.
     * @param label1        Label para mostrar mensajes.
     * @param label2        Label para mostrar el total.
     * @param historialArea Área de texto para mostrar el historial.
     * @param fechaHoraArea Área de texto para mostrar fechas y horas.
     * @param df            Formato decimal para las cantidades.
     * @param dbmanager     Gestor de la DB.
     * @param userId        Identificador del usuario.
     * @param scene         La escena principal.
     * @param lightTheme    Ruta al tema claro.
     * @param darkTheme     Ruta al tema oscuro.
     */
    public ButtonActionsManager(double totalInicial, ArrayList<String> historial, ArrayList<String> fechas, Label label1, Label label2, TextArea historialArea, TextArea fechaHoraArea, DecimalFormat df, DataBaseManager dbmanager, int userId, Scene scene, String lightTheme, String darkTheme) {
        this.total = totalInicial;
        this.historial = historial;
        this.fechas = fechas;
        this.label1 = label1;
        this.label2 = label2;
        this.historialArea = historialArea;
        this.fechaHoraArea = fechaHoraArea;
        this.df = df;
        this.dbmanager = dbmanager;
        this.userId = userId;
        this.scene = scene;
        this.lightTheme = lightTheme;
        this.darkTheme = darkTheme;
    }

    /**
     * Registra un ingreso en el sistema.
     *
     * @param textField Campo de texto donde se ingresa la cantidad.
     * @param concepto  Campo de texto donde se ingresa el concepto.
     */
    public void registrarIngreso(TextField textField, TextField concepto) {
        LocalDateTime actualidad = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd");
        String fechaHora = actualidad.format(formato);

        try {
            double cantidad = Double.parseDouble(textField.getText());
            total += cantidad;

            label1.setText("Último ingreso:  " + df.format(cantidad));
            label2.setText("TOTAL:  " + df.format(total));

            String transaccionConFecha = "+ " + df.format(cantidad);
            String concepto1 = concepto.getText().trim();

            if (concepto1.length() > 25) {
                concepto1 = concepto1.substring(0, 25);
            }

            String formattedLine = String.format("  %-10s %-25s", transaccionConFecha, concepto1);
            historialArea.appendText(formattedLine + "\n");

            if (fechaHoraArea.getText().isEmpty()) {
                fechaHoraArea.setText("\n\n");
            }

            historial.add(formattedLine);
            fechas.add(fechaHora);
            fechaHoraArea.appendText(fechaHora + "\n");

            dbmanager.insertTransaction(userId, "+", cantidad, fechaHora, concepto1);

            textField.clear();
            concepto.clear();
            textField.requestFocus();

        } catch (NumberFormatException e) {
            label1.setText("Por favor, introduce un número válido.");
        }
    }

    /**
     * Registra un gasto en el sistema.
     *
     * @param textField Campo de texto donde se ingresa la cantidad.
     * @param concepto  Campo de texto donde se ingresa el concepto.
     */
    public void registrarGasto(TextField textField, TextField concepto) {
        LocalDateTime actualidad = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd");
        String fechaHora = actualidad.format(formato);

        try {
            double cantidad = Double.parseDouble(textField.getText());
            total -= cantidad;

            label1.setText("Último gasto:  " + df.format(cantidad));
            label2.setText("TOTAL:  " + df.format(total));

            String transaccionConFecha = "- " + df.format(cantidad);
            String concepto1 = concepto.getText().trim();

            if (concepto1.length() > 25) {
                concepto1 = concepto1.substring(0, 25);
            }

            String formattedLine = String.format("  %-10s %-25s", transaccionConFecha, concepto1);
            historialArea.appendText(formattedLine + "\n");

            if (fechaHoraArea.getText().isEmpty()) {
                fechaHoraArea.setText("\n\n");
            }

            historial.add(formattedLine);
            fechas.add(fechaHora);
            fechaHoraArea.appendText(fechaHora + "\n");

            dbmanager.insertTransaction(userId, "-", cantidad, fechaHora, concepto1);

            textField.clear();
            concepto.clear();
            textField.requestFocus();

        } catch (NumberFormatException e) {
            label1.setText("Por favor, introduce un número válido.");
        }
    }

    /**
     * Deshace la última transacción realizada.
     */
    public void deshacer() {
        if (!historial.isEmpty() && !fechas.isEmpty()) {
            String lastTransaction = historial.remove(historial.size() - 1);
            fechas.remove(fechas.size() - 1);

            String[] parts = lastTransaction.trim().split("\\s+", 3);

            if (parts.length >= 2) {
                String tipo = parts[0];
                String cantidadStr = parts[1];

                double valor = Double.parseDouble(cantidadStr.replace(",", "."));

                if (tipo.equals("+")) {
                    total -= valor;
                } else {
                    total += valor;
                }

                label1.setText("Última acción deshecha:  " + lastTransaction);
                label2.setText("TOTAL:  " + df.format(total));

                String historialTexto = historialArea.getText();
                int ultimaLinea = historialTexto.lastIndexOf("\n", historialTexto.length() - 2);
                historialArea.setText(historialTexto.substring(0, ultimaLinea + 1));

                String fechaHoraTexto = fechaHoraArea.getText();
                int ultimaLineaFechaHora = fechaHoraTexto.lastIndexOf("\n", fechaHoraTexto.length() - 2);
                fechaHoraArea.setText(fechaHoraTexto.substring(0, ultimaLineaFechaHora + 1));

                dbmanager.deleteLastTransaction(userId);
            } else {
                System.out.println("Formato de transacción inválido al deshacer: " + lastTransaction);
            }
        }
    }

    /**
     * Cambia el tema de la aplicación entre claro y oscuro.
     *
     * @param toggleButton El botón que activa el cambio de tema.
     */
    public void cambiarTema(Button toggleButton) {
        if (ThemeManager.getCurrentTheme().equals("light")) {
            ThemeManager.setCurrentTheme("dark");
            toggleButton.setText("☀");
        } else {
            ThemeManager.setCurrentTheme("light");
            toggleButton.setText("☽");
        }
    }
}
