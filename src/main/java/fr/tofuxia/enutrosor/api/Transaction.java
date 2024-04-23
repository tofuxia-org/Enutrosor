package fr.tofuxia.enutrosor.api;

public record Transaction(int amount, Wallet from, Wallet to, int timestamp, TransactionReason reason) {

    public static class Builder {
        private int amount;
        private Wallet from;
        private Wallet to;
        private int timestamp;
        private TransactionReason reason = TransactionReason.UNKNOWN;

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder from(Wallet from) {
            this.from = from;
            return this;
        }

        public Builder to(Wallet to) {
            this.to = to;
            return this;
        }

        public Builder timestamp(int timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder reason(TransactionReason reason) {
            this.reason = reason;
            return this;
        }

        public Transaction build() {
            // Validate transaction
            if (amount <= 0 || from == null || to == null || timestamp == 0) {
                throw new InvalidTransactionException(amount, from, to, timestamp);
            }
            return new Transaction(amount, from, to, timestamp, reason);
        }
    }

    public static class InvalidTransactionException extends IllegalStateException {

        private final int amount, timestamp;
        private final Wallet from, to;

        public InvalidTransactionException(int amount, Wallet from, Wallet to, int timestamp) {
            super("Invalid transaction: amount=" + amount + ", from=" + from + ", to=" + to + ", timestamp=" + 
                    + timestamp);
            this.amount = amount;
            this.from = from;
            this.to = to;
            this.timestamp = timestamp;
        }

        @Override
        public String getMessage() {
            // Specify which field is invalid
            if (amount <= 0) {
                return "Invalid transaction amount: " + amount;
            } else if (timestamp <= 0) {
                return "Invalid transaction timestamp: " + timestamp;
            } else if (from == null) {
                return "Invalid transaction source: " + from.toString();
            } else if (to == null) {
                return "Invalid transaction to: " + to.toString();
            } else {
                return "Invalid transaction: amount=" + amount + ", from=" + from.toString() + ", to=" + to.toString()
                        + ", timestamp=" + timestamp;
            }
        }

    }

}