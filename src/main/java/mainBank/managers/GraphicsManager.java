package mainBank.managers;

import mainBank.windows.MainBankWindow.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GraphicsManager {
    private DataBaseManager dataBaseManager;
    private int id;

    public GraphicsManager(DataBaseManager dataBaseManager, int id) {
        this.dataBaseManager = dataBaseManager;
        this.id = id;
    }

    public Map<LocalDate, Double> getDailyTotals() {
        List<Transaction> transactions = dataBaseManager.getUserTransactionsObjects(id);
        Map<LocalDate, Double> dailyTotals = new TreeMap<>();

        double runningTotal = 0.0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Transaction transaction : transactions) {
            // Extraer la fecha de la marca de tiempo
            String dateString = transaction.getTimestamp().split(" ")[1];
            LocalDate date = LocalDate.parse(dateString, formatter);

            // Actualizar el total acumulado
            if (transaction.getType().equals("+")) {
                runningTotal += transaction.getAmountInEuros();
            } else if (transaction.getType().equals("-")) {
                runningTotal -= transaction.getAmountInEuros();
            }

            // Asignar el total acumulado a la fecha correspondiente
            dailyTotals.put(date, runningTotal);
        }

        return dailyTotals;
    }
    public void printDailyTotals() {
        Map<LocalDate, Double> dailyTotals = getDailyTotals();
        for (Map.Entry<LocalDate, Double> entry : dailyTotals.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

}
