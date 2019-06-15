package com.revolut.services.moneycontrol.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.revolut.services.moneycontrol.util.Utils;

import java.io.Serializable;
import java.time.Instant;

/**
 * Account Object to capture the state of Accounts for Money Control
 */
public class Account implements Serializable {

    private String accountNumber;
    private String name;
    private Double balance;
    private Currency currency;
    private int version;
    @JsonDeserialize(using = DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Instant creationDate;

    public Account() {}

    public Account(String accountNumber, String name, Double balance, Currency currency, int version, Instant creationDate) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = balance;
        this.currency =currency;
        this.version = version;
        this.creationDate = creationDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public Double getBalance() {
        return balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public boolean isDebitPermitted(double amount) {
        return (this.balance >= amount);
    }

    @Override
    public String toString() {

        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", currency=" + currency +
                ", version=" + version +
                ", creationDate='" + Utils.getDateString(creationDate) + '\'' +
                '}';
    }
}
