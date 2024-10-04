package mainBank;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ButtonActions {
    private double total;
    private ArrayList<String> historial;
    private ArrayList<String> fechas;
    private Label label1;
    private Label label2;
    private TextArea historialArea;
    private TextArea fechaHoraArea;
    private DecimalFormat df;

    public ButtonActions(double totalInicial, ArrayList<String> historial, ArrayList<String> fechas, Label label1, Label label2, TextArea historialArea, TextArea fechaHoraArea, DecimalFormat df) {
        this.total = totalInicial;
        this.historial = historial;
        this.fechas = fechas;
        this.label1 = label1;
        this.label2 = label2;
        this.historialArea = historialArea;
        this.fechaHoraArea = fechaHoraArea;
        this.df = df;
    }

    public void registrarIngreso(TextField textField) {
        LocalDateTime actualidad = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd");
        String fechaHora = actualidad.format(formato);

        try {
            double cantidad = Double.parseDouble(textField.getText());
            total += cantidad;

            label1.setText("Último ingreso:  " + df.format(cantidad));
            label2.setText("TOTAL:  " + df.format(total));

            String transaccionConFecha = "+ " + df.format(cantidad);
            historial.add(transaccionConFecha);
            historialArea.appendText(transaccionConFecha + "\n");

            if (fechaHoraArea.getText().isEmpty()) {
                fechaHoraArea.setText("\n\n");
            }

            fechas.add(fechaHora);
            fechaHoraArea.appendText(fechaHora + "\n");

            textField.clear();
        } catch (NumberFormatException e) {
            label1.setText("Por favor, introduce un número válido.");
        }
    }
    public void registrarGasto(TextField textField) {
        LocalDateTime actualidad = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd");
        String fechaHora = actualidad.format(formato);

        try {
            double cantidad = Double.parseDouble(textField.getText());
            total -= cantidad;

            label1.setText("Último gasto:  " + df.format(cantidad));
            label2.setText("TOTAL:  " + df.format(total));

            String transaccionConFecha = "-  " + df.format(cantidad);
            historial.add(transaccionConFecha);
            historialArea.appendText(transaccionConFecha + "\n");

            if (fechaHoraArea.getText().isEmpty()) {
                fechaHoraArea.setText("\n\n");
            }

            fechas.add(fechaHora);
            fechaHoraArea.appendText(fechaHora + "\n");

            textField.clear();
        } catch (NumberFormatException e) {
            label1.setText("Por favor, introduce un número válido.");
        }
    }

    public void deshacer() {
        if (!historial.isEmpty() && !fechas.isEmpty()) {

            String lastTransaction = historial.remove(historial.size() - 1);

            fechas.remove(fechas.size() - 1);

            String transaccionSinFecha = lastTransaction.split(" \\(")[0];
            double valor = Double.parseDouble(transaccionSinFecha.substring(1).replace(",", "."));

            if (transaccionSinFecha.startsWith("+")) {
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
        }
    }
}
