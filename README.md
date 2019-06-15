###################### MoneyTransfer API ######################

HTTP-based app for Money Transfer.


How to Run - Build - using maven - 
    
    mvn clean package 

**Launcher class** 
>ccom.revolut.services.moneycontrol.app

**JavaDocs Generated**
>http://localhost:63342/money-control/target/apidocs/index.html

**Scripts**
> scripts/tables.sql
Table creation DDL sql  
> scripts/data.sql
Data population DML sql

**Tables**
ACCOUNT
    * ACCOUNT_NUMBER VARCHAR(7)  PRIMARY_KEY
    * NAME VARCHAR(30)
    * BALANCE Double
    * CURRENCY VARCHAR(10)
    * VERSION NUMBER(4)
    * CREATION_DATE TIMESTAMP(1)

**API Details**

* http://localhost:8080/transfer POST
 
Request Payload :
    
     {
     	"fromAccountNum":"String",
     	"toAccountNum":"String",
     	"amount" : double,
     	"currency" " "String"
     }
   Response Payload :
    
    {
    	"status" : "String",
    	"errors" : [],
    	"payload" : {
                    		"id" : "String",
                    		"fromAccountNum" : "String",
                    		"toAccountNum" : "String",
                    		"amount" : double,
                    		"currency" " "String",
                    		"status" : boolean,
                    		"reason" : "String",
                    		"creationDate" : "timestamp"
                    	}
    }




* http://localhost:8080/accounts/{accountNumber}/ GET

    Response Payload -


                        {
                                "status" : "String",
                                "errors" : [],
                                "payload" : {
                                                        "accountNumber" : "String",
                                                        "name" : "String",
                                                        "balance" : double,
                                                        "currency" " "String",
                                                        "version" : integer,
                                                        "creationDate" : "timestamp"
                                                    }
                            }         
    
* http://localhost:8080/accounts/ GET

    Response Payload -
  
            
            {
                    "status" : "String",
                    "errors" : [],
                    "payload" : 
                    [{
                                 "accountNumber" : "String",
                                 "name" : "String",
                                 "balance" : double,
                                 "currency" " "String",
                                 "version" : integer,
                                 "creationDate" : "timestamp"
                             },
                             {
                                         "accountNumber" : "String",
                                         "name" : "String",
                                         "balance" : double,
                                         "currency" " "String",
                                         "version" : integer,
                                         "creationDate" : "timestamp"
                                     }
                    ]
                }



Technical Details -

    
    Development - Java 8
    Unit test - Junit,Mockito, 
    Integration Test - ApacheHttpClient
    Build - Maven 


Data Model

    Account, MoneyTransferRequest, MoneyTransferRecord

Architecture - N-layered 

    DAO Layer, Service Layer, Service Interface(Controller)

Implementation -

    * DAO Layer - In-memory DAO implementation is done using H2DB.
    * Services Layer - Facilitates the data to the controller
    * Service Interface(Controller)- Exposes REST interfaces
    * Server - JDK Implementation of HttpServer


**Thoughts and Considerations**
 
*  _Functional Requirements_ 

    * RESTFUL API for money transfer between accounts - Sync Process.
    * Can be invoked on behalf on end User. System allows money Transfer only between accounts onboarded in the System.
    * Tables and Data are loaded in Data Store(H2 DB) using scripts (scripts directory) , maintained in-memory when application starts.
    * Adding accounts to System through API not supported. However, It can be done by adding DML SQLs to scripts/data.sql
    * Only fetching account is supported.
    * Account Numbers are 7 digit long.
    * Cross currency money transfer not supported.
    * Money Transfers scheduling is not supported. 

* _Non-Functional Requirements_

    * Spring not Used.
    * Datastore run in-memory (In Memory H2 DB).
    * Executable as Standalone program without any container/server.
    * Integration Tests (main.test.java.com.revolut.services.moneycontrol.IntegrationTest) demonstrates the API works as expected.
    * No Authentication implemented.

**Controllers/Handlers**

* _ManageAccount Handler_
>com.revolut.services.moneycontrol.handler.ManageAccountHandler
 
     * Http Handler used for retrieving Account Details
     * Returns 200 for Successful fetch of Account
     * Returns 404 for Account not found in system
     * Returns 405 for when invoked with HTTP method other than GET
     * Returns 500 for Exceptions from Service Layer or other Exceptions

* _MoneyTransfer Handler_
>com.revolut.services.moneycontrol.handler.MoneyTransferHandler
 
     * Http Handler used for Money Transfer
     * Returns 200 for Account not found in system
     * Returns 400 for Bad Request (Required Request Payload not provided)
     * Returns 400 for Bad Request (Account Number length not equals to 7)
     * Returns 403 for Cross currency Money Transfer not supported
     * Returns 403 for Insufficient funds
     * Returns 404 for Account not found in system
     * Returns 405 for when invoked with HTTP method other than POST
     * Returns 500 for Exceptions from Service Layer or other Exceptions

*   _Default Handler_
>com.revolut.services.moneycontrol.handler.DefaultHandler
 
     * Http Handler used for Health Check
     * The Async processing is delegated to QueueProcessor Implementation.
     * Returns 200 if application is UP



**DAO Implementation**
> com.revolut.services.moneycontrol.dao.impl.InMemoryMoneyControlDAOImpl
 
     * Uses InMemory Implementation of DAO layer.
     * Uses DBAdapter implementation to communicate with in-Memory DB
     

**Service Implementation** 
> com.revolut.services.moneycontrol.service.impl.MoneyControlServiceImpl
        
     * Implementation of the Service Layer. 
     * Fetching Data is delegated to the DAO layer Implementation.
 
**Application**
>com.revolut.services.moneycontrol.app.Application
 
     * Main Application to Instantiate Http Server and register context paths
     * For starting and stopping the server gracefully

**Http Server Implementation**
>com.revolut.services.moneycontrol.server

    Since in JDK Implementation of com.sun.net.httpserver.HttpServer 
    and  the existing implementation is copied. 
    However, slight modification is done to the existing JDK 
    implementation for making it handle concurrent requests.
    The existing implementation of Server was Single Threaded.
    Updated it to work with multiple threads. Used Threadpool.