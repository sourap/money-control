package com.revolut.services.moneycontrol.adapter.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class H2AdapterTest {

    @InjectMocks
    private H2Adapter h2Adapter;

    @Test
    public void getConnectionSuccess() throws SQLException, ClassNotFoundException {
        final Connection connection = h2Adapter.getConnection();
        Assert.assertNotNull(connection);
        connection.close();
    }

    @Test
    public void setupTableNullFIle() throws SQLException, IOException, ClassNotFoundException {
        h2Adapter.setupTable(null);
    }

    @Test
    public void setupTableIncorrectFile() throws SQLException, IOException, ClassNotFoundException {
        h2Adapter.setupTable("scripts/data.sql");
    }

    @Test
    public void setupTableSuccess() throws SQLException, IOException, ClassNotFoundException {
        h2Adapter.setupTable("scripts/tables.sql");
    }

    @Test
    public void querySuccess() throws SQLException, IOException, ClassNotFoundException {
        h2Adapter.setupTable("scripts/tables.sql");
        h2Adapter.setupTable("scripts/data.sql");
        List<Map<String, Object>> query = h2Adapter.query(null);
        Assert.assertNotNull(query);
        Assert.assertEquals(0, query.size());
        query = h2Adapter.query("");
        Assert.assertNotNull(query);
        Assert.assertEquals(0, query.size());
        query = h2Adapter.query("select * from account");
        Assert.assertNotNull(query);
        Assert.assertTrue(query.size() > 0);
        Connection con;
        String q = "Update account set balance = 10 where account_number='123456'";
        boolean update = h2Adapter.update((con = h2Adapter.getConnection()), q);
        Assert.assertFalse(update);
        q = "Update query set balance = 10 where account_number='1234567'";
        try {
            h2Adapter.update((con), q);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        q = "Update account set balance = 10 where account_number='1234567'";
        update = h2Adapter.update((con), q);
        Assert.assertTrue(update);
        con.close();
    }

    @Test(expected = SQLException.class)
    public void updateFailure1() throws SQLException {
        h2Adapter.update(null, null);
    }

    @Test(expected = SQLException.class)
    public void updateFailure2() throws SQLException {
        h2Adapter.update(null, "");
    }

    @Test(expected = SQLException.class)
    public void updateFailure3() throws SQLException, ClassNotFoundException {
        h2Adapter.update(h2Adapter.getConnection(), null);
    }
}