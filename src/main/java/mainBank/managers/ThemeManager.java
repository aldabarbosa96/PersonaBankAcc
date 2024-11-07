package mainBank.managers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

/**
 * Clase que gestiona las propiedades de los temas.
 */
public class ThemeManager {
    private static final String LIGHTHEME = Objects.requireNonNull(ThemeManager.class.getResource("/cssThemes/light-theme.css")).toExternalForm();
    private static final String DARKTHEME = Objects.requireNonNull(ThemeManager.class.getResource("/cssThemes/dark-theme.css")).toExternalForm();
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

    public static String getLIGHTHEME() {
        return LIGHTHEME;
    }

    public static String getDARKTHEME() {
        return DARKTHEME;
    }
}
