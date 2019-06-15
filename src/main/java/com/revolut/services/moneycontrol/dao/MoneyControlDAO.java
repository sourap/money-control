package com.revolut.services.moneycontrol.dao;

import com.revolut.services.moneycontrol.exception.DAOException;
import com.revolut.services.moneycontrol.model.Account;
import com.revolut.services.moneycontrol.model.MoneyTransferRecord;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface to Handle DAO Layer responsibilities for Money Control
 */
public interface MoneyControlDAO {

    /**
     * Transfer Money from
     * @param sender Sender accountNum
     * @param receiver Receiver accountNum
     * @param amount Amount
     * @param currency Currency
     * @return MoneyTransferRecord Object
     * @throws DAOException DAOException
     */
    MoneyTransferRecord transferMoney(String sender, String receiver, double amount, String currency) throws DAOException;

    /**
     * Get Account Details for a given Account.
     *
     * @param accountNum accountNumber
     * @return Optional Account Object
     * @throws DAOException DAOException
     */
    Optional<Account> getAccountDetails(String accountNum) throws DAOException;

    /**
     * Get All Account Details
     *
     * @return Collection Of Account Objects
     * @throws DAOException DAOException
     */
    Collection<Account> getAllAccountDetails() throws DAOException;
}
