package com.revolut.services.moneycontrol.dao.impl;

import com.revolut.services.moneycontrol.adapter.DBAdapter;
import com.revolut.services.moneycontrol.dao.MoneyControlDAO;
import com.revolut.services.moneycontrol.exception.DAOException;
import com.revolut.services.moneycontrol.model.Account;
import com.revolut.services.moneycontrol.model.Constants;
import com.revolut.services.moneycontrol.model.Currency;
import com.revolut.services.moneycontrol.model.MoneyTransferRecord;
import com.revolut.services.moneycontrol.util.Utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * InMemory Implementation of DAO layer.
 *Uses DBAdapter to connect to a particular DB
 */
public class InMemoryMoneyControlDAOImpl implements MoneyControlDAO {

    private static final Logger LOGGER = Logger.getLogger(InMemoryMoneyControlDAOImpl.class.getName());
    private final DBAdapter h2DBAdapter;
    private String getAllAccountDetailsQuery = "select account_number, name, balance, currency, version, creation_date from Account";

    public InMemoryMoneyControlDAOImpl(DBAdapter h2DBAdapter) {
        this.h2DBAdapter = h2DBAdapter;
        init();
    }

    /**
     * Loads the Tables and the Data
     */
    private void init() {
        try {
            h2DBAdapter.setupTable(Constants.TABLES_SCRIPT);
            h2DBAdapter.setupTable(Constants.DATA_SCRIPT);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            LOGGER.log(Level.SEVERE, "Exception encountered in transferMoney init:", e);
        }
    }

    @Override
    public MoneyTransferRecord transferMoney(String fromAccountNum, String toAccountNum, double amount, String currency)
            throws DAOException {
        LOGGER.log(Level.INFO, "Transfer Money initiated from : " + fromAccountNum + " to : "
                + toAccountNum + " amount : " + amount);
        MoneyTransferRecord result = new MoneyTransferRecord(UUID.randomUUID().toString(),
                fromAccountNum, toAccountNum, amount, currency);
        Connection connection = null;
        try {
            connection = h2DBAdapter.getConnection();
            connection.setAutoCommit(false);
            return processMoneyTransfer(connection, fromAccountNum, toAccountNum, amount, currency, result);
        } catch (ClassNotFoundException | SQLException e) {
            if (Objects.nonNull(connection)) {
                try {
                    connection.rollback();
                    LOGGER.log(Level.SEVERE, "ClassNotFoundException or SQLException encountered in transferMoney :", e);
                    throw new DAOException("ClassNotFoundException or SQLException encountered in transferMoney :  ", e);
                } catch (SQLException e1) {
                    LOGGER.log(Level.SEVERE, "Exception encountered in transferMoney while rollback :", e1);
                    throw new DAOException("Exception encountered in transferMoney while rollback : ", e1);
                }
            } else {
                result.setReason("Connection is Null");
                result.setStatus(false);
                return result;
            }
        }
    }

    @Override
    public Optional<Account> getAccountDetails(String accountNum) throws DAOException {
        try {
            String queryStr = getAllAccountDetailsQuery + " where account_number ='" + accountNum + "'";
            final List<Map<String, Object>> result = h2DBAdapter.query(queryStr);
            if (Objects.nonNull(result) && result.size() > 0) {
                return Optional.of(parseMapForAccountRequest(result.get(0)));
            }
            LOGGER.log(Level.SEVERE, "Account Not found : " + accountNum);
            return Optional.empty();
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "SQLException encountered in getAccountDetails querying :", e);
            throw new DAOException("SQLException encountered in getAccountDetails querying : ", e);
        }
    }

    @Override
    public Collection<Account> getAllAccountDetails() throws DAOException {
        try {
            final List<Map<String, Object>> result = h2DBAdapter.query(getAllAccountDetailsQuery);
            List<Account> list = new ArrayList<>();
            if (Objects.nonNull(result) && result.size() > 0) {
                result.forEach(r -> list.add(parseMapForAccountRequest(r)));
            }
            return list;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception encountered in getAccountDetails :", e);
            throw new DAOException("Exception encountered in getAccountDetails : ", e);
        }
    }

    private Account parseMapForAccountRequest(Map<String, Object> map) {
        return new Account(map.get(Constants.ACCOUNT_NUM_LABEL).toString(), map.get(Constants.
                ACCOUNT_NAME_LABEL).toString(),
                new Double(map.get(Constants.ACCOUNT_BALANCE_LABEL).toString()), Currency.valueOf(
                        map.get(Constants.ACCOUNT_CURRENCY_LABEL).toString()),
                new Integer(map.get(
                        Constants.ACCOUNT_VERSION_LABEL).toString()),
                Utils.getInstant(map.get(Constants.ACCOUNT_CREATION_DATE_LABEL).toString()));
    }

    private boolean debit(Connection connection, String fromAccountNum, double balance, int version,
                          double amount) throws SQLException {
        String debitQuery = "update account set balance = " + (balance - amount) + ", version = " + (version + 1) +
                " where account_number ='" + fromAccountNum + "' and balance = " + balance + " and version = " + version;
        return h2DBAdapter.update(connection, debitQuery);
    }

    private boolean credit(Connection connection, String toAccountNum, double balance, int version, double amount)
            throws SQLException {
        String creditQuery = "update account set balance = " + (balance + amount) + ", version = " + (version + 1)
                + " where account_number ='" + toAccountNum + "' and balance = " + balance + " and version = " + version;
        return h2DBAdapter.update(connection, creditQuery);
    }

    private boolean processAccountNotFound(Optional<Account> accountOptional, MoneyTransferRecord result, String num) {
        if (!accountOptional.isPresent()) {
            result.setStatus(false);
            LOGGER.log(Level.SEVERE, "Account Not found : " + num);
            result.setReason("Account Not found : " + num);
            return true;
        }
        return false;
    }

    private MoneyTransferRecord processMoneyTransfer(Connection connection, String fromAccountNum,
        String toAccountNum, double amount, String currency, MoneyTransferRecord result) throws SQLException, DAOException {
        final Optional<Account> fromAccount = getAccountDetails(fromAccountNum);
        if (processAccountNotFound(fromAccount, result, fromAccountNum)) {
            return result;
        }
        final Optional<Account> toAccount = getAccountDetails(toAccountNum);
        if (processAccountNotFound(toAccount, result, toAccountNum)) {
            return result;
        }
        if(!(currency.equals(fromAccount.get().getCurrency().getDescription()) || currency.equals(toAccount.get().getCurrency()
                .getDescription()))){
            String reason = String.format("Cross currency transfer not supported for money Transfer from : %s to: %s",
                    fromAccountNum, toAccountNum);
            LOGGER.log(Level.SEVERE, reason);
            result.setReason(reason);
            result.setStatus(false);
            return result;
        }
        double fromAccountBalance = fromAccount.get().getBalance();
        String reason;
        if (!fromAccount.get().isDebitPermitted(amount)) {
            reason = String.format("Insufficient Balance for money Transfer from : %s to: %s",
                    fromAccountNum, toAccountNum);
            LOGGER.log(Level.SEVERE, reason);
            result.setReason(reason);
            result.setStatus(false);
            return result;
        } else {
            if (debit(connection, fromAccountNum, fromAccountBalance, fromAccount.get().getVersion(), amount)) {
                LOGGER.log(Level.INFO, String.format("Transfer Money -Debit completed - from : %s amount : %s",
                        fromAccountNum, amount));
                if (credit(connection, toAccountNum, toAccount.get().getBalance(), fromAccount.get().getVersion(),
                        amount)) {
                    connection.commit();
                    LOGGER.log(Level.INFO, String.format("Transfer Money complete from : %s to: %s amount : %s",
                            fromAccountNum, toAccountNum, amount));
                    result.setReason("Success");
                    result.setStatus(true);
                    return result;
                } else {
                    reason = String.format("Transfer Money -Credit failed - to : %s amount : %s",
                            toAccountNum, amount);
                    LOGGER.log(Level.INFO, reason);
                }
            } else {
                reason = "Transfer Money -Debit failed - from : " + fromAccountNum + " amount : " + amount;
                LOGGER.log(Level.INFO, reason);
            }
        }
        LOGGER.log(Level.INFO, String.format("Rollback transaction Transfer Money initiated from : %s to: %s " +
                "amount : %s", fromAccountNum, toAccountNum, amount));
        connection.rollback();
        result.setReason(reason);
        result.setStatus(false);
        return result;
    }
}
