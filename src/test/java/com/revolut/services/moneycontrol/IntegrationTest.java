package com.revolut.services.moneycontrol;

import com.revolut.services.moneycontrol.app.Application;
import com.revolut.services.moneycontrol.model.*;
import com.revolut.services.moneycontrol.model.Error;
import com.revolut.services.moneycontrol.util.Utils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class IntegrationTest {

    private final static CountDownLatch latch = new CountDownLatch(1);

    @BeforeClass
    public static void setup() {
        Application testApp = Application.getInstance();

        Thread thread = new Thread(() -> {
            testApp.startTestServer();
            try {
                latch.await();
                testApp.stopServer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        thread.start();
    }

    @AfterClass
    public static void teardown() {
        latch.countDown();
    }

    @Test
    public void getAccountDetailsAPISuccess() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(Constants.LOCALHOST_URL + Constants.TEST_SERVER_PORT+Constants.SLASH+
                Constants.ACCOUNT_PATH+Constants.SLASH+"1234568");
        CloseableHttpResponse response = httpClient.execute(get);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatusLine());
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        Assert.assertNotNull(body);
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        Account account =  Utils.readObject(serviceResponse.getPayload().toString(),Account.class);
        Assert.assertNotNull(account);
        Assert.assertEquals("1234568", account.getAccountNumber());
        Assert.assertEquals("User2", account.getName());
        Assert.assertEquals("50.0", account.getBalance().toString());
        Assert.assertEquals(1, account.getVersion());
    }

    @Test
    public void getAccountDetailsAPINotFound() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(Constants.LOCALHOST_URL + Constants.TEST_SERVER_PORT +Constants.SLASH+
                Constants.ACCOUNT_PATH+Constants.SLASH+"1234565");
        CloseableHttpResponse response = httpClient.execute(get);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatusLine());
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        Assert.assertNull(serviceResponse.getPayload());
    }

    @Test
    public void getAccountDetailsAPI1() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(Constants.LOCALHOST_URL + Constants.TEST_SERVER_PORT);
        CloseableHttpResponse response = httpClient.execute(get);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatusLine());
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        Assert.assertNotNull(serviceResponse.getPayload());
        Assert.assertEquals("OK!! Working Fine", serviceResponse.getPayload());
    }

    @Test
    public void getAccountDetailsAPIMethodNotSupported() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost get = new HttpPost(Constants.LOCALHOST_URL + Constants.TEST_SERVER_PORT +Constants.SLASH+
                Constants.ACCOUNT_PATH+Constants.SLASH+"1234565");
        CloseableHttpResponse response = httpClient.execute(get);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatusLine());
        Assert.assertEquals(405, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        Assert.assertNull(serviceResponse.getPayload());
    }

    @Test
    public void getAllAccountDetailsAPISuccess() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(Constants.LOCALHOST_URL + Constants.TEST_SERVER_PORT +
                Constants.SLASH+ Constants.ACCOUNT_PATH);
        CloseableHttpResponse response = httpClient.execute(get);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatusLine());
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        Assert.assertNotNull(body);
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        List<Map<String,Object>> accounts = Utils.readObject(serviceResponse.getPayload().toString(),List.class);
        Assert.assertNotNull(accounts);
        Assert.assertTrue(accounts.size() >0);
        Map<String, Object> account1 = accounts.get(0);
        Assert.assertEquals("1234567", account1.get("accountNumber"));
        Assert.assertEquals("User1", account1.get("name"));
        Assert.assertEquals(100.0, account1.get("balance"));
        Assert.assertEquals(1, account1.get("version"));
        Map<String, Object> account2 = accounts.get(1);
        Assert.assertEquals("1234568", account2.get("accountNumber"));
        Assert.assertEquals("User2", account2.get("name"));
        Assert.assertEquals(50.0, account2.get("balance"));
        Assert.assertEquals(1, account2.get("version"));
    }


    private CloseableHttpResponse processMoneyTransferRequest(MoneyTransferRequest request) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(Constants.LOCALHOST_URL + Constants.TEST_SERVER_PORT +
                Constants.SLASH+ Constants.TRANSFER_PATH);
        String s = Utils.writeObjAsString(request);
        StringEntity entity = new StringEntity(s);
        post.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(post);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatusLine());
        return response;
    }
    @Test
    public void getMoneyTransferAPIFailure1() throws IOException {
        MoneyTransferRequest request = new MoneyTransferRequest("1234569","1234568",10.0, "USD");
        CloseableHttpResponse response = processMoneyTransferRequest(request);
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        Assert.assertNull(serviceResponse.getPayload());
        String str = "Account Not found : "+request.getFromAccountNum();
        Assert.assertEquals(str, ((Error)serviceResponse.getErrors().get(0)).getDescription());
    }

    @Test
    public void getMoneyTransferAPIFailure2() throws IOException {
        MoneyTransferRequest request = new MoneyTransferRequest("1234567","1234569",10.0, "USD");
        CloseableHttpResponse response = processMoneyTransferRequest(request);
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        Assert.assertNull(serviceResponse.getPayload());
        String str = "Account Not found : "+request.getToAccountNum();
        Assert.assertEquals(str, ((Error)serviceResponse.getErrors().get(0)).getDescription());
    }

    @Test
    public void getMoneyTransferAPIFailure3() throws IOException {
        MoneyTransferRequest request = new MoneyTransferRequest("1234567","1234568",2000.0, "USD");
        CloseableHttpResponse response = processMoneyTransferRequest(request);
        Assert.assertEquals(403, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        Assert.assertNull(serviceResponse.getPayload());
        String str = "Insufficient Balance for money Transfer from : "+request.getFromAccountNum()
                +" to: "+request.getToAccountNum();
        Assert.assertEquals(str, ((Error)serviceResponse.getErrors().get(0)).getDescription());
    }

    @Test
    public void getMoneyTransferAPIFailure4() throws IOException {
        MoneyTransferRequest request = new MoneyTransferRequest("1234567","1234568",00.0, "GBP");
        CloseableHttpResponse response = processMoneyTransferRequest(request);
        Assert.assertEquals(403, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        Assert.assertNull(serviceResponse.getPayload());
        String str = "Cross currency transfer not supported for money Transfer from : "+request.getFromAccountNum()
                +" to: "+request.getToAccountNum();
        Assert.assertEquals(str, ((Error)serviceResponse.getErrors().get(0)).getDescription());
    }

    @Test
    public void getMoneyTransferAPIMethodNotSupported() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(Constants.LOCALHOST_URL + Constants.TEST_SERVER_PORT +
                Constants.SLASH+ Constants.TRANSFER_PATH);
        CloseableHttpResponse response = httpClient.execute(get);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatusLine());
        Assert.assertEquals(405, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        Assert.assertNull(serviceResponse.getPayload());
    }


    @Test
    public void getMoneyTransferAPISuccess() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(Constants.LOCALHOST_URL + Constants.TEST_SERVER_PORT +
                Constants.SLASH+ Constants.TRANSFER_PATH);
        MoneyTransferRequest request = new MoneyTransferRequest("1234567","1234568",10.0, "USD");
        String s = Utils.writeObjAsString(request);
        StringEntity entity = new StringEntity(s);
        post.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(post);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatusLine());
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        MoneyTransferRecord result = Utils.readObject(serviceResponse.getPayload().toString(),MoneyTransferRecord.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(request.getFromAccountNum(),result.getFromAccountNum());
        Assert.assertEquals(request.getToAccountNum(),result.getToAccountNum());
        Assert.assertEquals(request.getAmount(),result.getAmount());
        Assert.assertTrue(result.getStatus());
        Assert.assertEquals("Success",result.getReason());
    }

    @Test
    public void getMoneyTransferAPIValidationError() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(Constants.LOCALHOST_URL + Constants.TEST_SERVER_PORT +
                Constants.SLASH+ Constants.TRANSFER_PATH);
        MoneyTransferRequest request = new MoneyTransferRequest();
        String s = Utils.writeObjAsString(request);
        StringEntity entity = new StringEntity(s);
        post.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(post);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatusLine());
        Assert.assertEquals(400, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        Assert.assertNotNull(serviceResponse);
        Assert.assertNotNull(serviceResponse.getErrors());
        Assert.assertTrue(serviceResponse.getErrors().size() > 0);
        Error error = (Error)serviceResponse.getErrors().get(0);
        Assert.assertTrue(error.getDescription().contains(Constants.MONEY_TRANSFER_REQUEST_FROM_ACC_NUM_REQUIRED));
        Assert.assertTrue(error.getDescription().contains(Constants.MONEY_TRANSFER_REQUEST_TO_ACC_NUM_REQUIRED));
        Assert.assertTrue(error.getDescription().contains(Constants.MONEY_TRANSFER_REQUEST_AMOUNT_REQUIRED));
    }

    @Test
    public void getMoneyTransferAPIValidationError2() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(Constants.LOCALHOST_URL + Constants.TEST_SERVER_PORT +
                Constants.SLASH+ Constants.TRANSFER_PATH);
        StringEntity entity = new StringEntity("");
        post.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(post);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatusLine());
        Assert.assertEquals(400, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        Assert.assertNotNull(serviceResponse);
        Assert.assertNotNull(serviceResponse.getErrors());
        Assert.assertTrue(serviceResponse.getErrors().size() > 0);
        Error error = (Error) serviceResponse.getErrors().get(0);
        Assert.assertTrue(error.getDescription().contains(Constants.MONEY_TRANSFER_REQUEST_REQUIRED));
    }

    @Test
    public void getMoneyTransferAPIValidationError3() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(Constants.LOCALHOST_URL + Constants.TEST_SERVER_PORT +
                Constants.SLASH+ Constants.TRANSFER_PATH);
        MoneyTransferRequest request = new MoneyTransferRequest("12345","1234568",10.0, "USD");
        String s = Utils.writeObjAsString(request);
        StringEntity entity = new StringEntity(s);
        post.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(post);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatusLine());
        Assert.assertEquals(400, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getEntity());
        String body = Utils.processStream(response.getEntity().getContent());
        ServiceResponse serviceResponse = Utils.readObject(body, ServiceResponse.class);
        Assert.assertNotNull(serviceResponse);
        Assert.assertNotNull(serviceResponse.getErrors());
        Assert.assertTrue(serviceResponse.getErrors().size() > 0);
        Error error = (Error)serviceResponse.getErrors().get(0);
        Assert.assertTrue(error.getDescription().contains(Constants.MONEY_TRANSFER_REQUEST_ACCOUNT_NUM_LENGTH));
    }
}
