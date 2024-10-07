package mainBank;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Clase principal de la aplicación Personal Bank Account (PBA).
 */
public class MainBank extends Application {
    private ArrayList<String> historial = new ArrayList<>();
    private DecimalFormat df;
    private double totalInicial = 0.0;
    private DataBaseManager dbmanager;
    private int userId;

    /**
     * Constructor de la clase MainBank.
     *
     * @param dbmanager Gestor de la DB.
     * @param userId    Identificador del usuario.
     */
    public MainBank(DataBaseManager dbmanager, int userId) {
        this.dbmanager = dbmanager;
        this.userId = userId;
        //configuramos el formato decimal para utilizar el punto como separador
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        df = new DecimalFormat("0.00", symbols);
        dbmanager.setDecimalFormat(df);
    }

    @Override
    public void start(Stage primaryStage) {
        //etiquetas campos/mensajes
        Label labelCantidad = new Label("Introduzca cantidad:");
        Label labelRegistros = new Label("Ingresos/Gastos registrados: ");
        Label labelTotal = new Label("TOTAL: ");
        Label labelConcepto = new Label("Concepto:");

        //campos de texto para ingresar cantidad/concepto
        TextField textFieldCantidad = new TextField("00.00");
        textFieldCantidad.setPrefWidth(230);
        TextField textFieldConcepto = new TextField();
        textFieldConcepto.setPrefWidth(230);

        //formateador max. 16char
        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() <= 16) {
                return change;
            } else {
                return null;
            }
        });
        textFieldConcepto.setTextFormatter(textFormatter);

        //organización horizontal de etiqueta y campo cantidad/concepto
        HBox hboxCantidad = new HBox(10, labelCantidad, textFieldCantidad);
        hboxCantidad.setSpacing(10);
        HBox hboxConcepto = new HBox(10, labelConcepto, textFieldConcepto);
        hboxConcepto.setSpacing(10);

        //TextArea para historial transacciones
        TextArea historialArea = new TextArea();
        historialArea.setPrefSize(250, 320);
        historialArea.setText("HISTORIAL\n-----------------------------\n");
        historialArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12;");
        historialArea.setEditable(false);

        //TextArea para fecha/hora
        TextArea fechaHoraArea = new TextArea();
        fechaHoraArea.setPrefSize(250, 320);
        fechaHoraArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12;");
        fechaHoraArea.setEditable(false);

        //CSS para eliminar scrollbar inferior
        historialArea.getStyleClass().add("no-horizontal-scroll");
        String css = ".no-horizontal-scroll .scroll-pane {\n" +
                "    -fx-hbar-policy: never;\n" +
                "}\n";
        historialArea.getStylesheets().add("data:text/css," + css);

        //botones ingreso, gasto y undo
        Button botonIngreso = new Button("Registrar ingreso");
        Button botonGasto = new Button("Registrar gasto");
        Button botonUndo = new Button("Deshacer ⎌");
        botonIngreso.setMinWidth(110);
        botonGasto.setMinWidth(110);
        botonUndo.setMinWidth(88);

        //organización botones
        HBox hboxBotones = new HBox(10, botonIngreso, botonGasto);
        hboxBotones.setSpacing(10);

        //organización historial transacciones/fecha/hora
        HBox hboxHistorial = new HBox(0, historialArea, fechaHoraArea);

        //obtenemos transacciones en base al userID
        ArrayList<String> fechasHoras = new ArrayList<>();
        ArrayList<String[]> transactions = dbmanager.getUserTransactions(userId);

        fechaHoraArea.setText("\n\n");

        //procesado transacciones
        for (String[] transaction : transactions) {
            String formattedLine = transaction[0];
            historialArea.appendText(formattedLine + "\n");
            fechaHoraArea.appendText(transaction[1] + "\n");

            //dividimos la línea formateada para extraer tipo y cantidad
            String[] parts = formattedLine.trim().split("\\s+", 3);

            if (parts.length >= 2) {
                String tipo = parts[0];
                String cantidadStr = parts[1];

                double cantidad = Double.parseDouble(cantidadStr.replace(",", "."));

                //actualizamos total inicial según el tipo de transacción
                if (tipo.equals("+")) {
                    totalInicial += cantidad;
                } else if (tipo.equals("-")) {
                    totalInicial -= cantidad;
                }
            } else {
                System.out.println("Formato de transacción inválido: " + transaction[0]);
            }

            historial.add(formattedLine);
            fechasHoras.add(transaction[1]);
        }

        // Actualizar el texto del label con el total inicial
        labelTotal.setText("TOTAL:  " + df.format(totalInicial));

        //instanciamos gestor de botones
        ButtonActionsManager buttonActions = new ButtonActionsManager(totalInicial, historial, fechasHoras, labelRegistros, labelTotal, labelConcepto, historialArea, fechaHoraArea, df, dbmanager, userId);

        //asignamos funcionalidades a los botones
        botonIngreso.setOnAction(e -> buttonActions.registrarIngreso(textFieldCantidad, textFieldConcepto));
        botonGasto.setOnAction(e -> buttonActions.registrarGasto(textFieldCantidad, textFieldConcepto));
        botonUndo.setOnAction(e -> buttonActions.deshacer());

        //organizamos todos los elementos en un VBox
        VBox vbox = new VBox(10, labelCantidad, hboxCantidad, labelConcepto, hboxConcepto, hboxBotones, labelRegistros, hboxHistorial, labelTotal, botonUndo);
        vbox.setPadding(new Insets(20));

        //instanciamos y configuramos la escena
        Scene scene = new Scene(vbox, 500, 600);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PersonalBankAccount");
        primaryStage.show();
    }
}
