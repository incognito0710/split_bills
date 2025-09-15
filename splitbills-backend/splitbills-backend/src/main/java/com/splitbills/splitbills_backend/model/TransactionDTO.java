package com.splitbills.splitbills_backend.model;

public class TransactionDTO {
    private String fromUser;
    private String toUser;
    private double amount;

    public TransactionDTO(String fromUser, String toUser, double amount) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
    }

    // Getters & Setters
    public String getFromUser() { return fromUser; }
    public void setFromUser(String fromUser) { this.fromUser = fromUser; }

    public String getToUser() { return toUser; }
    public void setToUser(String toUser) { this.toUser = toUser; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
