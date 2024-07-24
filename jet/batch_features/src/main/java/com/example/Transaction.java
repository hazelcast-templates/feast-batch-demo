package com.example;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {
    private String accountNumber;
    private Long unixTime;
    private double amount;

    @JsonProperty("acct_num")
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @JsonProperty("unix_time")
    public Long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(Long unixTime) {
        this.unixTime = unixTime;
    }

    @JsonProperty("amt")
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
