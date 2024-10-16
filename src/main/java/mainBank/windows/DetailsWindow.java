package mainBank.windows;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainBank.managers.CurrencyManager;
import mainBank.managers.DataBaseManager;
import mainBank.managers.LanguageManager;
import mainBank.managers.ThemeManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DetailsWindow {
    private Stage stage;
    private DataBaseManager dbmanager;
    private int userId;
    private double total;
    private DecimalFormat df;
    private ResourceBundle resources;
    private TextArea areaDetalles;
    private ArrayList<MainBankWindow.Transaction> transactionsList = new ArrayList<>();

    public DetailsWindow(int userId, double total, DataBaseManager dbmanager, DecimalFormat df) {
        this.userId = userId;
        this.total = total;
        this.dbmanager = dbmanager;
        this.df = df;
        this.stage = new Stage();
        this.resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());

        // Cargar transacciones
        ArrayList<String[]> transactionsData = dbmanager.getUserTransactions(userId);

        for (String[] transactionData : transactionsData) {
            String tipo = transactionData[0];
            double amountInEuros = Double.parseDouble(transactionData[1]);
            String concept = transactionData[2];
            String timestamp = transactionData[3];

            MainBankWindow.Transaction transaction = new MainBankWindow(dbmanager, userId).new Transaction(tipo, amountInEuros, concept, timestamp);
            transactionsList.add(transaction);
        }

        createWindow();

        // Añadir listener para cambios de divisa
        CurrencyManager.currentCurrencyProperty().addListener((observable, oldValue, newValue) -> {
            updateCurrency();
        });
    }

    private void createWindow() {
        stage.setTitle(resources.getString("details.title"));

        areaDetalles = new TextArea();
        areaDetalles.setPrefSize(800, 540);
        areaDetalles.setEditable(false);
        areaDetalles.getStyleClass().addAll("custom-text-area", "custom-font");
        areaDetalles.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");

        VBox vbox = new VBox(areaDetalles);
        vbox.setPadding(new Insets(20));
        Scene scene = new Scene(vbox, 620, 550);

        String lightTheme = ThemeManager.getLIGHTHEME();
        String darkTheme = ThemeManager.getDARKTHEME();

        if (ThemeManager.getCurrentTheme().equals("light")) {
            scene.getStylesheets().add(lightTheme);
        } else {
            scene.getStylesheets().add(darkTheme);
        }

        ThemeManager.currentThemeProperty().addListener((observable, oldValue, newValue) -> {
            scene.getStylesheets().clear();
            if (newValue.equals("light")) {
                scene.getStylesheets().add(lightTheme);
            } else {
                scene.getStylesheets().add(darkTheme);
            }
        });

        stage.setScene(scene);
        stage.setResizable(false);

        updateCurrency();
    }

    public void updateCurrency() {
        updateDecimalFormat();
        updateDetailsArea();
    }

    private void updateDecimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(LanguageManager.getLocale());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        df = new DecimalFormat("#,##0.00", symbols);
    }

    private void updateDetailsArea() {
        areaDetalles.clear();

        String header = String.format("%-12s %-20s %-10s %-12s %-15s\n",
                resources.getString("details.amount"),
                resources.getString("details.concept"),
                resources.getString("details.time"),
                resources.getString("details.date"),
                resources.getString("details.total"));
        areaDetalles.appendText(header);
        areaDetalles.appendText("---------------------------------------------------" +
                "----------------------------------------------------------\n");

        double runningTotal = 0.0;
        double exchangeRate = CurrencyManager.getExchangeRate(CurrencyManager.getCurrentCurrency());
        String currencySymbol = CurrencyManager.getCurrencySymbol(CurrencyManager.getCurrentCurrency());

        for (MainBankWindow.Transaction transaction : transactionsList) {
            String tipo = transaction.type;
            double amountInEuros = transaction.amountInEuros;
            String concept = transaction.concept;
            String timestamp = transaction.timestamp;

            double amountInSelectedCurrency = amountInEuros * exchangeRate;
            String formattedAmount = df.format(amountInSelectedCurrency);

            if (tipo.equals("+")) {
                runningTotal += amountInSelectedCurrency;
            } else if (tipo.equals("-")) {
                runningTotal -= amountInSelectedCurrency;
            }

            String totalFormateado = df.format(runningTotal) + " " + currencySymbol;

            String hora = timestamp.substring(0, 8);
            String fecha = timestamp.substring(9);

            String linea = String.format("%-12s %-20s %-10s %-12s %-15s\n",
                    tipo + formattedAmount, concept, hora, fecha, totalFormateado);

            areaDetalles.appendText(linea);
        }
    }

    public Stage getStage() {
        return stage;
    }
}
