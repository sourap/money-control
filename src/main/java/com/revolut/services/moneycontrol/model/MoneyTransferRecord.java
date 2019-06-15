package com.revolut.services.moneycontrol.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.revolut.services.moneycontrol.util.Utils;

import java.io.Serializable;
import java.time.Instant;

/**
 * Money Transfer Record Object to Capture Result of a MoneyTransfer operation
 */
public class MoneyTransferRecord implements Serializable {

    private String id;
    private String fromAccountNum;
    private String toAccountNum;
    private Double amount;
    private String currency;
    private boolean status;
    private String reason;

    @JsonDeserialize(using = DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Instant createdDate;

    public MoneyTransferRecord() {}

    public MoneyTransferRecord(String id, String fromAccountNum, String toAccountNum,
                               Double amount, String currency) {
        this.id = id;
        this.fromAccountNum = fromAccountNum;
        this.toAccountNum = toAccountNum;
        this.amount = amount;
        this.currency = currency;
        this.createdDate = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getFromAccountNum() {
        return fromAccountNum;
    }

    public String getToAccountNum() {
        return toAccountNum;
    }

    public Double getAmount() {
        return amount;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "MoneyTransferRecord{" +
                "id='" + id + '\'' +
                ", fromAccountNum='" + fromAccountNum + '\'' +
                ", toAccountNum='" + toAccountNum + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                ", createdDate='" + Utils.getDateString(createdDate) + '\'' +
                '}';
    }
}
