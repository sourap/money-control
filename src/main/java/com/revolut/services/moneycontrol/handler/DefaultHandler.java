package com.revolut.services.moneycontrol.handler;

import com.revolut.services.moneycontrol.model.Constants;
import com.revolut.services.moneycontrol.model.Status;
import com.revolut.services.moneycontrol.util.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Default Http Handler for health check of Http Server
 */
public class DefaultHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) {

        Utils.generateResponse(httpExchange, Constants.OK_RESPONSE_MESSAGE, Status.OK);
    }
}
