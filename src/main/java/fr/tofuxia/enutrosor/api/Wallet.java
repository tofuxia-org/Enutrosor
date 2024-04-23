package fr.tofuxia.enutrosor.api;
import java.util.Collection;

public interface Wallet {
    boolean add(int amount);
    boolean remove(int amount);
    Collection<Transaction> transactions();
    Owner getOwner();
    WalletType getWalletType();
    int getId();
    int getAmount();
}
