package com.revolut.services.moneycontrol.service.impl;

import com.revolut.services.moneycontrol.dao.MoneyControlDAO;
import com.revolut.services.moneycontrol.exception.DAOException;
import com.revolut.services.moneycontrol.exception.ServiceException;
import com.revolut.services.moneycontrol.model.Account;
import com.revolut.services.moneycontrol.model.MoneyTransferRecord;
import com.revolut.services.moneycontrol.service.MoneyControlService;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the Service Layer. Fetching Data is delegated to the DAO layer Implementation.
 */
public class MoneyControlServiceImpl implements MoneyControlService {

    private static final Logger LOGGER = Logger.getLogger(MoneyControlServiceImpl.class.getName());
    private final MoneyControlDAO moneyControlDAO;

    public MoneyControlServiceImpl(MoneyControlDAO moneyControlDAO) {
        this.moneyControlDAO = moneyControlDAO;
    }

    @Override
    public MoneyTransferRecord transferMoney(String accountNum1, String accountNum2, double amount, String currency)
            throws ServiceException {
        try {
            LOGGER.log(Level.INFO, "Transfer Money started from : " + accountNum1 + " to: " + accountNum2 + " " +
                    "for amount : " + amount);
            return moneyControlDAO.transferMoney(accountNum1, accountNum2, amount, currency);
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "DAOException encountered in transferMoney ", e);
            throw new ServiceException("DAOException encountered in transferMoney : ", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception encountered in transferMoney ", e);
            throw new ServiceException("Exception encountered in transferMoney : ", e);
        }
    }

    @Override
    public Optional<Account> getAccountDetails(String accountNum) throws ServiceException {
        try {
            LOGGER.log(Level.INFO, "Get Account Details for : " + accountNum);
            return moneyControlDAO.getAccountDetails(accountNum);
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "DAOException encountered in getAccountDetails ", e);
            throw new ServiceException("DAOException encountered in getAccountDetails : ", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception encountered in getAccountDetails ", e);
            throw new ServiceException("Exception encountered in getAccountDetails : ", e);
        }
    }

    @Override
    public Collection<Account> getAllAccountDetails() throws ServiceException {
        try {
            LOGGER.log(Level.INFO, "Get All Account Details ");
            return moneyControlDAO.getAllAccountDetails();
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "DAOException encountered in getAccountDetails ", e);
            throw new ServiceException("DAOException encountered in getAccountDetails : ", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception encountered in getAccountDetails ", e);
            throw new ServiceException("Exception encountered in getAccountDetails : ", e);
        }
    }
}
