package com.splitbills.splitbills_backend.model;

public class SettlementDTO {
    private Long userId;
    private String userName;
    private Double balance;

    public SettlementDTO(Long userId, String userName, Double balance) {
        this.userId = userId;
        this.userName = userName;
        this.balance = balance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
