package fr.tofuxia.enutrosor.api;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface API {
    Optional<Wallet> getWallet(Owner owner, WalletType walletType);

    Optional<Wallet> createWallet(Owner owner, WalletType walletType);

    Optional<Wallet> getOrCreateWallet(Owner owner, WalletType walletType);

    Optional<Transaction> createTransaction(Wallet from, Wallet to, int amount, TransactionReason reason);

    Collection<Wallet> getWalletsWhereOwnerLike(String input);
    
    Collection<Wallet> getWalletsWhereOwner(Owner owner);

    Optional<Owner> getOwner(UUID uuid);

    Owner getOrCreateOwner(UUID uuid);


}
