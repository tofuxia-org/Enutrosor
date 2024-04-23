package fr.tofuxia.enutrosor.api;

public enum TransactionReason {
    PLAYER_TRADE,
    NPC_SHOP,
    NPC_QUEST,
    PLAYER_SHOP,
    BANK_DEPOSIT,
    BANK_WITHDRAW,
    GAME_MASTER,
    OTHER,
    UNKNOWN; // Do not use this value, it is only used for invalid transactions
}
