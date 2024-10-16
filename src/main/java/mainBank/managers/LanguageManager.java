package mainBank.managers;

import java.io.*;
import java.util.Locale;
import java.util.Properties;

public class LanguageManager {
    private static Locale currentLocale = new Locale("es");

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        saveLocalePreference();
    }

    public static Locale getLocale() {
        return currentLocale;
    }

    private static void saveLocalePreference() {
        try (FileOutputStream out = new FileOutputStream("config.properties")) {
            Properties props = new Properties();
            props.setProperty("language", currentLocale.getLanguage());
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
            currentLocale = new Locale(language);
        } catch (IOException e) {
            //usaremos el idioma por defecto si no existe el archivo
        }
    }
}
