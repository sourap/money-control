package com.revolut.services.moneycontrol.dao.impl;

import com.revolut.services.moneycontrol.adapter.impl.H2Adapter;
import com.revolut.services.moneycontrol.exception.DAOException;
import com.revolut.services.moneycontrol.model.Account;
import com.revolut.services.moneycontrol.model.Constants;
import com.revolut.services.moneycontrol.model.MoneyTransferRecord;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryMoneyControlDAOImplTest {
    @InjectMocks
    private InMemoryMoneyControlDAOImpl moneyControlDAO;
    @Mock
    private H2Adapter h2Adapter;
    @Mock
    private Connection connection;
    private static List<Map<String, Object>> list;
    private static Map<String, Object> map1;
    private static Map<String, Object> map2;

    @BeforeClass
    public static void setUp() {
        list = new ArrayList<>();
        map1 = new HashMap<>();
        map1.put(Constants.ACCOUNT_NUM_LABEL, "1234567");
        map1.put(Constants.ACCOUNT_NAME_LABEL, "User1");
        map1.put(Constants.ACCOUNT_BALANCE_LABEL, "1234");
        map1.put(Constants.ACCOUNT_CURRENCY_LABEL, "USD");
        map1.put(Constants.ACCOUNT_VERSION_LABEL, "1");
        map1.put(Constants.ACCOUNT_CREATION_DATE_LABEL, "2019-06-13 00:00:00.0");

        map2 = new HashMap<>();
        map2.put(Constants.ACCOUNT_NUM_LABEL, "1234568");
        map2.put(Constants.ACCOUNT_NAME_LABEL, "User2");
        map2.put(Constants.ACCOUNT_BALANCE_LABEL, "100");
        map2.put(Constants.ACCOUNT_CURRENCY_LABEL, "USD");
        map2.put(Constants.ACCOUNT_VERSION_LABEL, "1");
        map2.put(Constants.ACCOUNT_CREATION_DATE_LABEL, "2019-06-10 00:00:00.0");
        list.add(map1);
        list.add(map2);
    }

    @Test
    public void transferMoneySuccess() throws SQLException, ClassNotFoundException, DAOException {
        setUpAccounts();
        String q3 = "update account set balance = 1124.0, version = 2 where account_number ='1234567' and balance = 1234.0 and version = 1";
        String q4 = "update account set balance = 210.0, version = 2 where account_number ='1234568' and balance = 100.0 and version = 1";
        Mockito.when(h2Adapter.update(connection, q3)).thenReturn(true);
        Mockito.when(h2Adapter.update(connection, q4)).thenReturn(true);
        final MoneyTransferRecord result = moneyControlDAO.transferMoney("1234567",
                "1234568", 110.0, "USD");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getStatus());
        Assert.assertEquals("Success", result.getReason());
    }

    @Test
    public void transferMoneyNullConnection() throws SQLException, ClassNotFoundException, DAOException {
        Mockito.when(h2Adapter.getConnection()).thenThrow(ClassNotFoundException.class);
        final MoneyTransferRecord result = moneyControlDAO.transferMoney("1234567",
                "1234568", 110.0, "USD");
        Assert.assertNotNull(result);
        Assert.assertFalse(result.getStatus());
        Assert.assertEquals("Connection is Null", result.getReason());
    }

    @Test(expected = DAOException.class)
    public void transferMoneyException1() throws SQLException, ClassNotFoundException, DAOException {
        setUpAccounts();
        Mockito.when(h2Adapter.update(Mockito.any(Connection.class), Mockito.anyString())).thenThrow(ClassNotFoundException.class);
        moneyControlDAO.transferMoney("1234567", "1234568", 110.0, "USD");
    }

    @Test(expected = DAOException.class)
    public void transferMoneyException2() throws SQLException, ClassNotFoundException, DAOException {
        setUpAccounts();
        Mockito.when(h2Adapter.update(Mockito.any(Connection.class), Mockito.anyString())).thenThrow(SQLException.class);
        Mockito.doThrow(SQLException.class).when(connection).rollback();
        moneyControlDAO.transferMoney("1234567", "1234568", 110.0, "USD");
    }

    @Test
    public void transferMoneyFromAccountNotFound() throws SQLException, ClassNotFoundException, DAOException {
        setUpAccounts();
        final MoneyTransferRecord result = moneyControlDAO.transferMoney("1234",
                "1235", 2000.0, "USD");
        Assert.assertNotNull(result);
        Assert.assertFalse(result.getStatus());
        Assert.assertEquals("Account Not found : 1234", result.getReason());
    }

    @Test
    public void transferMoneyToAccountNotFound() throws SQLException, ClassNotFoundException, DAOException {
        setUpAccounts();
        final MoneyTransferRecord result = moneyControlDAO.transferMoney("1234567",
                "1235", 2000.0, "USD");
        Assert.assertNotNull(result);
        Assert.assertFalse(result.getStatus());
        Assert.assertEquals("Account Not found : 1235", result.getReason());
    }


    @Test
    public void transferMoneyInsufficientBalance() throws SQLException, ClassNotFoundException, DAOException {
        setUpAccounts();
        final MoneyTransferRecord result = moneyControlDAO.transferMoney("1234567",
                "1234568", 2000.0, "USD");
        Assert.assertNotNull(result);
        Assert.assertFalse(result.getStatus());
        Assert.assertEquals("Insufficient Balance for money Transfer from : 1234567 to: 1234568", result.getReason());
    }

    @Test
    public void transferMoneyICrossCurrency() throws SQLException, ClassNotFoundException, DAOException {
        setUpAccounts();
        final MoneyTransferRecord result = moneyControlDAO.transferMoney("1234567",
                "1234568", 20.0, "GBP");
        Assert.assertNotNull(result);
        Assert.assertFalse(result.getStatus());
        Assert.assertEquals("Cross currency transfer not supported for money Transfer from : 1234567 to: 1234568", result.getReason());
    }

    @Test
    public void transferMoneyDebitFailed() throws SQLException, ClassNotFoundException, DAOException {
        setUpAccounts();
        String q3 = "update account set balance = 1224.0, version = 2 where account_number ='1234567' " +
                "and balance = 1234.0 and version = 1";
        Mockito.when(h2Adapter.update(connection, q3)).thenReturn(false);
        final MoneyTransferRecord result = moneyControlDAO.transferMoney("1234567",
                "1234568", 10.0, "USD");
        Assert.assertNotNull(result);
        Assert.assertFalse(result.getStatus());
        Assert.assertEquals("Transfer Money -Debit failed - from : 1234567 amount : 10.0", result.getReason());
    }

    @Test
    public void transferMoneyCreditFailed() throws SQLException, ClassNotFoundException, DAOException {
        setUpAccounts();
        String q3 = "update account set balance = 1224.0, version = 2 where account_number ='1234567' " +
                "and balance = 1234.0 and version = 1";
        Mockito.when(h2Adapter.update(connection, q3)).thenReturn(true);
        final MoneyTransferRecord result = moneyControlDAO.transferMoney("1234567",
                "1234568", 10.0, "USD");
        Assert.assertNotNull(result);
        Assert.assertFalse(result.getStatus());
        Assert.assertEquals("Transfer Money -Credit failed - to : 1234568 amount : 10.0", result.getReason());
    }

    private void setUpAccounts() throws SQLException, ClassNotFoundException {
        Mockito.when(h2Adapter.getConnection()).thenReturn(connection);
        String q1 = "select account_number, name, balance, currency, version, creation_date from Account " +
                "where account_number ='1234567'";
        String q2 = "select account_number, name, balance, currency, version, creation_date from Account " +
                "where account_number ='1234568'";

        List<Map<String,Object>> lis1 = new ArrayList<>();
        lis1.add(map1);
        List<Map<String,Object>> lis2 = new ArrayList<>();
        lis2.add(map2);
        Mockito.when(h2Adapter.query(q1)).thenReturn(lis1);
        Mockito.when(h2Adapter.query(q2)).thenReturn(lis2);
    }

    @Test
    public void getAccountDetailsSuccess() throws DAOException, SQLException, ClassNotFoundException {
        Mockito.when(h2Adapter.query(Mockito.anyString())).thenReturn(list);
        final Optional<Account> accountDetails = moneyControlDAO.getAccountDetails("1234567");
        Assert.assertNotNull(accountDetails);
        Assert.assertTrue(accountDetails.isPresent());
        Assert.assertEquals("1234567", accountDetails.get().getAccountNumber());
    }

    @Test
    public void getAccountDetailsNotFound() throws DAOException, SQLException, ClassNotFoundException {
        Mockito.when(h2Adapter.query(Mockito.anyString())).thenReturn(Collections.emptyList());
        final Optional<Account> accountDetails = moneyControlDAO.getAccountDetails("123456");
        Assert.assertNotNull(accountDetails);
        Assert.assertFalse(accountDetails.isPresent());
    }

    @Test(expected = DAOException.class)
    public void getAccountDetailsException() throws DAOException, SQLException, ClassNotFoundException {
        Mockito.when(h2Adapter.query(Mockito.anyString())).thenThrow(SQLException.class);
        final Optional<Account> accountDetails = moneyControlDAO.getAccountDetails("123456");
        Assert.assertNotNull(accountDetails);
        Assert.assertFalse(accountDetails.isPresent());
    }

    @Test(expected = DAOException.class)
    public void getAccountDetailsClassNotFoundException() throws DAOException, SQLException, ClassNotFoundException {
        Mockito.when(h2Adapter.query(Mockito.anyString())).thenThrow(ClassNotFoundException.class);
        final Optional<Account> accountDetails = moneyControlDAO.getAccountDetails("123456");
        Assert.assertNotNull(accountDetails);
        Assert.assertFalse(accountDetails.isPresent());
    }

    @Test
    public void getAllAccountDetailsSuccess() throws DAOException, SQLException, ClassNotFoundException {
        Mockito.when(h2Adapter.query(Mockito.anyString())).thenReturn(list);
        final Collection<Account> allAccountDetails = moneyControlDAO.getAllAccountDetails();
        Assert.assertNotNull(allAccountDetails);
        Assert.assertTrue(allAccountDetails.size() > 0);
    }

    @Test
    public void getAllAccountDetailsEmpty() throws DAOException, SQLException, ClassNotFoundException {
        Mockito.when(h2Adapter.query(Mockito.anyString())).thenReturn(Collections.emptyList());
        final Collection<Account> allAccountDetails = moneyControlDAO.getAllAccountDetails();
        Assert.assertNotNull(allAccountDetails);
        Assert.assertFalse(allAccountDetails.size() > 0);
    }

    @Test(expected = DAOException.class)
    public void getAllAccountDetailsException() throws DAOException, SQLException, ClassNotFoundException {
        Mockito.when(h2Adapter.query(Mockito.anyString())).thenThrow(DAOException.class);
        final Collection<Account> allAccountDetails = moneyControlDAO.getAllAccountDetails();
        Assert.assertNotNull(allAccountDetails);
        Assert.assertTrue(allAccountDetails.size() > 0);
    }
}