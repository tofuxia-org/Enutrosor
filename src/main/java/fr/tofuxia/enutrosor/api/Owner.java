package fr.tofuxia.enutrosor.api;

import java.util.UUID;

public class Owner {
    
    private UUID uuid;
    public Owner(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getOwnerUUID() {
        return this.uuid;
    }

    public static Owner getServerOwner(API api) {
        return api.getOrCreateOwner(new UUID(0L, 0L));
    }

    @Override
    public String toString() {
        return this.uuid.toString();
    }

}
