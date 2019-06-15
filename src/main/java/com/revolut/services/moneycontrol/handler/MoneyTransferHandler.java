package com.revolut.services.moneycontrol.handler;

import com.revolut.services.moneycontrol.exception.ServiceException;
import com.revolut.services.moneycontrol.model.Constants;
import com.revolut.services.moneycontrol.model.MoneyTransferRecord;
import com.revolut.services.moneycontrol.model.MoneyTransferRequest;
import com.revolut.services.moneycontrol.model.Status;
import com.revolut.services.moneycontrol.service.MoneyControlService;
import com.revolut.services.moneycontrol.util.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Http Handler (Controller) to Manage Money Transfer
 */
public class MoneyTransferHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(MoneyTransferHandler.class.getName());
    private final BaseHttpHandler baseHttpHandler;
    private final MoneyControlService moneyControlService;
    private final Validator validator;

    public MoneyTransferHandler(BaseHttpHandler baseHttpHandler, MoneyControlService moneyControlService) {
        this.baseHttpHandler = baseHttpHandler;
        this.moneyControlService = moneyControlService;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            baseHttpHandler.handle(httpExchange);
            if (Constants.HTTP_POST.equals(httpExchange.getRequestMethod())) {
                //Extract Request Object
                MoneyTransferRequest request = Utils.extractPayload(httpExchange, MoneyTransferRequest.class);
                List<String> errorList = validateRequest(request, httpExchange);
                if (Objects.nonNull(errorList) && errorList.size() > 0) {
                    //Validation Errors Found
                    LOGGER.log(Level.SEVERE, "Validation errors Encountered in MoneyTransferHandler : "+errorList);
                    Utils.generateResponse(httpExchange, Constants.Validation_ERROR_MESSAGE + errorList, Status.BAD_REQUEST);
                }else{
                    // Delegate money transfer to Service Layer
                    MoneyTransferRecord result = moneyControlService.transferMoney(request.getFromAccountNum(),
                            request.getToAccountNum(), request.getAmount(), request.getCurrency());
                    if(!result.getStatus()){
                        //Set Failure Status Codes Appropriately
                        processFailure(result, httpExchange);
                    }else{
                        Utils.generateResponse(httpExchange, Utils.writeObjAsString(result), Status.OK);
                    }
                }
            }else{
                Utils.generateResponse(httpExchange, Constants.HTTP_METHOD_NOT_ALLOWED_ERROR_MESSAGE, Status.METHOD_NOT_ALLOWED);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException Exception Encountered in MoneyTransferHandler : ", e);
            Utils.generateResponse(httpExchange, Constants.MONEY_TRANSFER_REQ_PARSE_ERROR_MESSAGE, Status.BAD_REQUEST);
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Service Exception Encountered in MoneyTransferHandler : ", e);
            Utils.generateResponse(httpExchange, Constants.MONEY_TRANSFER__ERROR_MESSAGE, Status.FAIL);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception Encountered in MoneyTransferHandler : ", e);
            Utils.generateResponse(httpExchange, Constants.GENERAL_ERROR_MESSAGE, Status.FAIL);
        }
    }

    private List<String> validateRequest(MoneyTransferRequest request, HttpExchange httpExchange) {
        if(Objects.isNull(request)){
            Utils.generateResponse(httpExchange, Constants.Validation_ERROR_MESSAGE +
                    Constants.MONEY_TRANSFER_REQUEST_REQUIRED, Status.BAD_REQUEST);
            return null;
        }
        // Validate and Retrieve Errors
        return validator.validate(request).
                stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    }

    private void processFailure(MoneyTransferRecord result, HttpExchange httpExchange) {
        if(result.getReason().contains("Insufficient")){
            Utils.generateResponse(httpExchange, result.getReason(), Status.FORBIDDEN);
        }else if(result.getReason().contains("currency")){
            Utils.generateResponse(httpExchange, result.getReason(), Status.FORBIDDEN);
        }else if(result.getReason().contains("Not")){
            Utils.generateResponse(httpExchange, result.getReason(), Status.NOT_FOUND);
        }else{
            Utils.generateResponse(httpExchange, result.getReason(), Status.FAIL);
        }
    }
}
