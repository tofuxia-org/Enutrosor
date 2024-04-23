package fr.tofuxia.enutrosor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;

import fr.tofuxia.enutrosor.api.Transaction;
import fr.tofuxia.enutrosor.api.Wallet;

public class Database {

    private static Connection connection = null;
    private static boolean prepared = false;
    private static Logger logger;

    /**
     * Connect to the database
     * 
     * @return true if the connection is successful or if the connection is already
     *         established otherwise false
     */
    public static boolean connect() {

        try {
            if (connection != null && !connection.isClosed())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("Failed to load the MySQL driver: " + e.getMessage());
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("Failed to load the MySQL driver: " + e.getMessage());
        }

        try {
            connection = DriverManager.getConnection(Config.jdbcUrl, Config.username, Config.password);
        } catch (SQLException e) {
            logger.error("Failed to connect to the database: " + e.getMessage());
        }

        logger.warn("Connected to the database ? {}", connection != null ? "✅" : "❌");
        return connection != null;
    }

    public static void prepare() {
        if (connection == null)
            return;
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY," + "wallet_from INT NOT NULL," + "wallet_to INT NOT NULL,"
                    + "amount INT NOT NULL," + "reason VARCHAR(255) NOT NULL," + "timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                    + ");");
            statement.execute();

            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS wallets ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY," + "owner VARCHAR(255) NOT NULL," + "type VARCHAR(255) NOT NULL,"
                    + "amount INT NOT NULL," + "UNIQUE(owner, type)"
                    + ");");
            statement.execute();

            prepared = true;
        } catch (SQLException e) {
            logger.error("Failed to prepare the database: " + e.getMessage());
        }
    }

    public static boolean isReady() {
        if (connection == null)
            logger.error("Connection is null");
        if (!prepared)
            logger.error("Database is not prepared");
        return connection != null && prepared;
    }

    private static ArrayList<Transaction> cachedTransactions = new ArrayList<>();
    public static void saveTransaction(Transaction transaction) {
        cachedTransactions.add(transaction);
        if(cachedTransactions.size() >= 10) saveCachedTransactions();
    }

    private static void saveCachedTransactions() {
        if(!isReady()) return;
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO transactions (wallet_from, wallet_to, amount, reason) VALUES (?, ?, ?, ?);");
            for(Transaction t : cachedTransactions) {
                statement.setInt(1, t.from().getId());
                statement.setInt(2, t.to().getId());
                statement.setInt(3, t.amount());
                statement.setString(4, t.reason().name());
                statement.addBatch();
            }
            statement.executeBatch();
            cachedTransactions.clear();
        } catch (SQLException e) {
            logger.error("Failed to save transactions: " + e.getMessage());
        }
    }

    private static ArrayList<Wallet> cachedWallets = new ArrayList<>();
    public static void saveWallets(Wallet walletFrom, Wallet walletTo) {
        saveWallet(walletFrom);
        saveWallet(walletTo);
    }

    public static void saveWallet(Wallet wallet) {
        cachedWallets.add(wallet);
        if(cachedWallets.size() >= 10) saveCachedWallets();
    }

    private static void saveCachedWallets() {
        if(!isReady()) {
            logger.error("Database is not ready");
            return;
        }
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO wallets (id, owner, type, amount) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE amount = ?;");
            for(Wallet w : cachedWallets) {
                statement.setInt(1, w.getId());
                statement.setString(2, w.getOwner().getOwnerUUID().toString());
                statement.setString(3, w.getWalletType().name());
                statement.setInt(4, w.getAmount()); 
                statement.setInt(5, w.getAmount()); // Update the amount if the wallet already exists
                statement.addBatch();
            }
            statement.executeBatch();
            cachedWallets.clear();
        } catch (SQLException e) {
            logger.error("Failed to save wallets: " + e.getMessage());
        }
    }

    public static int nextWalletId() {
        if(!isReady()) return -1; // If the database is not ready, return -1
        
        // Firslty, we need to save the cached wallets
        saveCachedWallets();

        try {
            // Prepare a statement to select the maximum ID from the wallets table
            PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) FROM wallets;");
            ResultSet resultSet = statement.executeQuery(); // Execute the query
            if(resultSet.next()) return resultSet.getInt(1) + 1; // If there's a result, return the max ID + 1
            else return 1; // If there's no result (i.e., the table is empty), return 1
        } catch (SQLException e) {
            // If there's an SQL exception, log the error message
            logger.error("Failed to get the next wallet id: " + e.getMessage());
        }
        return -1; // If there's an exception, return -1
    }

    public static void onShutdown() {
        logger.info("Shutting down the database...");
        saveRemaining();
        cleanUp();
    }

    private static void saveRemaining() {
        logger.info("Saving remaining data...");
        saveCachedWallets();
        saveCachedTransactions();
    }

    public static void cleanUp() {
        logger.info("Cleaning up the database connection");
        if(connection == null) return;
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error("Failed to close the connection: " + e.getMessage());
        }
    }

    public static void setLogger(Logger pLogger) {
        logger = pLogger;
    }

}
