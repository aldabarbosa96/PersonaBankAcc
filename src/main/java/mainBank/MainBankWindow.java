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
public class MainBankWindow {
    private ArrayList<String> historial = new ArrayList<>();
    private DecimalFormat df;
    private double totalInicial = 0.0;
    private DataBaseManager dbmanager;
    private int userId;
    private Scene scene;
    private String lightTheme;
    private String darkTheme;
    private Stage detallesStage;

    /**
     * Constructor de la clase MainBank.
     *
     * @param dbmanager Gestor de la DB.
     * @param userId    Identificador del usuario.
     */
    public MainBankWindow(DataBaseManager dbmanager, int userId) {
        this.dbmanager = dbmanager;
        this.userId = userId;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        df = new DecimalFormat("#,##0.00", symbols);

        dbmanager.setDecimalFormat(df);
    }

    public void start(Stage primaryStage) {
        Label labelRegistros = new Label("Ingresos/Gastos registrados: ");
        Label labelTotal = new Label("TOTAL: ");

        TextField textFieldCantidad = new TextField();
        textFieldCantidad.setPromptText("00.00"); //placeholder
        textFieldCantidad.setPrefWidth(105);
        textFieldCantidad.setMinHeight(25);

        TextField textFieldConcepto = new TextField();
        textFieldConcepto.setPromptText("Concepto");
        textFieldConcepto.setPrefWidth(240);
        textFieldConcepto.setMinHeight(30);


        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() <= 16) { //limitamos a 16 caracteres el concepto
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
        botonDetalles.setMinWidth(90);
        botonDetalles.setFocusTraversable(false);

        Region spacerTop = new Region();
        HBox.setHgrow(spacerTop, Priority.ALWAYS);

        HBox hboxCantidadTema = new HBox(10, hboxCantidad, spacerTop, botonTema);
        hboxCantidadTema.setAlignment(Pos.CENTER_LEFT);

        HBox hboxConcepto = new HBox(10, textFieldConcepto);
        hboxConcepto.setAlignment(Pos.CENTER_LEFT);

        Button botonIngreso = new Button("INGRESO");
        Button botonGasto = new Button("GASTO");
        Button botonUndo = new Button("Deshacer ⎌");
        botonIngreso.setMinWidth(115);
        botonGasto.setMinWidth(115);
        botonUndo.setMinWidth(88);

        HBox hboxBotones = new HBox(10, botonIngreso, botonGasto);
        hboxBotones.setAlignment(Pos.CENTER_LEFT);

        TextArea historialArea = new TextArea();
        historialArea.setPrefSize(250, 320);
        historialArea.setText("         HISTORIAL\n-----------------------------\n");
        historialArea.setEditable(false);
        historialArea.setFocusTraversable(false);

        TextArea fechaHoraArea = new TextArea();
        fechaHoraArea.setPrefSize(175, 320);
        fechaHoraArea.setEditable(false);
        fechaHoraArea.setFocusTraversable(false);

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

                cantidadStr = cantidadStr.replace(".", "").replace(",", ".");

                double cantidad = Double.parseDouble(cantidadStr);

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

        HBox hboxTotal = new HBox(labelTotal);
        hboxTotal.setAlignment(Pos.CENTER_LEFT);

        HBox hboxDeshacerDetalles = new HBox();
        hboxDeshacerDetalles.setAlignment(Pos.CENTER_LEFT);
        hboxDeshacerDetalles.setSpacing(10);
        Region spacerDeshacer = new Region();
        HBox.setHgrow(spacerDeshacer, Priority.ALWAYS);
        hboxDeshacerDetalles.getChildren().addAll(botonUndo, spacerDeshacer, botonDetalles);

        VBox vbox = new VBox(20, hboxCantidadTema, hboxConcepto, hboxBotones, labelRegistros, hboxHistorial, hboxTotal, hboxDeshacerDetalles);
        vbox.setPadding(new Insets(20));

        scene = new Scene(vbox, 460, 620);

        lightTheme = Objects.requireNonNull(getClass().getResource("/cssThemes/light-theme.css")).toExternalForm();
        darkTheme = Objects.requireNonNull(getClass().getResource("/cssThemes/dark-theme.css")).toExternalForm();

        scene.getStylesheets().add(lightTheme);

        ThemeManager.currentThemeProperty().addListener((observable, oldValue, newValue) -> {
            scene.getStylesheets().clear();
            if (newValue.equals("light")) {
                scene.getStylesheets().add(lightTheme);
            } else {
                scene.getStylesheets().add(darkTheme);
            }
        });

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PersonalBankAccount");
        primaryStage.show();

        ButtonActionsManager buttonActions = new ButtonActionsManager(totalInicial, historial, fechasHoras, labelRegistros, labelTotal, historialArea, fechaHoraArea, df, dbmanager, userId, scene, lightTheme, darkTheme);

        botonIngreso.setOnAction(e -> buttonActions.registrarIngreso(textFieldCantidad, textFieldConcepto));
        botonGasto.setOnAction(e -> buttonActions.registrarGasto(textFieldCantidad, textFieldConcepto));
        botonUndo.setOnAction(e -> buttonActions.deshacer());
        botonTema.setOnAction(e -> buttonActions.cambiarTema(botonTema));
        botonDetalles.setOnAction(e -> {
            if (detallesStage == null || !detallesStage.isShowing()) {
                DetailsWindow detailsWindow = new DetailsWindow(userId, totalInicial, dbmanager, df);
                detallesStage = detailsWindow.getStage();
                detallesStage.show();
            } else {
                detallesStage.close();
            }
        });

    }
}
