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

        Button botonIngreso = new Button("Registrar ingreso");
        Button botonGasto = new Button("Registrar gasto");
        Button botonUndo = new Button("Deshacer ⎌");
        botonIngreso.setMinWidth(125.0);
        botonGasto.setMinWidth(125.0);

        HBox hboxBotones = new HBox(10, botonIngreso, botonGasto);
        hboxBotones.setSpacing(10);

        SaveAndLoad sl = new SaveAndLoad();
        sl.cargarDatos(historialArea, label2, historial);

        totalInicial = Double.parseDouble(label2.getText().replace("TOTAL:  ", "").replace(",", "."));

        ButtonActions buttonActions = new ButtonActions(totalInicial, historial, label1, label2, historialArea, df);

        botonIngreso.setOnAction(e -> buttonActions.registrarIngreso(textField));
        botonGasto.setOnAction(e -> buttonActions.registrarGasto(textField));
        botonUndo.setOnAction(e -> buttonActions.deshacer());

        VBox vbox = new VBox(10, label, textField, hboxBotones, label1, historialArea, label2, botonUndo);
        vbox.setPadding(new Insets(20));

        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            sl.guardarCambios(historial, label2);
        });

        Scene scene = new Scene(vbox, 300, 550);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PersonalBankAccount");
        primaryStage.show();
    }
}
