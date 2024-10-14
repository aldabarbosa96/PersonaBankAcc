package mainBank;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mainBank.subWindows.AccountSubWindow;
import mainBank.subWindows.ConfigurationSubWindow;
import mainBank.subWindows.HelpSubWindow;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainBankWindow {
    private ArrayList<String> historial = new ArrayList<>();
    private ArrayList<String> fechas = new ArrayList<>();
    private DecimalFormat df;
    private double totalInicial = 0.0;
    private DataBaseManager dbmanager;
    private int userId;
    private Scene scene;
    private String lightTheme, darkTheme;
    private Stage detallesStage, ajustesStage;
    private ResourceBundle resources;
    private Label labelRegistros;
    private Label labelTotal;
    private TextField textFieldCantidad;
    private TextField textFieldConcepto;
    private Button botonIngreso;
    private Button botonGasto;
    private Button botonUndo;
    private Button botonDetalles;
    private MenuItem menu1;
    private MenuItem menu2;
    private MenuItem menu3;
    private Stage primaryStage;

    public MainBankWindow(DataBaseManager dbmanager, int userId) {
        this.dbmanager = dbmanager;
        this.userId = userId;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(LanguageManager.getLocale());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        df = new DecimalFormat("#,##0.00", symbols);
        dbmanager.setDecimalFormat(df);
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());

        // Configuración de etiquetas
        labelRegistros = new Label(resources.getString("main.records"));
        labelTotal = new Label(resources.getString("main.total") + ": ");

        // Configuración de campos de texto
        textFieldCantidad = new TextField();
        textFieldCantidad.setPromptText(resources.getString("main.amount"));
        textFieldCantidad.setPrefWidth(115);
        textFieldCantidad.setMinHeight(25);

        textFieldConcepto = new TextField();
        textFieldConcepto.setPromptText(resources.getString("main.concept"));
        textFieldConcepto.setPrefWidth(240);
        textFieldConcepto.setMinHeight(30);

        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.length() <= 16 ? change : null;
        });
        textFieldConcepto.setTextFormatter(textFormatter);

        // Configuración de botones y menús
        Button botonTema = new Button();
        botonTema.setMinWidth(30);
        botonTema.setMinHeight(30);
        botonTema.setFocusTraversable(false);

        Button botonAjustes = new Button();
        botonAjustes.setMinWidth(30);
        botonAjustes.setMinHeight(30);
        botonAjustes.setFocusTraversable(false);

        ContextMenu contextMenu = new ContextMenu();
        menu1 = new MenuItem(resources.getString("main.config"));
        menu2 = new MenuItem(resources.getString("main.account"));
        menu3 = new MenuItem(resources.getString("main.help"));
        contextMenu.getItems().addAll(menu1, menu2, menu3);

        botonAjustes.setOnAction(e -> {
            if (!contextMenu.isShowing()) {
                contextMenu.show(botonAjustes, javafx.geometry.Side.BOTTOM, 0, 0);
            } else {
                contextMenu.hide();
            }
        });

        menu1.setOnAction(e -> {
            if (ajustesStage == null || !ajustesStage.isShowing()) {
                ConfigurationSubWindow configSubWindow = new ConfigurationSubWindow(this);
                ajustesStage = configSubWindow.getStage();
                ajustesStage.show();
            } else {
                ajustesStage.close();
            }
        });

        menu2.setOnAction(e -> {
            if (ajustesStage == null || !ajustesStage.isShowing()) {
                AccountSubWindow accountSubWindow = new AccountSubWindow(dbmanager, userId);
                ajustesStage = accountSubWindow.getStage();
                ajustesStage.show();
            } else {
                ajustesStage.close();
            }
        });

        menu3.setOnAction(e -> {
            if (ajustesStage == null || !ajustesStage.isShowing()) {
                HelpSubWindow helpSubWindow = new HelpSubWindow();
                ajustesStage = helpSubWindow.getStage();
                ajustesStage.show();
            } else {
                ajustesStage.close();
            }
        });

        // Configuración de imágenes e íconos
        Image iconSol = new Image(getClass().getResourceAsStream("/images/sun.png"));
        Image iconLuna = new Image(getClass().getResourceAsStream("/images/moon.png"));
        Image iconUndo = new Image(getClass().getResourceAsStream("/images/undo.png"));
        Image iconConfig = new Image(getClass().getResourceAsStream("/images/settings.png"));

        ImageView viewSol = new ImageView(iconSol);
        ImageView viewLuna = new ImageView(iconLuna);
        ImageView viewUndo = new ImageView(iconUndo);
        ImageView viewConfig = new ImageView(iconConfig);

        viewSol.setFitWidth(20);
        viewSol.setFitHeight(20);
        viewLuna.setFitWidth(20);
        viewLuna.setFitHeight(20);
        viewUndo.setFitWidth(10);
        viewUndo.setFitHeight(10);
        viewConfig.setFitWidth(20);
        viewConfig.setFitHeight(20);

        botonAjustes.setGraphic(viewConfig);

        if (ThemeManager.getCurrentTheme().equals("light")) {
            botonTema.setGraphic(viewLuna);
        } else {
            botonTema.setGraphic(viewSol);
        }

        botonDetalles = new Button(resources.getString("main.details"));
        botonDetalles.setMinHeight(30);
        botonDetalles.setMinWidth(90);
        botonDetalles.setFocusTraversable(false);

        botonIngreso = new Button(resources.getString("main.income"));
        botonGasto = new Button(resources.getString("main.expense"));
        botonUndo = new Button(resources.getString("main.undo"));
        botonIngreso.setMinWidth(115);
        botonGasto.setMinWidth(115);
        botonUndo.setMinWidth(88);
        botonUndo.setGraphic(viewUndo);

        // Configuración de áreas de texto
        TextArea historialArea = new TextArea();
        historialArea.setPrefSize(250, 320);
        historialArea.setText("         HISTORIAL\n-----------------------------\n");
        historialArea.setEditable(false);
        historialArea.setFocusTraversable(false);

        TextArea fechaHoraArea = new TextArea();
        fechaHoraArea.setPrefSize(160, 320);
        fechaHoraArea.setEditable(false);
        fechaHoraArea.setFocusTraversable(false);

        historialArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        fechaHoraArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        // Aplicación de estilos
        labelRegistros.getStyleClass().add("custom-label");
        labelTotal.getStyleClass().add("custom-label");
        historialArea.getStyleClass().add("custom-text-area");
        fechaHoraArea.getStyleClass().add("custom-text-area");
        textFieldCantidad.getStyleClass().add("custom-text-field");
        textFieldConcepto.getStyleClass().add("custom-text-field");

        // Configuración de eventos y filtros para áreas de texto
        Platform.runLater(() -> {
            ScrollPane scrollPane = (ScrollPane) historialArea.lookup(".scroll-pane");
            if (scrollPane != null) {
                scrollPane.hvalueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.doubleValue() != 0) {
                        scrollPane.setHvalue(0);
                    }
                });
                scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
                    event.consume();
                });
                scrollPane.getContent().addEventFilter(ScrollEvent.SCROLL, event -> {
                    event.consume();
                });
            }
            historialArea.addEventFilter(MouseEvent.ANY, event -> {
                event.consume();
            });
        });

        Platform.runLater(() -> {
            ScrollPane scrollPane = (ScrollPane) fechaHoraArea.lookup(".scroll-pane");
            if (scrollPane != null) {
                scrollPane.hvalueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.doubleValue() != 0) {
                        scrollPane.setHvalue(0);
                    }
                });
                scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
                    event.consume();
                });
                scrollPane.getContent().addEventFilter(ScrollEvent.SCROLL, event -> {
                    event.consume();
                });
                fechaHoraArea.addEventFilter(MouseEvent.ANY, event -> {
                    event.consume();
                });
            }
        });

        // Construcción de layouts
        HBox hboxCantidad = new HBox(10, textFieldCantidad);
        hboxCantidad.setAlignment(Pos.CENTER_LEFT);

        Region spacerTop = new Region();
        HBox.setHgrow(spacerTop, Priority.ALWAYS);
        HBox hboxCantidadTema = new HBox(10, hboxCantidad, spacerTop, botonTema);
        hboxCantidadTema.setAlignment(Pos.CENTER_LEFT);

        HBox hboxConceptoAjustes = new HBox();
        hboxConceptoAjustes.setAlignment(Pos.CENTER_LEFT);
        hboxConceptoAjustes.setSpacing(10);
        Region spacerConceptoAjsutes = new Region();
        HBox.setHgrow(spacerConceptoAjsutes, Priority.ALWAYS);
        hboxConceptoAjustes.getChildren().addAll(textFieldConcepto, spacerConceptoAjsutes, botonAjustes);

        HBox hboxBotones = new HBox(10, botonIngreso, botonGasto);
        hboxBotones.setAlignment(Pos.CENTER_LEFT);

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);
        HBox hboxHistorial = new HBox(spacerLeft, historialArea, fechaHoraArea, spacerRight);
        hboxHistorial.setAlignment(Pos.CENTER_LEFT);

        HBox hboxTotal = new HBox(labelTotal);
        hboxTotal.setAlignment(Pos.CENTER_LEFT);

        HBox hboxDeshacerDetalles = new HBox();
        hboxDeshacerDetalles.setAlignment(Pos.CENTER_LEFT);
        hboxDeshacerDetalles.setSpacing(10);
        Region spacerDeshacer = new Region();
        HBox.setHgrow(spacerDeshacer, Priority.ALWAYS);
        hboxDeshacerDetalles.getChildren().addAll(botonUndo, spacerDeshacer, botonDetalles);

        VBox vbox = new VBox(20, hboxCantidadTema, hboxConceptoAjustes, hboxBotones, labelRegistros,
                hboxHistorial, hboxTotal, hboxDeshacerDetalles);
        vbox.setPadding(new Insets(20));

        // Carga de datos y actualización de componentes
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

        labelTotal.setText(resources.getString("main.total") + ": " + df.format(totalInicial) + " €");

        // Configuración de temas
        scene = new Scene(vbox, 480, 620);

        lightTheme = ThemeManager.getLIGHTHEME();
        darkTheme = ThemeManager.getDARKTHEME();

        scene.getStylesheets().add(lightTheme);

        ThemeManager.currentThemeProperty().addListener((observable, oldValue, newValue) -> {
            scene.getStylesheets().clear();
            if (newValue.equals("light")) {
                scene.getStylesheets().add(lightTheme);
            } else {
                scene.getStylesheets().add(darkTheme);
            }
        });

        // Configuración del escenario (Stage)
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle(resources.getString("main.title"));
        primaryStage.show();

        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            if (detallesStage != null && detallesStage.isShowing()) {
                detallesStage.close();
            }
            if (ajustesStage != null && ajustesStage.isShowing()) {
                ajustesStage.close();
            }
        });

        // Asignación de manejadores de eventos
        botonIngreso.setOnAction(e -> {
            ButtonActionsManager buttonActions = new ButtonActionsManager(totalInicial, historial, fechas,
                    labelRegistros, labelTotal, historialArea, fechaHoraArea, df, dbmanager, userId, scene,
                    lightTheme, darkTheme);
            buttonActions.registrarIngreso(textFieldCantidad, textFieldConcepto);
            totalInicial = buttonActions.getTotal(); // Actualizar totalInicial
        });

        botonGasto.setOnAction(e -> {
            ButtonActionsManager buttonActions = new ButtonActionsManager(totalInicial, historial, fechas,
                    labelRegistros, labelTotal, historialArea, fechaHoraArea, df, dbmanager, userId, scene,
                    lightTheme, darkTheme);
            buttonActions.registrarGasto(textFieldCantidad, textFieldConcepto);
            totalInicial = buttonActions.getTotal(); // Actualizar totalInicial
        });

        botonUndo.setOnAction(e -> {
            ButtonActionsManager buttonActions = new ButtonActionsManager(totalInicial, historial, fechas,
                    labelRegistros, labelTotal, historialArea, fechaHoraArea, df, dbmanager, userId, scene,
                    lightTheme, darkTheme);
            buttonActions.deshacer();
            totalInicial = buttonActions.getTotal(); // Actualizar totalInicial
        });

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

    public void updateTexts() {
        resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());

        // Actualizar etiquetas y textos
        labelRegistros.setText(resources.getString("main.records"));
        labelTotal.setText(resources.getString("main.total") + ": " + df.format(totalInicial) + " €");
        botonIngreso.setText(resources.getString("main.income"));
        botonGasto.setText(resources.getString("main.expense"));
        botonUndo.setText(resources.getString("main.undo"));
        botonDetalles.setText(resources.getString("main.details"));
        textFieldCantidad.setPromptText(resources.getString("main.amount"));
        textFieldConcepto.setPromptText(resources.getString("main.concept"));
        primaryStage.setTitle(resources.getString("main.title"));

        // Actualizar menús
        menu1.setText(resources.getString("main.config"));
        menu2.setText(resources.getString("main.account"));
        menu3.setText(resources.getString("main.help"));
    }

}
