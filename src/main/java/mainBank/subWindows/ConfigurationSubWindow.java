package mainBank.subWindows;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainBank.managers.CurrencyManager;
import mainBank.managers.LanguageManager;
import mainBank.windows.MainBankWindow;
import mainBank.managers.ThemeManager;

import java.util.Locale;
import java.util.ResourceBundle;

public class ConfigurationSubWindow {
    private Stage stage;
    private MainBankWindow mainBankWindow;
    private ResourceBundle resources;
    private ComboBox<String> languageComboBox;
    private Label languageLabel;

    private ComboBox<String> currencyComboBox;
    private Label currencyLabel;

    public ConfigurationSubWindow(MainBankWindow mainBankWindow) {
        this.stage = new Stage();
        this.mainBankWindow = mainBankWindow;
        resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());
        createWindow();
    }

    private void createWindow() {
        stage.setTitle(resources.getString("main.config"));

        //configuración selector de idioma
        languageLabel = new Label(resources.getString("config.language"));
        languageLabel.getStyleClass().add("custom-label");
        languageLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");
        languageComboBox = new ComboBox<>();
        languageComboBox.setMaxWidth(75);
        languageComboBox.getItems().addAll("es", "en", "ca");

        languageComboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String languageCode, boolean empty) {
                super.updateItem(languageCode, empty);
                if (empty || languageCode == null) {
                    setText(null);
                } else {
                    setText(getDisplayNameForLanguageCode(languageCode));
                }
            }
        });
        languageComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String languageCode, boolean empty) {
                super.updateItem(languageCode, empty);
                if (empty || languageCode == null) {
                    setText(null);
                } else {
                    setText(getDisplayNameForLanguageCode(languageCode));
                }
            }
        });

        languageComboBox.setValue(LanguageManager.getLocale().getLanguage());

        languageComboBox.setOnAction(e -> {
            String selectedLanguageCode = languageComboBox.getValue();
            LanguageManager.setLocale(new Locale(selectedLanguageCode));
            resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());
            mainBankWindow.updateTexts();
            updateTexts();
        });

        //configuración selector divisa
        currencyLabel = new Label(resources.getString("config.currency"));
        currencyLabel.getStyleClass().add("custom-label");
        currencyLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");

        currencyComboBox = new ComboBox<>();
        currencyComboBox.setMaxWidth(75);
        currencyComboBox.getItems().addAll("EUR", "USD", "GBP");

        currencyComboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String currencyCode, boolean empty) {
                super.updateItem(currencyCode, empty);
                if (empty || currencyCode == null) {
                    setText(null);
                } else {
                    setText(getDisplayNameForCurrencyCode(currencyCode));
                }
            }
        });
        currencyComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String currencyCode, boolean empty) {
                super.updateItem(currencyCode, empty);
                if (empty || currencyCode == null) {
                    setText(null);
                } else {
                    setText(getDisplayNameForCurrencyCode(currencyCode));
                }
            }
        });

        currencyComboBox.setValue(CurrencyManager.getCurrentCurrency());

        currencyComboBox.setOnAction(e -> {
            String selectedCurrencyCode = currencyComboBox.getValue();
            CurrencyManager.setCurrentCurrency(selectedCurrencyCode);
            mainBankWindow.updateCurrency();
            updateTexts();
        });

        VBox vBox = new VBox(10, languageLabel, languageComboBox, currencyLabel, currencyComboBox);
        vBox.setPadding(new Insets(20));

        Scene scene = new Scene(vBox, 240, 220);

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
    }

    private String getDisplayNameForCurrencyCode(String currencyCode) {
        switch (currencyCode) {
            case "EUR":
                return resources.getString("config.euro");
            case "USD":
                return resources.getString("config.dollar");
            case "GBP":
                return resources.getString("config.pound");
            default:
                return currencyCode;
        }
    }

    private String getDisplayNameForLanguageCode(String languageCode) {
        switch (languageCode) {
            case "es":
                return resources.getString("config.spanish");
            case "en":
                return resources.getString("config.english");
            case "ca":
                return resources.getString("config.catalan");
            default:
                return languageCode;
        }
    }

    private void updateTexts() {
        resources = ResourceBundle.getBundle("i18n.Messages", LanguageManager.getLocale());
        stage.setTitle(resources.getString("main.config"));
        languageLabel.setText(resources.getString("config.language"));
        currencyLabel.setText(resources.getString("config.currency"));

        languageComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String languageCode, boolean empty) {
                super.updateItem(languageCode, empty);
                if (empty || languageCode == null) {
                    setText(null);
                } else {
                    setText(getDisplayNameForLanguageCode(languageCode));
                }
            }
        });
        languageComboBox.setValue(LanguageManager.getLocale().getLanguage());

        currencyComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String currencyCode, boolean empty) {
                super.updateItem(currencyCode, empty);
                if (empty || currencyCode == null) {
                    setText(null);
                } else {
                    setText(getDisplayNameForCurrencyCode(currencyCode));
                }
            }
        });
        currencyComboBox.setValue(CurrencyManager.getCurrentCurrency());
    }

    public Stage getStage() {
        return stage;
    }
}
