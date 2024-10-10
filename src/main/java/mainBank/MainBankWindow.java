package mainBank;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
    private ArrayList<String> fechas = new ArrayList<>();
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
        textFieldCantidad.setPromptText("00.00");
        textFieldCantidad.setPrefWidth(105);
        textFieldCantidad.setMinHeight(25);

        TextField textFieldConcepto = new TextField();
        textFieldConcepto.setPromptText("Concepto");
        textFieldConcepto.setPrefWidth(240);
        textFieldConcepto.setMinHeight(30);

        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.length() <= 16 ? change : null;
        });
        textFieldConcepto.setTextFormatter(textFormatter);

        HBox hboxCantidad = new HBox(10, textFieldCantidad);
        hboxCantidad.setAlignment(Pos.CENTER_LEFT);

        Button botonTema = new Button();
        botonTema.setMinWidth(30);
        botonTema.setMinHeight(30);
        botonTema.setFocusTraversable(false);

        Image iconSol = new Image(getClass().getResourceAsStream("/images/sun.png"));
        Image iconLuna = new Image(getClass().getResourceAsStream("/images/moon.png"));
        Image iconUndo = new Image(getClass().getResourceAsStream("/images/undo.png"));

        ImageView viewSol = new ImageView(iconSol);
        ImageView viewLuna = new ImageView(iconLuna);
        ImageView viewUndo = new ImageView(iconUndo);

        viewSol.setFitWidth(20);
        viewSol.setFitHeight(20);
        viewLuna.setFitWidth(20);
        viewLuna.setFitHeight(20);
        viewUndo.setFitWidth(10);
        viewUndo.setFitHeight(10);

        if (ThemeManager.getCurrentTheme().equals("light")) {
            botonTema.setGraphic(viewLuna);
        } else {
            botonTema.setGraphic(viewSol);
        }

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
        Button botonUndo = new Button("Deshacer");
        botonIngreso.setMinWidth(115);
        botonGasto.setMinWidth(115);
        botonUndo.setMinWidth(88);
        botonUndo.setGraphic(viewUndo);

        HBox hboxBotones = new HBox(10, botonIngreso, botonGasto);
        hboxBotones.setAlignment(Pos.CENTER_LEFT);

        TextArea historialArea = new TextArea();
        historialArea.setPrefSize(250, 320);
        historialArea.setText("         HISTORIAL\n-----------------------------\n");
        historialArea.setEditable(false);
        historialArea.setFocusTraversable(false);

        Platform.runLater(() -> { //gestiona el bloqueo del desplazamiento horizontal en el historialArea
            ScrollPane scrollPane = (ScrollPane) historialArea.lookup(".scroll-pane");
            if (scrollPane != null) {
                scrollPane.hvalueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.doubleValue() != 0) {
                        scrollPane.setHvalue(0);
                    }
                });

                scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
                    if (event.getDeltaX() != 0) {
                        event.consume();
                    }
                });

                scrollPane.getContent().addEventFilter(ScrollEvent.SCROLL, event -> {
                    if (event.getDeltaX() != 0) {
                        event.consume();
                    }
                });
            }
        });

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

                historial.add(formattedLine);
                fechas.add(transaction[1]);
            } else {
                System.out.println("Formato de transacción inválido: " + transaction[0]);
            }
        }

        labelTotal.setText("TOTAL:  " + df.format(totalInicial) + " €");

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

        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            if (detallesStage != null && detallesStage.isShowing()) {
                detallesStage.close();
            }
        });

        ButtonActionsManager buttonActions = new ButtonActionsManager(totalInicial, historial, fechas, labelRegistros, labelTotal, historialArea, fechaHoraArea, df, dbmanager, userId, scene, lightTheme, darkTheme);

        botonIngreso.setOnAction(e -> buttonActions.registrarIngreso(textFieldCantidad, textFieldConcepto));
        botonGasto.setOnAction(e -> buttonActions.registrarGasto(textFieldCantidad, textFieldConcepto));
        botonUndo.setOnAction(e -> buttonActions.deshacer());

        botonTema.setOnAction(e -> {
            if (ThemeManager.getCurrentTheme().equals("light")) {
                ThemeManager.setCurrentTheme("dark");
                botonTema.setGraphic(viewSol);
            } else {
                ThemeManager.setCurrentTheme("light");
                botonTema.setGraphic(viewLuna);
            }
        });

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
