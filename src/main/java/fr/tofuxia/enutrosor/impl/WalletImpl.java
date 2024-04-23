package fr.tofuxia.enutrosor.impl;

import java.util.ArrayList;
import java.util.Collection;

import fr.tofuxia.enutrosor.api.Owner;
import fr.tofuxia.enutrosor.api.Transaction;
import fr.tofuxia.enutrosor.api.Wallet;
import fr.tofuxia.enutrosor.api.WalletType;

public class WalletImpl implements Wallet {

    private final int id;
    private final Owner owner;
    private final WalletType walletType;
    private int amount;
    private Collection<Transaction> transactions;

    public WalletImpl(int id, Owner owner, WalletType walletType) {
        this.id = id;
        this.owner = owner;
        this.walletType = walletType;
        this.amount = 0;
    }

    @Override
    public boolean add(int amount) {
        this.amount += amount;
        return true;
    }

    @Override
    public boolean remove(int toRemove) {
        if(walletType == WalletType.SERVER) return true;
        if(this.amount < toRemove) return false;
        this.amount -= toRemove;
        return true;
    }

    @Override
    public Collection<Transaction> transactions() {
        if(transactions == null) transactions = new ArrayList<Transaction>();
        return transactions;
    }

    @Override
    public Owner getOwner() {
        return owner;
    }

    @Override
    public WalletType getWalletType() {
        return walletType;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "WalletImpl{" +
                "id=" + id +
                ", owner=" + owner +
                ", walletType=" + walletType +
                ", amount=" + amount +
                '}';
    }

}
