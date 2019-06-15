package com.revolut.services.moneycontrol.handler;

import com.revolut.services.moneycontrol.exception.ServiceException;
import com.revolut.services.moneycontrol.model.Account;
import com.revolut.services.moneycontrol.model.Constants;
import com.revolut.services.moneycontrol.model.Status;
import com.revolut.services.moneycontrol.service.MoneyControlService;
import com.revolut.services.moneycontrol.util.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Http Handler (Controller) to Manage (Retrieve) Account Details from the Application
 */
public class ManageAccountHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(ManageAccountHandler.class.getName());
    private final BaseHttpHandler baseHttpHandler;
    private final MoneyControlService moneyControlService;

    public ManageAccountHandler(BaseHttpHandler baseHttpHandler, MoneyControlService moneyControlService) {
        this.baseHttpHandler = baseHttpHandler;
        this.moneyControlService = moneyControlService;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            baseHttpHandler.handle(httpExchange);
            if (Constants.HTTP_GET.equals(httpExchange.getRequestMethod())) {
                Optional<String> accountNumOptional = Utils.extractPathParamValue(httpExchange, Constants.ACCOUNT_PATH);
                if (accountNumOptional.isPresent()) {
                    //Retrieving a given Account
                    final Optional<Account> accountDetails = moneyControlService.getAccountDetails(accountNumOptional.get());
                    if (accountDetails.isPresent()) {
                        Utils.generateResponse(httpExchange, Utils.writeObjAsString(accountDetails.get()), Status.OK);
                    } else {
                        Utils.generateResponse(httpExchange, Constants.ACCOUNT_NOT_FOUND_ERROR_MESSAGE +
                                accountNumOptional.get(), Status.NOT_FOUND);
                    }
                } else {
                    //Retrieving all Accounts
                    final Collection<Account> allccountDetails = moneyControlService.getAllAccountDetails();
                    Utils.generateResponse(httpExchange, Utils.writeObjAsString(allccountDetails), Status.OK);
                }
            } else {
                Utils.generateResponse(httpExchange, Constants.HTTP_METHOD_NOT_ALLOWED_ERROR_MESSAGE, Status.METHOD_NOT_ALLOWED);
            }
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Service Exception Encountered in AccountHandler : ", e);
            Utils.generateResponse(httpExchange, Constants.ACCOUNT_SERVICE_ERROR_MESSAGE, Status.FAIL);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception Encountered in PersistAccountHandler : ", e);
            Utils.generateResponse(httpExchange, Constants.GENERAL_ERROR_MESSAGE, Status.FAIL);
        }
    }
}
