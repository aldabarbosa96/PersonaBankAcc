package mainBank.windows;

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
import mainBank.managers.CurrencyManager;
import mainBank.managers.DataBaseManager;
import mainBank.managers.LanguageManager;
import mainBank.managers.ThemeManager;
import mainBank.subWindows.AccountSubWindow;
import mainBank.subWindows.ConfigurationSubWindow;
import mainBank.subWindows.HelpSubWindow;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainBankWindow {
    //datos/recursos
    private ArrayList<Transaction> transactionsList = new ArrayList<>();
    private DecimalFormat dfTotal, dfTransaction;
    private double totalInicial = 0.0;
    private DataBaseManager dbmanager;
    private int userId;
    private ResourceBundle resources;

    //componenetes GUI
    private Scene scene;
    private String lightTheme, darkTheme;
    private double cantidad;
    private Stage detallesStage, ajustesStage, primaryStage;
    private Label labelRegistros, labelTotal;
    private TextField textFieldCantidad, textFieldConcepto;
    private Button botonIngreso, botonGasto, botonUndo, botonDetalles;
    private MenuItem menu1, menu2, menu3;
    private TextArea historialArea, fechaHoraArea;

    public MainBankWindow(DataBaseManager dbmanager, int userId) {
        this.dbmanager = dbmanager;
        this.userId = userId;
        updateDecimalFormats();
        dbmanager.setDecimalFormat(dfTotal);
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());

        //inicialización componentes principales
        configureThemes();
        initLabels();
        initTextFields();
        initButtonsAndMenus();
        initTextAreas();
        applyStyles();
        disableTextAreaInteraction(historialArea);
        disableTextAreaInteraction(fechaHoraArea);
        loadTransactions();

        //escena principal
        scene = new Scene(buildMainLayout(), 480, 620);
        applyCurrentTheme();
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle(resources.getString("main.title"));
        primaryStage.show();
        primaryStage.setOnCloseRequest(this::handleCloseEvent);

        updateCurrency();
        assignEventHandlers();
    }

    private void initLabels() {
        labelRegistros = new Label(resources.getString("main.records"));
        labelTotal = new Label(resources.getString("main.total") + ": ");
    }

    private void initTextFields() {
        textFieldCantidad = new TextField();
        textFieldCantidad.setPromptText(resources.getString("main.amount"));
        textFieldCantidad.setPrefWidth(115);
        textFieldCantidad.setMinHeight(25);

        textFieldConcepto = new TextField();
        textFieldConcepto.setPromptText(resources.getString("main.concept"));
        textFieldConcepto.setPrefWidth(240);
        textFieldConcepto.setMinHeight(30);
        textFieldConcepto.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().length() <= 16 ? change : null));
    }

    private void initButtonsAndMenus() {
        botonIngreso = new Button(resources.getString("main.income"));
        botonGasto = new Button(resources.getString("main.expense"));
        botonUndo = new Button(resources.getString("main.undo"), createImageView("/images/undo.png", 10, 10));
        botonDetalles = new Button(resources.getString("main.details"));
        botonIngreso.setMinWidth(115);
        botonGasto.setMinWidth(115);
        botonUndo.setMinWidth(88);
        botonDetalles.setMinWidth(90);

        Button botonTema = new Button();
        botonTema.setMinSize(30, 30);
        botonTema.setFocusTraversable(false);
        botonTema.setGraphic(ThemeManager.getCurrentTheme().equals("light") ? createImageView("/images/moon.png", 20, 20) : createImageView("/images/sun.png", 20, 20));
        botonTema.setOnAction(e -> toggleTheme(botonTema));

        Button botonAjustes = new Button();
        botonAjustes.setMinSize(30, 30);
        botonAjustes.setFocusTraversable(false);
        botonAjustes.setGraphic(createImageView("/images/settings.png", 20, 20));

        ContextMenu contextMenu = new ContextMenu();
        menu1 = new MenuItem(resources.getString("main.config"));
        menu2 = new MenuItem(resources.getString("main.account"));
        menu3 = new MenuItem(resources.getString("main.help"));
        contextMenu.getItems().addAll(menu1, menu2, menu3);
        botonAjustes.setOnAction(e -> toggleContextMenu(contextMenu, botonAjustes));

        menu1.setOnAction(e -> openSubWindow(new ConfigurationSubWindow(this).getStage()));
        menu2.setOnAction(e -> openSubWindow(new AccountSubWindow(dbmanager, userId).getStage()));
        menu3.setOnAction(e -> openSubWindow(new HelpSubWindow().getStage()));
    }

    private void initTextAreas() {
        historialArea = createTextArea("         HISTORIAL\n-----------------------------\n", 250, 320);
        fechaHoraArea = createTextArea("\n\n", 160, 320);
    }

    private void applyStyles() {
        String[] labelStyles = {"custom-label"};
        labelRegistros.getStyleClass().addAll(labelStyles);
        labelTotal.getStyleClass().addAll(labelStyles);

        String[] textAreaStyles = {"custom-text-area"};
        historialArea.getStyleClass().addAll(textAreaStyles);
        fechaHoraArea.getStyleClass().addAll(textAreaStyles);

        String[] textFieldStyles = {"custom-text-field"};
        textFieldCantidad.getStyleClass().addAll(textFieldStyles);
        textFieldConcepto.getStyleClass().addAll(textFieldStyles);
    }

    private VBox buildMainLayout() {
        HBox hboxCantidadTema = new HBox(10, new HBox(10, textFieldCantidad), createSpacer(), createThemeButton());
        hboxCantidadTema.setAlignment(Pos.CENTER_LEFT);

        HBox hboxConceptoAjustes = new HBox(10, textFieldConcepto, createSpacer(), createSettingsButton());
        hboxConceptoAjustes.setAlignment(Pos.CENTER_LEFT);

        HBox hboxBotones = new HBox(10, botonIngreso, botonGasto);
        hboxBotones.setAlignment(Pos.CENTER_LEFT);

        HBox hboxHistorial = new HBox(createSpacer(), historialArea, fechaHoraArea, createSpacer());
        hboxHistorial.setAlignment(Pos.CENTER_LEFT);

        HBox hboxTotal = new HBox(labelTotal);
        hboxTotal.setAlignment(Pos.CENTER_LEFT);

        HBox hboxDeshacerDetalles = new HBox(10, botonUndo, createSpacer(), botonDetalles);
        hboxDeshacerDetalles.setAlignment(Pos.CENTER_LEFT);

        VBox vbox = new VBox(20, hboxCantidadTema, hboxConceptoAjustes, hboxBotones, labelRegistros, hboxHistorial, hboxTotal, hboxDeshacerDetalles);
        vbox.setPadding(new Insets(20));

        return vbox;
    }

    private void loadTransactions() {
        ArrayList<String[]> transactionsData = dbmanager.getUserTransactions(userId);
        for (String[] data : transactionsData) {
            transactionsList.add(new Transaction(data[0], Double.parseDouble(data[1]), data[2], data[3]));
        }
    }

    private void configureThemes() {
        lightTheme = ThemeManager.getLIGHTHEME();
        darkTheme = ThemeManager.getDARKTHEME();
        ThemeManager.currentThemeProperty().addListener((obs, oldVal, newVal) -> applyCurrentTheme());
    }

    private void applyCurrentTheme() {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(ThemeManager.getCurrentTheme().equals("light") ? lightTheme : darkTheme);
    }

    private void handleCloseEvent(WindowEvent event) {//cierra ambas ventanas si se cierra la principal
        if (detallesStage != null && detallesStage.isShowing()) detallesStage.close();
        if (ajustesStage != null && ajustesStage.isShowing()) ajustesStage.close();
    }

    private void assignEventHandlers() {
        botonIngreso.setOnAction(e -> registrarTransaccion("+"));
        botonGasto.setOnAction(e -> registrarTransaccion("-"));
        botonUndo.setOnAction(e -> deshacerTransaccion());
        botonDetalles.setOnAction(e -> toggleDetailsWindow());
        CurrencyManager.currentCurrencyProperty().addListener((obs, oldVal, newVal) -> updateCurrency());
    }

    private void registrarTransaccion(String tipo) {
        try {
            cantidad = Double.parseDouble(textFieldCantidad.getText().replace(",", "."));
            double amountInEuros = cantidad / CurrencyManager.getExchangeRate(CurrencyManager.getCurrentCurrency());
            String concepto = textFieldConcepto.getText();
            String timestamp = obtenerTimestamp();
            transactionsList.add(new Transaction(tipo, amountInEuros, concepto, timestamp));
            dbmanager.insertTransaction(userId, tipo, amountInEuros, timestamp, concepto);
            labelRegistros.setText(resources.getString("main.records1") + ":  " + tipo + cantidad);
            textFieldCantidad.clear();
            textFieldConcepto.clear();
            updateCurrency();
        } catch (NumberFormatException ex) {
            labelRegistros.setText(resources.getString("main.error"));
        }
    }

    private void deshacerTransaccion() {
        if (!transactionsList.isEmpty()) {
            MainBankWindow.Transaction lastTransaction = transactionsList.get(transactionsList.size() - 1);

            double exchangeRate = CurrencyManager.getExchangeRate(CurrencyManager.getCurrentCurrency());
            double amountInSelectedCurrency = lastTransaction.amountInEuros * exchangeRate;
            String currencySymbol = CurrencyManager.getCurrencySymbol(CurrencyManager.getCurrentCurrency());

            labelRegistros.setText(resources.getString("main.undo1") + ": " + lastTransaction.type + " " + dfTransaction.format(amountInSelectedCurrency) + " " + currencySymbol);

            transactionsList.remove(transactionsList.size() - 1);
            dbmanager.deleteLastTransaction(userId);

            updateCurrency();
        }
    }

    public void updateCurrency() {
        updateDecimalFormats();
        updateTransactionList();
        updateTotalLabel();
    }

    private void updateDecimalFormats() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(LanguageManager.getLocale());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        symbols.setCurrencySymbol(CurrencyManager.getCurrencySymbol(CurrencyManager.getCurrentCurrency()));
        dfTotal = new DecimalFormat("#,##0.00 ¤", symbols);
        dfTransaction = new DecimalFormat("#,##0.00", symbols);
    }

    private void updateTransactionList() {
        StringBuilder historialContent = new StringBuilder("           HISTORIAL\n-------------------------------\n");
        StringBuilder fechaHoraContent = new StringBuilder("\n\n");
        double exchangeRate = CurrencyManager.getExchangeRate(CurrencyManager.getCurrentCurrency());
        totalInicial = 0.0;

        for (Transaction t : transactionsList) {
            double amount = t.amountInEuros * exchangeRate;
            String formattedAmount = dfTransaction.format(amount);
            String concept = t.concept.length() > 25 ? t.concept.substring(0, 25) : t.concept;
            historialContent.append(String.format("  %-10s %-25s\n", t.type + " " + formattedAmount, concept));
            fechaHoraContent.append(t.timestamp).append("\n");
            totalInicial += t.type.equals("+") ? amount : -amount;
        }

        historialArea.setText(historialContent.toString());
        fechaHoraArea.setText(fechaHoraContent.toString());
        Platform.runLater(() -> {
            ((ScrollPane) historialArea.lookup(".scroll-pane")).setVvalue(1.0);
            ((ScrollPane) fechaHoraArea.lookup(".scroll-pane")).setVvalue(1.0);
        });
    }

    private void updateTotalLabel() {
        labelTotal.setText(resources.getString("main.total") + ":  " + dfTotal.format(totalInicial));
    }

    private void toggleTheme(Button botonTema) {
        if (ThemeManager.getCurrentTheme().equals("light")) {
            ThemeManager.setCurrentTheme("dark");
            botonTema.setGraphic(createImageView("/images/sun.png", 20, 20));
        } else {
            ThemeManager.setCurrentTheme("light");
            botonTema.setGraphic(createImageView("/images/moon.png", 20, 20));
        }
    }

    private void toggleContextMenu(ContextMenu contextMenu, Button botonAjustes) {
        if (!contextMenu.isShowing()) {
            contextMenu.show(botonAjustes, javafx.geometry.Side.BOTTOM, 0, 0);
        } else {
            contextMenu.hide();
        }
    }

    private void openSubWindow(Stage subWindow) {
        if (ajustesStage == null || !ajustesStage.isShowing()) {
            ajustesStage = subWindow;
            ajustesStage.show();
        } else {
            ajustesStage.close();
        }
    }

    private void toggleDetailsWindow() {
        if (detallesStage == null || !detallesStage.isShowing()) {
            detallesStage = new DetailsWindow(userId, totalInicial, dbmanager, dfTotal).getStage();
            detallesStage.show();
        } else {
            detallesStage.close();
        }
    }

    private void disableTextAreaInteraction(TextArea textArea) { //esto gestiona que el textArea no se pueda desplazar ni vertical ni horizontalmente
        Platform.runLater(() -> {
            ScrollPane scrollPane = (ScrollPane) textArea.lookup(".scroll-pane");
            if (scrollPane != null) {
                scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> scrollPane.setVvalue(1.0));
                scrollPane.addEventFilter(ScrollEvent.ANY, event -> event.consume());
                scrollPane.getContent().addEventFilter(ScrollEvent.ANY, event -> event.consume());
            }
            textArea.addEventFilter(MouseEvent.ANY, event -> event.consume());
        });
    }

    private String obtenerTimestamp() {
        return java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd"));
    }

    public void updateTexts() {
        resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());
        labelRegistros.setText(resources.getString("main.records"));
        botonIngreso.setText(resources.getString("main.income"));
        botonGasto.setText(resources.getString("main.expense"));
        botonUndo.setText(resources.getString("main.undo"));
        botonDetalles.setText(resources.getString("main.details"));
        textFieldCantidad.setPromptText(resources.getString("main.amount"));
        textFieldConcepto.setPromptText(resources.getString("main.concept"));
        primaryStage.setTitle(resources.getString("main.title"));
        menu1.setText(resources.getString("main.config"));
        menu2.setText(resources.getString("main.account"));
        menu3.setText(resources.getString("main.help"));
        updateTotalLabel();
    }

    private ImageView createImageView(String path, int width, int height) {//gestiona la importación de imágenes
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(path)));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }

    private Button createThemeButton() {
        Button botonTema = new Button();
        botonTema.setMinSize(30, 30);
        botonTema.setFocusTraversable(false);
        botonTema.setGraphic(ThemeManager.getCurrentTheme().equals("light") ? createImageView("/images/moon.png", 20, 20) : createImageView("/images/sun.png", 20, 20));
        botonTema.setOnAction(e -> toggleTheme(botonTema));
        return botonTema;
    }

    private Button createSettingsButton() {
        Button botonAjustes = new Button();
        botonAjustes.setMinSize(30, 30);
        botonAjustes.setFocusTraversable(false);
        botonAjustes.setGraphic(createImageView("/images/settings.png", 20, 20));

        ContextMenu contextMenu = new ContextMenu();
        menu1 = new MenuItem(resources.getString("main.config"));
        menu2 = new MenuItem(resources.getString("main.account"));
        menu3 = new MenuItem(resources.getString("main.help"));
        contextMenu.getItems().addAll(menu1, menu2, menu3);
        botonAjustes.setOnAction(e -> toggleContextMenu(contextMenu, botonAjustes));

        menu1.setOnAction(e -> openSubWindow(new ConfigurationSubWindow(this).getStage()));
        menu2.setOnAction(e -> openSubWindow(new AccountSubWindow(dbmanager, userId).getStage()));
        menu3.setOnAction(e -> openSubWindow(new HelpSubWindow().getStage()));
        return botonAjustes;
    }

    private Region createSpacer() {//separador para mejor gestión de la disposición de los elementos
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private TextArea createTextArea(String text, int width, int height) {
        TextArea textArea = new TextArea();
        textArea.setPrefSize(width, height);
        textArea.setText(text);
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        textArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        return textArea;
    }

    //clase interna para gestionar las actualizaciones de las transacciones desde la clase principal
    public static class Transaction {
        private String type;
        private double amountInEuros;
        private String concept;
        private String timestamp;

        public Transaction(String type, double amountInEuros, String concept, String timestamp) {
            this.type = type;
            this.amountInEuros = amountInEuros;
            this.concept = concept;
            this.timestamp = timestamp;
        }

        public String getType() {
            return type;
        }

        public double getAmountInEuros() {
            return amountInEuros;
        }

        public String getConcept() {
            return concept;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

}
