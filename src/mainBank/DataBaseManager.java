package mainBank;

import java.sql.*;
import java.util.ArrayList;

public class DataBaseManager {
    private Connection connection;

    public void dbconnect() {
        try {
            String url = "jdbc:sqlite:src/mainBank/database/PersonalBank.db";
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dbdisconnect() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Desconectado de la base de datos");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTable() {
        String sql = "create table if not exists users (" +
                "id integer primary key autoincrement," +
                "username text not null unique," +
                "password text not null);";
        String sqlTransactions = "create table if not exists transactions (" +
                "id integer primary key autoincrement," +
                "user_id integer not null," +
                "transaction_type text not null," +
                "amount real not null," +
                "timestamp text not null," +
                "foreign key (user_id) references users(id))";


        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            statement.execute(sqlTransactions);
            System.out.println("Tabla creada o ya existente");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean insertUser(String username, String password) {
        String sqlCheck = "SELECT * FROM users WHERE username = ?";

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
            System.out.println("Usuario generado con éxito");
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    public int verifyUser(String username, String password) {
        String sql = "select id from users where username = ? and password = ?";

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

    public void insertTransaction(int userId, String transactionType, double amount, String timestamp) {
        String sql = "insert into transactions(user_id, transaction_type, amount, timestamp) values(?,?,?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, transactionType);
            preparedStatement.setDouble(3, amount);
            preparedStatement.setString(4, timestamp);
            preparedStatement.executeUpdate();
            System.out.println("Transacción registrada con éxito");
        } catch (SQLException e) {
            System.out.println("Error al registrar transacción: " + e.getMessage());
        }
    }

    public ArrayList<String[]> getUserTransactions(int userId) {
        String sql = "select transaction_type, amount, timestamp from transactions where user_id = ?";
        ArrayList<String[]> transactions = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String transactionType = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");
                String timestamp = rs.getString("timestamp");
                transactions.add(new String[]{transactionType + " " + amount, timestamp});
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
}
