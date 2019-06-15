package com.revolut.services.moneycontrol.model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Money Transfer Request Object to capture state of Money Transfer Request
 * Null Validations Messages are inline
 */
@XmlType(name = "MoneyTransferRequest")
@XmlEnum
public class MoneyTransferRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = Constants.MONEY_TRANSFER_REQUEST_FROM_ACC_NUM_REQUIRED)
    @Length(min = 7, max = 7, message = Constants.MONEY_TRANSFER_REQUEST_ACCOUNT_NUM_LENGTH)
    private String fromAccountNum;
    @NotNull(message = Constants.MONEY_TRANSFER_REQUEST_TO_ACC_NUM_REQUIRED)
    @Length(min = 7, max = 7, message = Constants.MONEY_TRANSFER_REQUEST_ACCOUNT_NUM_LENGTH)
    private String toAccountNum;
    @NotNull(message = Constants.MONEY_TRANSFER_REQUEST_AMOUNT_REQUIRED)
    private Double amount;
    @NotNull(message = Constants.MONEY_TRANSFER_REQUEST_CURRENCY_REQUIRED)
    private String currency;


    public MoneyTransferRequest(String fromAccountNum, String toAccountNum, Double amount, String currency) {
        this.fromAccountNum = fromAccountNum;
        this.toAccountNum = toAccountNum;
        this.amount = amount;
        this.currency = currency;
    }

    public MoneyTransferRequest() {
        super();
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

    public String getCurrency() {
        return currency;
    }
}
