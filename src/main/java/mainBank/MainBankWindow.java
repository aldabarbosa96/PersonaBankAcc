package mainBank;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 * Clase principal de la aplicación Personal Bank Account (PBA).
 */
public class MainBankWindow extends Application {
    private ArrayList<String> historial = new ArrayList<>();
    private DecimalFormat df;
    private double totalInicial = 0.0;
    private DataBaseManager dbmanager;
    private int userId;
    private Scene scene;
    private String lightTheme;
    private String darkTheme;

    /**
     * Constructor de la clase MainBank.
     *
     * @param dbmanager Gestor de la DB.
     * @param userId    Identificador del usuario.
     */
    public MainBankWindow(DataBaseManager dbmanager, int userId) {
        this.dbmanager = dbmanager;
        this.userId = userId;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        df = new DecimalFormat("0.00", symbols);
        dbmanager.setDecimalFormat(df);
    }

    @Override
    public void start(Stage primaryStage) {
        Label labelRegistros = new Label("Ingresos/Gastos registrados: ");
        Label labelTotal = new Label("TOTAL: ");

        TextField textFieldCantidad = new TextField("00.00");
        textFieldCantidad.setPromptText("00.00"); //placeholder
        textFieldCantidad.setPrefWidth(105);
        textFieldCantidad.setMinHeight(10);

        TextField textFieldConcepto = new TextField();
        textFieldConcepto.setPromptText("Concepto");
        textFieldConcepto.setPrefWidth(105);
        textFieldConcepto.setMinHeight(10);


        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() <= 16) { //formateador 16 chars
                return change;
            } else {
                return null;
            }
        });
        textFieldConcepto.setTextFormatter(textFormatter);

        HBox hboxCantidad = new HBox(10, textFieldCantidad);
        hboxCantidad.setAlignment(Pos.CENTER_LEFT);

        Button botonTema = new Button("☽");
        botonTema.setMinWidth(30);
        botonTema.setMinHeight(30);
        botonTema.setFocusTraversable(false); //evita que el botón sea enfocable con Tab

        Button botonDetalles = new Button("Detalles");
        botonDetalles.setMinHeight(30);
        botonDetalles.setMinHeight(30);
        botonDetalles.setFocusTraversable(false);


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox hboxCantidadTema = new HBox(10, hboxCantidad, spacer, botonTema, botonDetalles);
        hboxCantidadTema.setAlignment(Pos.CENTER_LEFT);

        HBox hboxConcepto = new HBox(10, textFieldConcepto);
        hboxConcepto.setAlignment(Pos.CENTER_LEFT);

        Button botonIngreso = new Button("INGRESO");
        Button botonGasto = new Button("GASTO");
        Button botonUndo = new Button("Deshacer ⎌");
        botonIngreso.setMinWidth(108);
        botonGasto.setMinWidth(108);
        botonUndo.setMinWidth(88);

        HBox hboxBotones = new HBox(10, botonIngreso, botonGasto);
        hboxBotones.setAlignment(Pos.CENTER_LEFT);

        TextArea historialArea = new TextArea();
        historialArea.setPrefSize(250, 320);
        historialArea.setText("         HISTORIAL\n-----------------------------\n");
        historialArea.setEditable(false);

        TextArea fechaHoraArea = new TextArea();
        fechaHoraArea.setPrefSize(175, 320);
        fechaHoraArea.setEditable(false);

        historialArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        fechaHoraArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        labelRegistros.getStyleClass().add("custom-label");
        labelTotal.getStyleClass().add("custom-label");

        historialArea.getStyleClass().add("custom-text-area");
        fechaHoraArea.getStyleClass().add("custom-text-area");

        textFieldCantidad.getStyleClass().add("custom-text-field");
        textFieldConcepto.getStyleClass().add("custom-text-field");

        HBox hboxHistorial = new HBox(0, historialArea, fechaHoraArea);
        hboxHistorial.setAlignment(Pos.CENTER_LEFT);

        ArrayList<String> fechasHoras = new ArrayList<>();
        ArrayList<String[]> transactions = dbmanager.getUserTransactions(userId);

        fechaHoraArea.setText("\n\n");

        for (String[] transaction : transactions) {
            String formattedLine = transaction[0];
            historialArea.appendText(formattedLine + "\n");
            fechaHoraArea.appendText(transaction[1] + "\n");

            String[] parts = formattedLine.trim().split("\\s+", 3);

            if (parts.length >= 2) {
                String tipo = parts[0];
                String cantidadStr = parts[1];

                double cantidad = Double.parseDouble(cantidadStr.replace(",", "."));

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

        labelTotal.setText("TOTAL:  " + df.format(totalInicial));

        VBox vbox = new VBox(20, hboxCantidadTema, hboxConcepto, hboxBotones, labelRegistros, hboxHistorial, labelTotal, botonUndo);
        vbox.setPadding(new Insets(20));

        scene = new Scene(vbox, 420, 620);

        lightTheme = Objects.requireNonNull(getClass().getResource("/cssThemes/light-theme.css")).toExternalForm();
        darkTheme = Objects.requireNonNull(getClass().getResource("/cssThemes/dark-theme.css")).toExternalForm();

        scene.getStylesheets().add(lightTheme);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PersonalBankAccount");
        primaryStage.show();

        ButtonActionsManager buttonActions = new ButtonActionsManager(totalInicial, historial, fechasHoras, labelRegistros, labelTotal, historialArea, fechaHoraArea, df, dbmanager, userId, scene, lightTheme, darkTheme);
        DetailsWindow detailsWindow = new DetailsWindow(userId, totalInicial);

        botonIngreso.setOnAction(e -> buttonActions.registrarIngreso(textFieldCantidad, textFieldConcepto));
        botonGasto.setOnAction(e -> buttonActions.registrarGasto(textFieldCantidad, textFieldConcepto));
        botonUndo.setOnAction(e -> buttonActions.deshacer());
        botonTema.setOnAction(e -> buttonActions.cambiarTema(botonTema));
        botonDetalles.setOnAction(e -> {
            try {
                detailsWindow.start(primaryStage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
