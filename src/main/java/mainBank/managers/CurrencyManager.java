package mainBank.managers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class CurrencyManager {
    private static StringProperty currentCurrency = new SimpleStringProperty("EUR");
    private static Map<String, Double> exchangeRates = new HashMap<>();

    static {
        //se deberá ir actualizando según la varición de la moneda
        exchangeRates.put("EUR", 1.0);
        exchangeRates.put("USD", 1.09);
        exchangeRates.put("GBP", 0.84);
    }

    public static String getCurrentCurrency() {
        return currentCurrency.get();
    }

    public static void setCurrentCurrency(String currencyCode) {
        currentCurrency.set(currencyCode);
    }

    public static StringProperty currentCurrencyProperty() {
        return currentCurrency;
    }

    public static double getExchangeRate(String currencyCode) {
        return exchangeRates.getOrDefault(currencyCode, 1.0);
    }

    public static String getCurrencySymbol(String currencyCode) {
        switch (currencyCode) {
            case "EUR":
                return "€";
            case "USD":
                return "$";
            case "GBP":
                return "£";
            default:
                return currencyCode;
        }
    }
}
