package fr.tofuxia.enutrosor.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;

import fr.tofuxia.enutrosor.Database;
import fr.tofuxia.enutrosor.api.API;
import fr.tofuxia.enutrosor.api.Owner;
import fr.tofuxia.enutrosor.api.OwnerTypePair;
import fr.tofuxia.enutrosor.api.Transaction;
import fr.tofuxia.enutrosor.api.TransactionReason;
import fr.tofuxia.enutrosor.api.Wallet;
import fr.tofuxia.enutrosor.api.WalletType;

public class APIImpl implements API {

    private HashMap<UUID, Owner> owners = new HashMap<>();
    private HashMap<OwnerTypePair, Wallet> wallets = new HashMap<>();
    private Logger logger;
    
    @Override
    public Optional<Wallet> getWallet(Owner owner, WalletType walletType) {
        return Optional.ofNullable(wallets.get(new OwnerTypePair(owner, walletType)));
    }

    @Override
    public Optional<Wallet> createWallet(Owner owner, WalletType walletType) {
        int id = Database.nextWalletId();
        if(id == -1) {
            logger.error("❌ Could not create wallet");
            return Optional.empty();
        }
        Wallet wallet = new WalletImpl(id, owner, walletType);
        wallets.put(new OwnerTypePair(owner, walletType), wallet);
        return Optional.of(wallet);
    }

    @Override
    public Optional<Wallet> getOrCreateWallet(Owner owner, WalletType walletType) {
        Optional<Wallet> get = getWallet(owner, walletType);
        logger.info("🔍 Wallet found: " + (get.isPresent() ? "✅" : "❌"));
        if(get.isPresent()) return get;
        Optional<Wallet> wallet = createWallet(owner, walletType);
        if(wallet.isEmpty()) {
            logger.error("❌ Could not create wallet");
            return Optional.empty();
        }
        logger.info("✅ Wallet created: " + wallet.get());
        wallets.put(new OwnerTypePair(owner, walletType), wallet.get());
        Database.saveWallet(wallet.get());
        return wallet;
    }

    public Optional<Transaction> createTransaction(Wallet from, Wallet to, int amount, TransactionReason reason) {
        return createTransaction(from.getOwner(), from.getWalletType(), to.getOwner(), to.getWalletType(), amount, reason);
    }

    private Optional<Transaction> createTransaction(
        Owner ownerFrom, WalletType walletTypeFrom,
        Owner ownerTo, WalletType walletTypeTo,
        int amount, TransactionReason reason) {
        
        Optional<Wallet> walletFromOpt = getOrCreateWallet(ownerFrom, walletTypeFrom);
        Optional<Wallet> walletToOpt = getOrCreateWallet(ownerTo, walletTypeTo);

        if(walletFromOpt.isEmpty() || walletToOpt.isEmpty()) {
            logger.error("❌ Could not create transaction because of missing wallets");
            return Optional.empty();
        }

        Wallet walletFrom = walletFromOpt.get();
        Wallet walletTo = walletToOpt.get();

        // If any of the types is BANK, the Owner should be the same
        if(walletTypeFrom == WalletType.BANK || walletTypeTo == WalletType.BANK) {
            if(!ownerFrom.equals(ownerTo)) {
                logger.error("❌ Bank transactions must be between the same owner");
                return Optional.empty();
            }
        }

        Transaction transaction = new Transaction.Builder()
        .amount(amount)
        .from(walletFrom)
        .to(walletTo)
        .timestamp((int) (System.currentTimeMillis() / 1000))
        .reason(reason)
        .build();

        if(walletFrom.remove(amount)) {
            walletTo.add(amount);
            walletFrom.transactions().add(transaction);
            walletTo.transactions().add(transaction);
            Database.saveTransaction(transaction);
            Database.saveWallets(walletFrom, walletTo);
            logger.info("✅ Transaction created: " + transaction);
            return Optional.of(transaction);
        } else {
            logger.error("❌ Not enough funds in the wallet");
            return Optional.empty();
        }

    }

    @Override
    public Collection<Wallet> getWalletsWhereOwnerLike(String input) {
        // Find all wallets where the owner UUID starts with the input
        Collection<Wallet> result = wallets.values().stream().filter(wallet -> wallet.getOwner().getOwnerUUID().toString().startsWith(input)).toList();
        logger.info("🔍 Found " + result.size() + " wallets for input " + input);
        return result;
    }

    @Override
    public Optional<Owner> getOwner(UUID uuid) {
        logger.info("🔍 Getting owner for " + uuid);
        return Optional.ofNullable(owners.get(uuid));
    }

    public Owner getOrCreateOwner(UUID uuid) {
        logger.info("🔍 Getting or creating owner for " + uuid);
        return owners.computeIfAbsent(uuid, (id) -> {
            Owner owner = new Owner(id);
            return owner;
        });
    }

    @Override
    public Collection<Wallet> getWalletsWhereOwner(Owner owner) {
        Collection<Wallet> result = wallets.values().stream().filter(wallet -> wallet.getOwner().equals(owner)).toList();
        logger.info("🔍 Found " + result.size() + " wallets for owner " + owner);
        return result;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

}
