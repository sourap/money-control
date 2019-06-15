package com.revolut.services.moneycontrol.service;

import com.revolut.services.moneycontrol.exception.ServiceException;
import com.revolut.services.moneycontrol.model.Account;
import com.revolut.services.moneycontrol.model.MoneyTransferRecord;

import java.util.Collection;
import java.util.Optional;

/**
 * Service Layer for Money Control
 */
public interface MoneyControlService {

    /**
     * Transfer given amount from accountNum1 to AccountNum2
     *
     * @param accountNum1 sender Account Number
     * @param accountNum2 recipient Account Number
     * @param amount given amount
     * @param amount given currency
     * @return MoneyTransferRecord
     * @throws ServiceException ServiceException
     */
    MoneyTransferRecord transferMoney(String accountNum1, String accountNum2, double amount, String currency)
            throws ServiceException;

    /**
     * Retrieve Account Details for a given AccountNumber
     *
     * @param accountNum given Account Number
     * @return Account Optional Object
     * @throws ServiceException ServiceException
     */
    Optional<Account> getAccountDetails(String accountNum) throws ServiceException;

    /**
     * List of All Accounts
     *
     * @return Collection of Account Objects
     * @throws ServiceException ServiceException
     */
    Collection<Account> getAllAccountDetails() throws ServiceException;


}
