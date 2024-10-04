package mainBank;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainBank extends Application {
    private ArrayList<String> historial = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("0.00");
    private double totalInicial = 0.0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Introduzca cantidad");
        Label label1 = new Label("Ingresos/Gastos registrados: ");
        Label label2 = new Label("TOTAL: ");

        TextField textField = new TextField("00.00");

        TextArea historialArea = new TextArea();
        historialArea.setPrefSize(250, 320);
        historialArea.setText("Historial:\n--------\n");
        historialArea.setEditable(false);

        TextArea fechaHoraArea = new TextArea();
        fechaHoraArea.setPrefSize(200, 320);
        fechaHoraArea.setEditable(false);

        Button botonIngreso = new Button("Registrar ingreso");
        Button botonGasto = new Button("Registrar gasto");
        Button botonUndo = new Button("Deshacer ⎌");
        botonIngreso.setMinWidth(145);
        botonGasto.setMinWidth(145);

        HBox hboxBotones = new HBox(10, botonIngreso, botonGasto);
        hboxBotones.setSpacing(10);

        HBox hboxHistorial = new HBox(0, historialArea, fechaHoraArea);

        ArrayList<String> fechasHoras = new ArrayList<>();

        SaveAndLoad sl = new SaveAndLoad();
        sl.cargarDatos(historialArea, fechaHoraArea, label2, historial, fechasHoras);

        totalInicial = Double.parseDouble(label2.getText().replace("TOTAL:  ", "").replace(",", "."));

        ButtonActions buttonActions = new ButtonActions(totalInicial, historial, fechasHoras, label1, label2, historialArea, fechaHoraArea, df);

        botonIngreso.setOnAction(e -> buttonActions.registrarIngreso(textField));
        botonGasto.setOnAction(e -> buttonActions.registrarGasto(textField));
        botonUndo.setOnAction(e -> buttonActions.deshacer());

        VBox vbox = new VBox(10, label, textField, hboxBotones, label1, hboxHistorial, label2, botonUndo);
        vbox.setPadding(new Insets(20));

        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            sl.guardarCambios(historial, fechasHoras, label2);
        });

        Scene scene = new Scene(vbox, 340, 550);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PersonalBankAccount");
        primaryStage.show();
    }
}
