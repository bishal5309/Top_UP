package com.example.top_up;

public class Transaction {
    private String transactionId;
    private String dateTime;
    private String amount;

    public Transaction(String transactionId, String dateTime, String amount) {
        this.transactionId = transactionId;
        this.dateTime = dateTime;
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getAmount() {
        return amount;
    }
}
