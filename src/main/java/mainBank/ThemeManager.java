package mainBank;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Clase que gestiona las propiedades de los temas.
 */
public class ThemeManager {
    private static final StringProperty currentTheme = new SimpleStringProperty("light");

    public static StringProperty currentThemeProperty() {
        return currentTheme;
    }

    public static String getCurrentTheme() {
        return currentTheme.get();
    }

    public static void setCurrentTheme(String theme) {
        currentTheme.set(theme);
    }
}
