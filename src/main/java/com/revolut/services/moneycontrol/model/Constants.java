package com.revolut.services.moneycontrol.model;

/**
 * Constants for Money Control Application
 * Handles :
 * DB script file names
 * DB connection Details, Driver names
 * DB Field Names
 * Http Error Messages, Context Names,
 * Config values like Port
 */
public class Constants {

    // DB Related Constants
    public static final String H2_JDBC_DRIVER = "org.h2.Driver";
    public static final String H2_DB_URL = "jdbc:h2:mem:MONEYCONTROL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false";
    public static final String H2_USER = "sa";
    public static final String H2_PASS = "";
    public static final String TABLES_SCRIPT = "scripts/tables.sql";
    public static final String DATA_SCRIPT = "scripts/data.sql";
    public static final String ACCOUNT_NUM_LABEL = "ACCOUNT_NUMBER";
    public static final String ACCOUNT_NAME_LABEL = "NAME";
    public static final String ACCOUNT_BALANCE_LABEL = "BALANCE";
    public static final String ACCOUNT_CURRENCY_LABEL = "CURRENCY";
    public static final String ACCOUNT_VERSION_LABEL = "VERSION";
    public static final String ACCOUNT_CREATION_DATE_LABEL = "CREATION_DATE";

    // Web Service Related Constants
    public static final int SERVER_PORT = 8080;
    public static final int TEST_SERVER_PORT = 8090;
    public static final String LOCALHOST_URL = "http://127.0.0.1:";
    public static final String SLASH = "/";
    public static final String TRANSFER_PATH = "transfer";
    public static final String ACCOUNT_PATH = "accounts";
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";

    public static final String MONEY_TRANSFER_REQUEST_REQUIRED = "MoneyTransfer Request is required";
    public static final String MONEY_TRANSFER_REQUEST_FROM_ACC_NUM_REQUIRED = "fromAccountNum is required";
    public static final String MONEY_TRANSFER_REQUEST_TO_ACC_NUM_REQUIRED = "toAccountNum is required";
    public static final String MONEY_TRANSFER_REQUEST_CURRENCY_REQUIRED = "currency is required";
    public static final String MONEY_TRANSFER_REQUEST_ACCOUNT_NUM_LENGTH = "Account Number must be 7 digits long";
    public static final String MONEY_TRANSFER_REQUEST_AMOUNT_REQUIRED = "amount is required";

    public static final String MONEY_TRANSFER_REQ_PARSE_ERROR_MESSAGE = "Money Transfer Request parse error";
    public static final String MONEY_TRANSFER__ERROR_MESSAGE = "Error in Money Transfer.";
    public static final String GENERAL_ERROR_MESSAGE = "Error encountered. Please try after some time.";
    public static final String Validation_ERROR_MESSAGE = "Request validations failed : ";
    public static final String ACCOUNT_SERVICE_ERROR_MESSAGE = "Service Error encountered.";
    public static final String ACCOUNT_NOT_FOUND_ERROR_MESSAGE = "Account Not found, accountNumber : ";
    public static final String HTTP_METHOD_NOT_ALLOWED_ERROR_MESSAGE = "Method not allowed";
    public static final String OK_RESPONSE_MESSAGE = "OK!! Working Fine";
}
