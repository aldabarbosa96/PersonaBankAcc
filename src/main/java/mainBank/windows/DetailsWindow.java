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
    private Button btnShowChart;
    private VBox detallesLayout;
    private VBox graficoLayout;

    private LineChart<String, Number> lineChart;
    private GraphicsManager graphicsManager;

    private XYChart.Series<String, Number> series;

    public DetailsWindow(int userId, double total, DataBaseManager dbmanager, DecimalFormat df) {
        this.userId = userId;
        this.total = total;
        this.dbmanager = dbmanager;
        this.df = df;
        this.stage = new Stage();
        this.resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());
        this.graphicsManager = new GraphicsManager(dbmanager, userId);

        // Cargar transacciones
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

        // Añadir listener para cambios de divisa
        CurrencyManager.currentCurrencyProperty().addListener((observable, oldValue, newValue) -> {
            updateCurrency();
        });
    }

    private void createWindow() {
        String lightTheme = ThemeManager.getLIGHTHEME();
        String darkTheme = ThemeManager.getDARKTHEME();
        stage.setTitle(resources.getString("details.title"));

        // Inicializar el área de detalles
        areaDetalles = new TextArea();
        areaDetalles.setEditable(false);
        areaDetalles.getStyleClass().addAll("custom-text-area", "custom-font");
        areaDetalles.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");
        areaDetalles.setMaxWidth(Double.MAX_VALUE);
        areaDetalles.setMaxHeight(Double.MAX_VALUE);
        // Eliminar el setPrefSize para evitar conflictos
        // areaDetalles.setPrefSize(800,600);

        // Crear el botón "Mostrar Gráfico"
        btnShowChart = new Button(resources.getString("details.showCart"));
        btnShowChart.setMinSize(100, 33);
        btnShowChart.setPadding(new Insets(10, 20, 10, 20));
        btnShowChart.setOnAction(e -> toggleView());

        // Configurar el layout de detalles
        detallesLayout = new VBox(10, areaDetalles);
        detallesLayout.setPadding(new Insets(10));
        VBox.setVgrow(areaDetalles, Priority.ALWAYS); // Permitir que el TextArea se expanda

        // Inicializar el layout del gráfico (vacío por ahora)
        graficoLayout = new VBox();
        graficoLayout.setPadding(new Insets(20));
        // Eliminar la línea de visibilidad para que el graficoLayout sea visible cuando se establezca como centro
        // graficoLayout.setVisible(false); // Oculto inicialmente
        graficoLayout.setMaxWidth(Double.MAX_VALUE);
        graficoLayout.setMaxHeight(Double.MAX_VALUE);

        // Crear el contenedor principal usando BorderPane
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setCenter(detallesLayout); // Por defecto, mostrar detalles

        // Colocar el botón en la parte inferior
        BorderPane.setMargin(btnShowChart, new Insets(10, 0, 0, 0)); // Margen superior para separación
        mainLayout.setBottom(btnShowChart);

        Scene scene = new Scene(mainLayout, 620, 650);

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

    private void toggleView() {
        BorderPane mainLayout = (BorderPane) stage.getScene().getRoot();
        if (mainLayout.getCenter() == detallesLayout) {
            // Mostrar gráfico
            mainLayout.setCenter(graficoLayout);
            btnShowChart.setText(resources.getString("details.details"));
        } else {
            // Mostrar detalles
            mainLayout.setCenter(detallesLayout);
            btnShowChart.setText(resources.getString("details.showCart"));
        }
    }

    private void initializeLineChart() {
        // Configurar los ejes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(resources.getString("details.date")); // Etiqueta del eje X

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(resources.getString("details.capital")); // Etiqueta del eje Y

        // Crear el LineChart
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(resources.getString("details.capitalProgress")); // Título del gráfico
        lineChart.setMaxWidth(Double.MAX_VALUE);
        lineChart.setMaxHeight(Double.MAX_VALUE);

        // Crear la serie de datos
        series = new XYChart.Series<>();
        series.setName(resources.getString("details.dailyTotal")); // Nombre de la serie

        // Añadir la serie al gráfico
        lineChart.getData().add(series);

        // Cargar los datos en el gráfico
        loadChartData();

        // Añadir el gráfico al layout del gráfico
        graficoLayout.getChildren().add(lineChart);
        VBox.setVgrow(lineChart, Priority.ALWAYS); // Permitir que el LineChart se expanda
    }

    private void loadChartData() {
        series.getData().clear();
        // Obtener los totales diarios desde GraphicsManager
        var dailyTotals = graphicsManager.getDailyTotals();
        for (var entry : dailyTotals.entrySet()) {
            String date = entry.getKey().toString(); // Convertir LocalDate a String
            Double total = entry.getValue();
            series.getData().add(new XYChart.Data<>(date, total));
        }
    }

    public void updateCurrency() {
        updateDecimalFormat();
        updateDetailsArea();
        updateChartData(); // Actualizar los datos del gráfico cuando cambie la divisa
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

            String totalFormateado = df.format(runningTotal) + " " + currencySymbol;

            String hora = timestamp.substring(0, 8);
            String fecha = timestamp.substring(9);

            String linea = String.format("%-12s %-20s %-10s %-12s %-15s\n",
                    tipo + formattedAmount, concept, hora, fecha, totalFormateado);

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
