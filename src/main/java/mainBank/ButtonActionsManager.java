package mainBank;

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
    private Label label3;
    private TextArea historialArea;
    private TextArea fechaHoraArea;
    private DecimalFormat df;
    private DataBaseManager dbmanager;
    private int userId;

    /**
     * Constructor de la clase ButtonActionsManager.
     *
     * @param totalInicial  Total inicial del usuario.
     * @param historial     Lista del historial de transacciones.
     * @param fechas        Lista de fechas y horas de las transacciones.
     * @param label1        Label para mostrar mensajes.
     * @param label2        Label para mostrar el total.
     * @param label3        Label para mostrar el concepto (no utilizado en este contexto).
     * @param historialArea Área de texto para mostrar el historial.
     * @param fechaHoraArea Área de texto para mostrar fechas y horas.
     * @param df            Formato decimal para las cantidades.
     * @param dbmanager     Gestor de la DB.
     * @param userId        Identificador del usuario.
     */
    public ButtonActionsManager(double totalInicial, ArrayList<String> historial, ArrayList<String> fechas, Label label1, Label label2, Label label3, TextArea historialArea, TextArea fechaHoraArea, DecimalFormat df, DataBaseManager dbmanager, int userId) {
        this.total = totalInicial;
        this.historial = historial;
        this.fechas = fechas;
        this.label1 = label1;
        this.label2 = label2;
        this.label3 = label3;
        this.historialArea = historialArea;
        this.fechaHoraArea = fechaHoraArea;
        this.df = df;
        this.dbmanager = dbmanager;
        this.userId = userId;
    }

    /**
     * Registra un ingreso en el sistema.
     *
     * @param textField Campo de texto donde se ingresa la cantidad.
     * @param concepto  Campo de texto donde se ingresa el concepto.
     */
    public void registrarIngreso(TextField textField, TextField concepto) {
        //obtenemos la hora/fecha actual
        LocalDateTime actualidad = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd");
        String fechaHora = actualidad.format(formato);

        try {
            //actualizamos el total
            double cantidad = Double.parseDouble(textField.getText());
            total += cantidad;

            //actualizamos las labels
            label1.setText("Último ingreso:  " + df.format(cantidad));
            label2.setText("TOTAL:  " + df.format(total));

            String transaccionConFecha = "+ " + df.format(cantidad);
            String concepto1 = concepto.getText().trim();

            //truncamos el concepto si es demasiado largo
            if (concepto1.length() > 25) {
                concepto1 = concepto1.substring(0, 25);
            }

            //formateamos la línea para el historial
            String formattedLine = String.format("  %-10s %-25s", transaccionConFecha, concepto1);
            historialArea.appendText(formattedLine + "\n");

            //doble salto de línea al inicio del registro de hora/fecha (para alinear con cantidades)
            if (fechaHoraArea.getText().isEmpty()) {
                fechaHoraArea.setText("\n\n");
            }

            //añadimos cada elemento
            historial.add(formattedLine);
            fechas.add(fechaHora);
            fechaHoraArea.appendText(fechaHora + "\n");

            //registramos transacción en la DB
            dbmanager.insertTransaction(userId, "+", cantidad, fechaHora, concepto1);

            //limpiamos los campos y le devolvemos el foco a cantidad
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
        //igual que registrarIngreso() pero restando al total
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
            //elimina última transacción y fecha de las listas
            String lastTransaction = historial.remove(historial.size() - 1);
            fechas.remove(fechas.size() - 1);

            //trim y split de la transacción para obtener tipo y cantidad por separado
            String[] parts = lastTransaction.trim().split("\\s+", 3);

            if (parts.length >= 2) {
                String tipo = parts[0];
                String cantidadStr = parts[1];

                double valor = Double.parseDouble(cantidadStr.replace(",", "."));

                //revertimos la operación en el total
                if (tipo.equals("+")) {
                    total -= valor;
                } else {
                    total += valor;
                }

                //actualizamos labels
                label1.setText("Última acción deshecha:  " + lastTransaction);
                label2.setText("TOTAL:  " + df.format(total));

                //actualizamos áreas de texto eliminando la última línea
                String historialTexto = historialArea.getText();
                int ultimaLinea = historialTexto.lastIndexOf("\n", historialTexto.length() - 2);
                historialArea.setText(historialTexto.substring(0, ultimaLinea + 1));

                String fechaHoraTexto = fechaHoraArea.getText();
                int ultimaLineaFechaHora = fechaHoraTexto.lastIndexOf("\n", fechaHoraTexto.length() - 2);
                fechaHoraArea.setText(fechaHoraTexto.substring(0, ultimaLineaFechaHora + 1));

                //eliminamos la última transacción de la DB
                dbmanager.deleteLastTransaction(userId);
            } else {
                System.out.println("Formato de transacción inválido al deshacer: " + lastTransaction);
            }
        }
    }
}
