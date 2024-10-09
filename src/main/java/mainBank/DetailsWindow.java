package mainBank;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class DetailsWindow extends Application {
    DataBaseManager dbmanager = new DataBaseManager();
    private int userId;
    private double total;
    MainBankWindow mainBankWindow = new MainBankWindow(dbmanager, userId);

    public DetailsWindow(int userId, double total) {
        this.userId = userId;
        this.total = total;
    }

    @Override
    public void start(Stage stage) {
        dbmanager.dbconnect();
        dbmanager.createTable();

        ArrayList<String[]> transactions = dbmanager.getUserTransactions(userId);

        TextArea areaDetalles = new TextArea();
        areaDetalles.setPrefSize(400, 590);
        areaDetalles.setEditable(false);
        areaDetalles.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        areaDetalles.setText("   Cantidad    Concepto                 Hora     Fecha      Total\n\n");//manejar esto con un HBox en un futuro

        for (String[] transaction : transactions) {
            String formattedLine = transaction[0];
            areaDetalles.appendText(formattedLine);
            areaDetalles.appendText(transaction[1] + "  " +/*+ total + */" €"+"\n");
        }

        for (int i = transactions.size();i<=0;i--){
            double totalParcial = total + transactions.get(i).length;
            /*seguir aquí: habría que crear una variable en ButtonActionManager que almacene el total en cada transacción
            para luego poder trabajar aquí con ese valor*/
        }

        VBox vbox = new VBox(areaDetalles);
        vbox.setPadding(new Insets(20));
        Scene scene = new Scene(vbox, 800, 400);
        stage.setScene(scene);
        stage.setTitle("Detalles");
        stage.show();

        stage.setOnCloseRequest(event -> dbmanager.dbdisconnect());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
