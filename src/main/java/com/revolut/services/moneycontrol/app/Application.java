package com.revolut.services.moneycontrol.app;

import com.revolut.services.moneycontrol.adapter.DBAdapter;
import com.revolut.services.moneycontrol.adapter.impl.H2Adapter;
import com.revolut.services.moneycontrol.dao.MoneyControlDAO;
import com.revolut.services.moneycontrol.dao.impl.InMemoryMoneyControlDAOImpl;
import com.revolut.services.moneycontrol.handler.BaseHttpHandler;
import com.revolut.services.moneycontrol.handler.DefaultHandler;
import com.revolut.services.moneycontrol.handler.ManageAccountHandler;
import com.revolut.services.moneycontrol.handler.MoneyTransferHandler;
import com.revolut.services.moneycontrol.model.Constants;
import com.revolut.services.moneycontrol.server.BasicHttpServerProvider;
import com.revolut.services.moneycontrol.service.MoneyControlService;
import com.revolut.services.moneycontrol.service.impl.MoneyControlServiceImpl;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main Application to Instantiate Http Server and register context paths
 * Instantiates using Singleton pattern
 * Call startServer() to start the server
 * Call stopServer() to Terminate the HttpServer gracefully
 */
public class Application {

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());
    private HttpServer server;

    private Application() {
    }

    /**
     * Get Instance Of Money Control Application
     *
     * @return Application Instance
     */
    public static Application getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Run the Application in Server
     */
    public void startServer() {
        this.server = instantiateHttpServer(Constants.SERVER_PORT);
        init(server);
    }

    /**
     * Run the Application in Test Server
     */
    public void startTestServer() {
        this.server = instantiateHttpServer(Constants.TEST_SERVER_PORT);
        init(server);
    }

    private void init(HttpServer server) {
        if (Objects.nonNull(server)) {

            MoneyControlDAO moneyControlDAO = getMoneyControlDAO();
            MoneyControlService moneyControlService = getMoneyControlService(moneyControlDAO);
            BaseHttpHandler baseHttpHandler = getBaseHttpHandler();

            //Register Paths
            server.createContext(Constants.SLASH, getDefaultHttpHandler());
            server.createContext(Constants.SLASH + Constants.TRANSFER_PATH,
                    getMoneyTransferHandler(baseHttpHandler, moneyControlService));
            server.createContext(Constants.SLASH + Constants.ACCOUNT_PATH,
                    getManageAccounteHandler(baseHttpHandler, moneyControlService));

            server.start();
        }
    }

    private MoneyControlDAO getMoneyControlDAO() {
        LOGGER.info("Creating InMemoryMoneyControlDAOImpl Bean");
        return new InMemoryMoneyControlDAOImpl(getH2Adapter());
    }

    private DBAdapter getH2Adapter() {
        LOGGER.info("Creating H2Adapter Bean");
        return new H2Adapter();
    }

    private MoneyControlService getMoneyControlService(MoneyControlDAO moneyControlDAO) {
        LOGGER.info("Creating MoneyControlServiceImpl Bean");
        return new MoneyControlServiceImpl(moneyControlDAO);
    }

    private BaseHttpHandler getBaseHttpHandler() {
        LOGGER.info("Creating BaseHttpHandler Bean");
        return new BaseHttpHandler();
    }

    private DefaultHandler getDefaultHttpHandler() {
        LOGGER.info("Creating DefaultHandler Bean");
        return new DefaultHandler();
    }

    private MoneyTransferHandler getMoneyTransferHandler(BaseHttpHandler baseHttpHandler, MoneyControlService moneyControlService) {
        LOGGER.info("Creating MoneyTransferHandler Bean");
        return new MoneyTransferHandler(baseHttpHandler, moneyControlService);
    }

    private ManageAccountHandler getManageAccounteHandler(BaseHttpHandler baseHttpHandler, MoneyControlService moneyControlService) {
        LOGGER.info("Creating ManageAccountHandler Bean");
        return new ManageAccountHandler(baseHttpHandler, moneyControlService);
    }

    private HttpServer instantiateHttpServer(int port) {
        LOGGER.info("Creating HttpServer Bean");
        HttpServer httpServer = null;
        BasicHttpServerProvider provider = BasicHttpServerProvider.newInstance();
        try {
            httpServer = provider.createHttpServer(new InetSocketAddress(port));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException encountered in instantiateHttpServer Bean : " + e);
        }
        return httpServer;
    }

    /**
     * Stop the Server
     */
    public void stopServer() {
        server.stop(0);
    }

    private static class Holder {
        private static final Application INSTANCE = new Application();
    }
}
