package com.revolut.services.moneycontrol.service.impl;

import com.revolut.services.moneycontrol.dao.MoneyControlDAO;
import com.revolut.services.moneycontrol.exception.DAOException;
import com.revolut.services.moneycontrol.exception.ServiceException;
import com.revolut.services.moneycontrol.model.Account;
import com.revolut.services.moneycontrol.model.Currency;
import com.revolut.services.moneycontrol.model.MoneyTransferRecord;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class MoneyControlServiceImplTest {

    @Mock
    private MoneyControlDAO moneyControlDAO;
    @InjectMocks
    private MoneyControlServiceImpl moneyControlService;
    private static Account account;
    private static List<Account> accountList;

    @BeforeClass
    public static void setup(){
        accountList = new ArrayList<>();
        account = new Account("1234","User1",
                1000.0, Currency.USD,1, Instant.now());
        accountList.add(account);
    }

    @Test
    public void transferMoney() throws DAOException, ServiceException {
        MoneyTransferRecord result = new MoneyTransferRecord("ID1234","123456", "123457", 100.50, "USD");
        Mockito.when(moneyControlDAO.transferMoney(Mockito.anyString(), Mockito.anyString(), Mockito.anyDouble(),
                Mockito.anyString())).thenReturn(result);
        final MoneyTransferRecord moneyTransferResult = moneyControlService.transferMoney("123456", "123457", 200.0, "USD");
        Assert.assertNotNull(moneyTransferResult);
        Assert.assertEquals(result.getId(), moneyTransferResult.getId());
        Assert.assertEquals(result.getFromAccountNum(), moneyTransferResult.getFromAccountNum());
        Assert.assertEquals(result.getToAccountNum(), moneyTransferResult.getToAccountNum());
        Assert.assertEquals(result.getReason(), moneyTransferResult.getReason());
        Assert.assertEquals(result.getStatus(), moneyTransferResult.getStatus());
        Assert.assertEquals(result.getAmount(), moneyTransferResult.getAmount());
    }

    @Test(expected = ServiceException.class)
    public void transferMoneyException1() throws DAOException, ServiceException {
        Mockito.when(moneyControlDAO.transferMoney(Mockito.anyString(), Mockito.anyString(), Mockito.anyDouble(),
                Mockito.anyString())).thenThrow(DAOException.class);
        moneyControlService.transferMoney("123456", "123457", 200.0, "USD");
    }

    @Test(expected = ServiceException.class)
    public void transferMoneyException2() throws DAOException, ServiceException {
        Mockito.when(moneyControlDAO.transferMoney(Mockito.anyString(), Mockito.anyString(), Mockito.anyDouble(),
                Mockito.anyString())).thenThrow(SQLException.class);
        moneyControlService.transferMoney("123456", "123457", 200.0, "USD");
    }

    @Test
    public void getAccountDetailsSuccess() throws DAOException, ServiceException {
        Mockito.when(moneyControlDAO.getAccountDetails(Mockito.anyString())).thenReturn(Optional.of(account));
        final Optional<Account> accountOptional = moneyControlService.getAccountDetails("1234");
        Assert.assertNotNull(accountOptional);
        Assert.assertEquals(account.getAccountNumber(), accountOptional.get().getAccountNumber());
        Assert.assertEquals(account.getName(), accountOptional.get().getName());
        Assert.assertEquals(account.getBalance(), accountOptional.get().getBalance());
        Assert.assertEquals(account.getVersion(), accountOptional.get().getVersion());
    }

    @Test(expected = ServiceException.class)
    public void getAccountDetailsException1() throws DAOException, ServiceException {
        Mockito.when(moneyControlDAO.getAccountDetails(Mockito.anyString())).thenThrow(DAOException.class);
        moneyControlService.getAccountDetails("1234");
    }

    @Test(expected = ServiceException.class)
    public void getAccountDetailsException2() throws DAOException, ServiceException {
        Mockito.when(moneyControlDAO.getAccountDetails(Mockito.anyString())).thenThrow(SQLException.class);
        moneyControlService.getAccountDetails("1234");
    }

    @Test
    public void getAllAccountDetails() throws DAOException, ServiceException {
        Mockito.when(moneyControlDAO.getAllAccountDetails()).thenReturn(accountList);
        final Collection<Account> accountDetailsList = moneyControlService.getAllAccountDetails();
        Assert.assertNotNull(accountDetailsList);
        Assert.assertTrue(accountDetailsList.size() > 0);
        for(Account acc : accountDetailsList){
            Assert.assertEquals(account.getAccountNumber(), acc.getAccountNumber());
            Assert.assertEquals(account.getName(), acc.getName());
            Assert.assertEquals(account.getBalance(), acc.getBalance());
            Assert.assertEquals(account.getVersion(), acc.getVersion());
        }
    }

    @Test(expected = ServiceException.class)
    public void getAllAccountDetailsException1() throws DAOException, ServiceException {
        Mockito.when(moneyControlDAO.getAllAccountDetails()).thenThrow(DAOException.class);
        moneyControlService.getAllAccountDetails();
    }

    @Test(expected = ServiceException.class)
    public void getAllAccountDetailsException2() throws DAOException, ServiceException {
        Mockito.when(moneyControlDAO.getAllAccountDetails()).thenThrow(SQLException.class);
        moneyControlService.getAllAccountDetails();
    }
}