package mainBank.managers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.*;
import java.util.Locale;
import java.util.Properties;

public class LanguageManager {
    private static final ObjectProperty<Locale> currentLocale = new SimpleObjectProperty<>(new Locale("es"));

    public static void setLocale(Locale locale) {
        currentLocale.set(locale);
        saveLocalePreference();
    }

    public static Locale getLocale() {
        return currentLocale.get();
    }

    public static ObjectProperty<Locale> localeProperty() {
        return currentLocale;
    }

    private static void saveLocalePreference() {
        try (FileOutputStream out = new FileOutputStream("config.properties")) {
            Properties props = new Properties();
            props.setProperty("language", currentLocale.get().getLanguage());
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadLocalePreference() {
        try (FileInputStream in = new FileInputStream("config.properties")) {
            Properties props = new Properties();
            props.load(in);
            String language = props.getProperty("language", "es");
            currentLocale.set(new Locale(language));
        } catch (IOException e) {
            // Usaremos el idioma por defecto si no existe el archivo
        }
    }
}
