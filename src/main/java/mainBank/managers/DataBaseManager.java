package mainBank.managers;

import mainBank.windows.MainBankWindow;

import java.io.File;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {
    private Connection connection;
    private DecimalFormat df;

    public void setDecimalFormat(DecimalFormat df) {
        this.df = df;
    }

    public void dbconnect() {
        try {
            String userHome = System.getProperty("user.home");
            String dbPath = userHome + File.separator + "PersonalBank.db";

            String url = "jdbc:sqlite:" + dbPath;
            System.out.println("Conectando a la base de datos en: " + dbPath);
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dbdisconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Desconectado de la base de datos");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTable() {
        //tabla usuarios
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL);";
        //tabla transacciones
        String sqlTransactions = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "transaction_type TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "timestamp TEXT NOT NULL," +
                "concept TEXT," +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            statement.execute(sqlTransactions);
            System.out.println("Tablas creadas o ya existentes");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean insertUser(String username, String password) {
        String sqlCheck = "SELECT * FROM users WHERE username = ?";

        if (!userIsValid(username)) {
            System.out.println("Nombre de usuario inválido. Solo se permiten letras, números, guiones bajos y puntos.");
            return false;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCheck)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                System.out.println("El usuario ya existe");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar usuario: " + e.getMessage());
            return false;
        }

        String sqlInsert = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            System.out.println("Usuario registrado con éxito");
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean userIsValid(String username) { //filtro para evitar SQLI
        String regex = "[a-zA-Z0-9_.]+$";
        return username.matches(regex);

    }

    public int verifyUser(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return -1;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public void insertTransaction(int userId, String transactionType, double amount, String timestamp, String concept) {
        String sql = "INSERT INTO transactions(user_id, transaction_type, amount, timestamp, concept) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, transactionType);
            preparedStatement.setDouble(3, amount);
            preparedStatement.setString(4, timestamp);
            preparedStatement.setString(5, concept);
            preparedStatement.executeUpdate();
            System.out.println("Transacción registrada con éxito");
        } catch (SQLException e) {
            System.out.println("Error al registrar transacción: " + e.getMessage());
        }
    }

    public ArrayList<String[]> getUserTransactions(int userId) {
        String sql = "SELECT transaction_type, amount, timestamp, concept FROM transactions WHERE user_id = ?";
        ArrayList<String[]> transactions = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String transactionType = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");
                String timestamp = rs.getString("timestamp");
                String concept = rs.getString("concept");

                transactions.add(new String[]{transactionType, String.valueOf(amount), concept, timestamp});
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener transacciones: " + e.getMessage());
        }

        return transactions;
    }

    public void deleteLastTransaction(int userId) {
        String sql = "DELETE FROM transactions WHERE id = (SELECT MAX(id) FROM transactions WHERE user_id = ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Última transacción eliminada con éxito.");
            } else {
                System.out.println("No se pudo eliminar la última transacción.");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar la última transacción: " + e.getMessage());
        }
    }

    public Connection getConnection(){
        return connection;
    }
}