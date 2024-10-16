package mainBank.managers;

import java.io.File;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Clase que gestiona las operaciones con la DB.
 */
public class DataBaseManager {
    private Connection connection;
    private DecimalFormat df;

    /**
     * Establece el formato decimal a utilizar.
     *
     * @param df Formato decimal.
     */
    public void setDecimalFormat(DecimalFormat df) {
        this.df = df;
    }

    /**
     * Conecta a la DB SQLite.
     */
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

    /**
     * Desconecta de la DB.
     */
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

    /**
     * Crea las tablas necesarias en la DB si no existen.
     */
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

    /**
     * Inserta un nuevo usuario en la DB.
     *
     * @param username Nombre de usuario.
     * @param password Contraseña.
     * @return true si se inserta correctamente, false si el usuario ya existe o hay un error.
     */
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

    /**
     * Comprueba que no se han insertado carácteres
     * especiales en el nombre de usuario (evita SQLInyection)
     */
    public boolean userIsValid(String username) {
        String regex = "[a-zA-Z0-9_.]+$";
        return username.matches(regex);

    }

    /**
     * Verifica las credenciales de un usuario.
     *
     * @param username Nombre de usuario.
     * @param password Contraseña.
     * @return ID del usuario si las credenciales son correctas, -1 en caso contrario.
     */
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

    /**
     * Inserta una transacción en la base de datos.
     *
     * @param userId          ID del usuario.
     * @param transactionType Tipo de transacción ("+" o "-").
     * @param amount          Cantidad en euros.
     * @param timestamp       Marca de tiempo.
     * @param concept         Concepto de la transacción.
     */
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

    /**
     * Obtiene las transacciones de un usuario.
     *
     * @param userId ID del usuario.
     * @return Lista de transacciones en formato [tipo, amount, concepto, timestamp].
     */
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

    /**
     * Elimina la última transacción del usuario.
     *
     * @param userId ID del usuario.
     */
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
