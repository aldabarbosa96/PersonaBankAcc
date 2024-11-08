package mainBank.windows;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainBank.managers.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.Map;

public class DetailsWindow {
    private Stage stage;
    private DataBaseManager dbmanager;
    private int userId;
    private double total;
    private DecimalFormat df;
    private ResourceBundle resources;
    private TextArea areaDetalles;
    private ArrayList<MainBankWindow.Transaction> transactionsList = new ArrayList<>();
    private Button btnShowChart;
    private VBox detallesLayout;
    private VBox graficoLayout;

    private LineChart<String, Number> lineChart;

    private XYChart.Series<String, Number> series;

    public DetailsWindow(int userId, double total, DataBaseManager dbmanager, DecimalFormat df) {
        this.userId = userId;
        this.total = total;
        this.dbmanager = dbmanager;
        this.df = df;
        this.stage = new Stage();
        this.resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());

        //guardamos la lista de transacciones
        ArrayList<String[]> transactionsData = dbmanager.getUserTransactions(userId);

        for (String[] transactionData : transactionsData) {
            String tipo = transactionData[0];
            double amountInEuros = Double.parseDouble(transactionData[1]);
            String concept = transactionData[2];
            String timestamp = transactionData[3];

            MainBankWindow.Transaction transaction = new MainBankWindow.Transaction(tipo, amountInEuros, concept, timestamp);
            transactionsList.add(transaction);
        }

        createWindow();

        //listener para cambio de divisa
        CurrencyManager.currentCurrencyProperty().addListener((observable, oldValue, newValue) -> {
            updateCurrency();
        });
    }

    private void createWindow() {
        String lightTheme = ThemeManager.getLIGHTHEME();
        String darkTheme = ThemeManager.getDARKTHEME();
        stage.setTitle(resources.getString("details.title"));

        areaDetalles = new TextArea();
        areaDetalles.setEditable(false);
        areaDetalles.getStyleClass().addAll("custom-text-area", "custom-font");
        areaDetalles.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");

        btnShowChart = new Button(resources.getString("details.showCart"));
        btnShowChart.setMinSize(100, 33);
        btnShowChart.setPadding(new Insets(20));
        btnShowChart.setOnAction(e -> toggleView());

        detallesLayout = new VBox(10, areaDetalles);
        detallesLayout.setPadding(new Insets(10));
        VBox.setVgrow(areaDetalles, Priority.ALWAYS);

        graficoLayout = new VBox();
        graficoLayout.setPadding(new Insets(20));

        //usamos borderPane para crear dos layouts
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setCenter(detallesLayout); //mostramos detalles por defecto
        BorderPane.setMargin(btnShowChart, new Insets(10, 0, 0, 0));
        mainLayout.setBottom(btnShowChart);

        Scene scene = new Scene(mainLayout, 625, 700);

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

        initializeLineChart();
        updateCurrency();
    }

    private void toggleView() { //alternar la vista entre detalles y gráfico
        BorderPane mainLayout = (BorderPane) stage.getScene().getRoot();
        if (mainLayout.getCenter() == detallesLayout) {
            mainLayout.setCenter(graficoLayout);
            btnShowChart.setText(resources.getString("details.details"));
        } else {
            mainLayout.setCenter(detallesLayout);
            btnShowChart.setText(resources.getString("details.showCart"));
        }
    }

    private void initializeLineChart() {
        //conf. ejes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(resources.getString("details.date"));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(resources.getString("details.capital"));

        //confg. gráfico
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(resources.getString("details.capitalProgress"));
        lineChart.setMaxWidth(Double.MAX_VALUE);
        lineChart.setMaxHeight(Double.MAX_VALUE);

        series = new XYChart.Series<>();
        series.setName(resources.getString("details.dailyTotal"));


        lineChart.getData().add(series); //añadimos serie de datos al gráfico

        loadChartData();

        graficoLayout.getChildren().add(lineChart);
        VBox.setVgrow(lineChart, Priority.ALWAYS);
    }

    private TreeMap<LocalDate, Double> calculateDailyBalances() { //calcular el balance diario
        TreeMap<LocalDate, Double> dailyBalances = new TreeMap<>();
        double runningTotal = 0.0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (MainBankWindow.Transaction transaction : transactionsList) {
            String timestamp = transaction.getTimestamp();
            String dateString = timestamp.substring(9);
            LocalDate date = LocalDate.parse(dateString, formatter);

            double amount = transaction.getAmountInEuros();
            if (transaction.getType().equals("+")) {
                runningTotal += amount;
            } else if (transaction.getType().equals("-")) {
                runningTotal -= amount;
            }
            dailyBalances.put(date, runningTotal);
        }

        return dailyBalances;
    }

    private void loadChartData() {
        series.getData().clear();
        TreeMap<LocalDate, Double> dailyBalances = calculateDailyBalances();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Map.Entry<LocalDate, Double> entry : dailyBalances.entrySet()) {
            String date = entry.getKey().format(formatter);
            Double balance = entry.getValue() * CurrencyManager.getExchangeRate(CurrencyManager.getCurrentCurrency());
            series.getData().add(new XYChart.Data<>(date, balance));
        }
    }

    public void updateCurrency() {
        updateDecimalFormat();
        updateDetailsArea();
        updateChartData();
    }

    private void updateDecimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(LanguageManager.getLocale());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        df = new DecimalFormat("#,##0.00", symbols);
    }

    private void updateDetailsArea() {
        areaDetalles.clear();

        String header = String.format("%-12s %-20s %-10s %-12s %-8s\n",
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
            String tipo = transaction.getType();
            double amountInEuros = transaction.getAmountInEuros();
            String concept = transaction.getConcept();
            String timestamp = transaction.getTimestamp();

            double amountInSelectedCurrency = amountInEuros * exchangeRate;
            String formattedAmount = df.format(amountInSelectedCurrency);

            if (tipo.equals("+")) {
                runningTotal += amountInSelectedCurrency;
            } else if (tipo.equals("-")) {
                runningTotal -= amountInSelectedCurrency;
            }

            String totalFormateado = df.format(runningTotal);

            String hora = timestamp.substring(0, 8);
            String fecha = timestamp.substring(9);

            String linea = String.format("%-12s %-20s %-10s %-12s %-8s %-1s\n", //lo que me puto ha cosatado alinearlo bien no tiene sentido
                    tipo + formattedAmount, concept, hora, fecha, totalFormateado,currencySymbol);

            areaDetalles.appendText(linea);
        }
    }

    private void updateChartData() {
        loadChartData();
    }

    public Stage getStage() {
        return stage;
    }
}
