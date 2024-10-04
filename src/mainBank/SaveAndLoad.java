package mainBank;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.*;
import java.util.ArrayList;

public class SaveAndLoad {
    File file;

    public SaveAndLoad() {
        try {
            file = new File("src/mainBank/files/transactions.txt");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al crear el archivo: " + e.getMessage());
        }
    }

    public void guardarCambios(ArrayList<String> historial, Label totalLabel) {
        try (PrintWriter pw = new PrintWriter(file)) {
            for (String transaction : historial) {
                pw.println(transaction);
            }
            pw.println("TOTAL:" + totalLabel.getText().replace("TOTAL:  ", ""));
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar los cambios: " + e.getMessage());
        }
    }

    public void cargarDatos(TextArea ta, Label totalLabel, ArrayList<String> historial) {
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            double ultimoValorTotal = 0;

            ta.setText("Historial:\n--------\n");
            historial.clear();

            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("TOTAL:")) {

                    String valorTotal = linea.replace("TOTAL:", "").trim();
                    if (!valorTotal.isEmpty()) {
                        ultimoValorTotal = Double.parseDouble(valorTotal.replace(",", "."));
                    }
                } else {
                    historial.add(linea);
                    ta.appendText(linea + "\n");
                }
            }

            if (ultimoValorTotal != 0.0) {
                totalLabel.setText("TOTAL:  " + ultimoValorTotal);
            } else {
                totalLabel.setText("TOTAL:  0.00");
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al leer los datos: " + e.getMessage());
        } catch (NumberFormatException e) {
            totalLabel.setText("TOTAL:  0.00");
        }
    }
}
