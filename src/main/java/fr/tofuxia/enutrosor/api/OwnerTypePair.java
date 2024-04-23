package fr.tofuxia.enutrosor.api;

import java.util.Objects;

public class OwnerTypePair {

    private Owner owner;
    private WalletType walletType;

    public OwnerTypePair(Owner owner, WalletType walletType) {
        this.owner = owner;
        this.walletType = walletType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OwnerTypePair that = (OwnerTypePair) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(walletType, that.walletType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, walletType);
    }

    @Override
    public String toString() {
        return "OwnerTypePair{" +
                "owner=" + owner +
                ", walletType=" + walletType +
                '}';
    }

}
