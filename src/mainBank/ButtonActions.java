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
    private Label label1;
    private Label label2;
    private TextArea historialArea;
    private DecimalFormat df;

    public ButtonActions(double totalInicial, ArrayList<String> historial, Label label1, Label label2, TextArea historialArea, DecimalFormat df) {
        this.total = totalInicial;
        this.historial = historial;
        this.label1 = label1;
        this.label2 = label2;
        this.historialArea = historialArea;
        this.df = df;
    }

    public void registrarIngreso(TextField textField) {


        try {
            double cantidad = Double.parseDouble(textField.getText());
            total += cantidad;

            label1.setText("Último ingreso:  " + df.format(cantidad));
            label2.setText("TOTAL:  " + df.format(total));

            historial.add("+" + df.format(cantidad));
            historialArea.appendText("+" + df.format(cantidad) + "\n");

            textField.clear();
        } catch (NumberFormatException e) {
            label1.setText("Por favor, introduce un número válido.");
        }
    }

    public void registrarGasto(TextField textField) {


        try {
            double cantidad = Double.parseDouble(textField.getText());
            total -= cantidad;

            label1.setText("Último gasto:  " + df.format(cantidad));
            label2.setText("TOTAL:  " + df.format(total));

            historial.add("-" + df.format(cantidad));
            historialArea.appendText("-" + df.format(cantidad) + "\n");

            textField.clear();
        } catch (NumberFormatException e) {
            label1.setText("Por favor, introduce un número válido.");
        }
    }

    public void deshacer() {
        if (!historial.isEmpty()) {
            String lastTransaction = historial.remove(historial.size() - 1);

            double valor = Double.parseDouble(lastTransaction.substring(1).replace(",", "."));

            if (lastTransaction.startsWith("+")) {
                total -= valor;
            } else {
                total += valor;
            }

            label1.setText("Última acción deshecha: " + lastTransaction);
            label2.setText("TOTAL:  " + df.format(total));

            String historialTexto = historialArea.getText();
            int ultimaLinea = historialTexto.lastIndexOf("\n", historialTexto.length() - 2);
            historialArea.setText(historialTexto.substring(0, ultimaLinea + 1));
        }
    }
}
