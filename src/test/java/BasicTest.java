import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.tofuxia.enutrosor.Config;
import fr.tofuxia.enutrosor.Database;
import fr.tofuxia.enutrosor.api.API;
import fr.tofuxia.enutrosor.api.Owner;
import fr.tofuxia.enutrosor.api.Transaction;
import fr.tofuxia.enutrosor.api.TransactionReason;
import fr.tofuxia.enutrosor.api.Wallet;
import fr.tofuxia.enutrosor.api.WalletType;
import fr.tofuxia.enutrosor.impl.APIImpl;

public class BasicTest {

    private Logger logger;

    @Test
    public void databaseConnection() {

        logger = LoggerFactory.getLogger(BasicTest.class);
        Database.setLogger(logger);

        logger.info("Database connection test");

        logger.info("Setting up the database configuration...");
        Config.jdbcUrl = loadFromFile("./.jdbcUrl.secret");
        Config.username = loadFromFile("./.username.secret");
        Config.password = loadFromFile("./.password.secret");

        logger.info("Connecting to the database...");
        assert(Database.connect());

        logger.info("Preparing the database...");
        Database.prepare();

        logger.info("Checking if the database is ready...");
        assert(Database.isReady());

        logger.info("Shutting down the database...");
        Database.onShutdown();
    }

    @Test
    public void ownerCreation() {
        API api = baseSetup();
        UUID uuid = UUID.randomUUID();
        api.getOrCreateOwner(uuid);
        assert(api.getOwner(uuid).isPresent());
        cleanup();
    }

    @Test
    public void walletCreation() {
        API api = baseSetup();
        Owner owner = api.getOrCreateOwner(UUID.randomUUID());
        assert(api.getOrCreateWallet(owner, WalletType.SERVER).isPresent());
        cleanup();
    }

    @Test
    public void transactionCreation() {
        API api = baseSetup();
        
        UUID uuidFrom = UUID.randomUUID();
        UUID uuidTo = UUID.randomUUID();
        
        Owner ownerFrom = api.getOrCreateOwner(uuidFrom);
        Owner ownerTo = api.getOrCreateOwner(uuidTo);

        Wallet fromP = api.getOrCreateWallet(ownerFrom, WalletType.PLAYER).get();
        Wallet fromB = api.getOrCreateWallet(ownerFrom, WalletType.BANK).get();

        Wallet toP = api.getOrCreateWallet(ownerTo, WalletType.PLAYER).get();
        Wallet toB = api.getOrCreateWallet(ownerTo, WalletType.BANK).get();

        assert(fromP.getOwner().getOwnerUUID().equals(uuidFrom));
        assert(fromB.getOwner().getOwnerUUID().equals(uuidFrom));
        assert(toP.getOwner().getOwnerUUID().equals(uuidTo));
        assert(toB.getOwner().getOwnerUUID().equals(uuidTo));

        assert(fromP.getWalletType() == WalletType.PLAYER);
        assert(fromB.getWalletType() == WalletType.BANK);
        assert(toP.getWalletType() == WalletType.PLAYER);
        assert(toB.getWalletType() == WalletType.BANK);

        fromP.add(30);
        fromB.add(980);

        Optional<Transaction> playerToPlayer = api.createTransaction(fromP, toP, 10, TransactionReason.GAME_MASTER);
        assert(playerToPlayer.isPresent()); // Should succeed because player to player transfers are allowed

        Optional<Transaction> playerToSelfBank = api.createTransaction(fromP, fromB, 20, TransactionReason.GAME_MASTER);
        assert(playerToSelfBank.isPresent()); // Should succeed because player to own bank transfers are allowed

        Optional<Transaction> playerToOtherBank = api.createTransaction(fromP, toB, 10, TransactionReason.GAME_MASTER);
        assert(!playerToOtherBank.isPresent()); // Should fail because bank transfers are only allowed to same owner

        Optional<Transaction> bankToOtherPlayer = api.createTransaction(fromB, toP, 10, TransactionReason.GAME_MASTER);
        assert(!bankToOtherPlayer.isPresent()); // Should fail because bank transfers are only allowed to same owner   

        Optional<Transaction> bankToOtherBank = api.createTransaction(fromB, toB, 10, TransactionReason.GAME_MASTER);
        assert(!bankToOtherBank.isPresent()); // Should fail because bank transfers are only allowed to same owner

        Optional<Transaction> playerToPlayerInssuficientFunds = api.createTransaction(toP, fromP, Integer.MAX_VALUE, TransactionReason.GAME_MASTER);
        assert(!playerToPlayerInssuficientFunds.isPresent()); // Should fail because player doesn't have enough money

        assert(fromP.getAmount() == 0);
        assert(fromB.getAmount() == 1000);
        assert(toP.getAmount() == 10);
        assert(toB.getAmount() == 0);

        cleanup();
    }

    private APIImpl baseSetup() {
        logger = LoggerFactory.getLogger(BasicTest.class);
        Config.jdbcUrl = loadFromFile("./.jdbcUrl.secret");
        Config.username = loadFromFile("./.username.secret");
        Config.password = loadFromFile("./.password.secret");
        Database.setLogger(logger);
        Database.connect();
        Database.prepare();
        APIImpl api = new APIImpl();
        api.setLogger(logger);
        return api;
    }

    private void cleanup() {
        Database.onShutdown();
    }

    private String loadFromFile(String fileName) {
        try {
            return Files.readString(Paths.get(fileName));
        } catch (IOException e) {
            logger.error("Failed to read file: " + fileName);
            try {
                Files.writeString(Paths.get(fileName), "CHANGE_ME");
            } catch (IOException e1) {
                e1.printStackTrace();
                return "";
            }
            return "";
        }
    }
    
}