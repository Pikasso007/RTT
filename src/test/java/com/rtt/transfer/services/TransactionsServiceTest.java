package com.rtt.transfer.services;

import com.rtt.transfer.services.model.Account;
import com.rtt.transfer.services.model.Transaction;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static com.rtt.transfer.services.services.TransactionsServiceSimple.NOT_INVOKED_TRANSACTION_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TransactionsServiceTest {

    private static final String TEST_PERSISTENCE_UNIT_NAME = "rtt-test";
    private static final String HTTP_LOCALHOST_ADDRESS = "http://localhost:";

    private static WebTarget webTarget;


    @BeforeClass
    public static void startServer() throws Exception {
        ServicesManager.initServices(TEST_PERSISTENCE_UNIT_NAME);
        TransferBootstrap.startEmbeddedServer();

        Client client = ClientBuilder.newClient();
        webTarget = client.target(HTTP_LOCALHOST_ADDRESS + TransferBootstrap.TOMCAT_PORT);
    }

    @AfterClass
    public static void stopServer() throws Exception {
        TransferBootstrap.stopEmbeddedServer();
    }

    @Test
    public void depositAndWithdrawAndTransferMoneyWithNotExistedAccountsTest() {
        MultivaluedMap<String, String> requestParams = new MultivaluedStringMap();
        requestParams.add("accountIdFrom", "1");
        requestParams.add("accountIdTo", "2");
        requestParams.add("amount", "100.25");
        requestParams.add("comment", "no comment");

        String createdTransactionIdAfterDeposit = webTarget.path("rest")
                .path("transactions-service")
                .path("deposit")
                .request()
                .post(Entity.form(requestParams))
                .readEntity(String.class);

        String createdTransactionIdAfterWithdraw = webTarget.path("rest")
                .path("transactions-service")
                .path("withdraw")
                .request()
                .post(Entity.form(requestParams))
                .readEntity(String.class);

        String createdTransactionIdAfterMoneyTransfer = webTarget.path("rest")
                .path("transactions-service")
                .path("transfer-money")
                .request()
                .post(Entity.form(requestParams))
                .readEntity(String.class);

        assertEquals(NOT_INVOKED_TRANSACTION_ID, createdTransactionIdAfterDeposit);
        assertEquals(NOT_INVOKED_TRANSACTION_ID, createdTransactionIdAfterWithdraw);
        assertEquals(NOT_INVOKED_TRANSACTION_ID, createdTransactionIdAfterMoneyTransfer);
    }

    @Test
    public void depositMoneyWithValidAccountsTest() {
        MultivaluedMap<String, String> accountRequestParams = new MultivaluedStringMap();
        accountRequestParams.add("amount", "100.25");

        String firstCreatedAccountId = webTarget.path("rest")
                .path("accounts-service")
                .path("create")
                .request()
                .post(Entity.form(accountRequestParams))
                .readEntity(String.class);

        String secondCreatedAccountId = webTarget.path("rest")
                .path("accounts-service")
                .path("create")
                .request()
                .post(Entity.form(accountRequestParams))
                .readEntity(String.class);

        MultivaluedMap<String, String> transactionRequestParams = new MultivaluedStringMap();
        transactionRequestParams.add("accountIdFrom", firstCreatedAccountId);
        transactionRequestParams.add("accountIdTo", secondCreatedAccountId);
        transactionRequestParams.add("amount", "45.25");
        transactionRequestParams.add("comment", "no comment");

        String createdTransactionIdAfterDeposit = webTarget.path("rest")
                .path("transactions-service")
                .path("deposit")
                .request()
                .post(Entity.form(transactionRequestParams))
                .readEntity(String.class);

        Transaction depositTransaction = webTarget.path("rest")
                .path("transactions-service")
                .path("fetch")
                .queryParam("transactionId", createdTransactionIdAfterDeposit)
                .request()
                .get()
                .readEntity(Transaction.class);

        assertEquals(Long.valueOf(createdTransactionIdAfterDeposit), depositTransaction.getId());
        assertNull(depositTransaction.getAccountIdFrom());
        assertEquals(secondCreatedAccountId, depositTransaction.getAccountIdTo());
        assertEquals("no comment", depositTransaction.getComment());
        assertEquals(new BigDecimal("45.25"), depositTransaction.getAmount());


        Account accountAfterDeposit = webTarget.path("rest")
                .path("accounts-service")
                .path("fetch")
                .queryParam("accountId", secondCreatedAccountId)
                .request()
                .get()
                .readEntity(Account.class);

        assertEquals(new Long(secondCreatedAccountId), accountAfterDeposit.getId());
        assertEquals(new BigDecimal("145.50"), accountAfterDeposit.getAmount());
    }

    @Test
    public void withdrawMoneyWithValidAccountsTest() {
        MultivaluedMap<String, String> accountRequestParams = new MultivaluedStringMap();
        accountRequestParams.add("amount", "100.25");

        String firstCreatedAccountId = webTarget.path("rest")
                .path("accounts-service")
                .path("create")
                .request()
                .post(Entity.form(accountRequestParams))
                .readEntity(String.class);

        String secondCreatedAccountId = webTarget.path("rest")
                .path("accounts-service")
                .path("create")
                .request()
                .post(Entity.form(accountRequestParams))
                .readEntity(String.class);

        MultivaluedMap<String, String> transactionRequestParams = new MultivaluedStringMap();
        transactionRequestParams.add("accountIdFrom", firstCreatedAccountId);
        transactionRequestParams.add("accountIdTo", secondCreatedAccountId);
        transactionRequestParams.add("amount", "45.25");
        transactionRequestParams.add("comment", "no comment");

        String createdTransactionIdAfterWithdraw = webTarget.path("rest")
                .path("transactions-service")
                .path("withdraw")
                .request()
                .post(Entity.form(transactionRequestParams))
                .readEntity(String.class);

        Transaction withdrawTransaction = webTarget.path("rest")
                .path("transactions-service")
                .path("fetch")
                .queryParam("transactionId", createdTransactionIdAfterWithdraw)
                .request()
                .get()
                .readEntity(Transaction.class);

        assertEquals(Long.valueOf(createdTransactionIdAfterWithdraw), withdrawTransaction.getId());
        assertNull(withdrawTransaction.getAccountIdTo());
        assertEquals(firstCreatedAccountId, withdrawTransaction.getAccountIdFrom());
        assertEquals("no comment", withdrawTransaction.getComment());
        assertEquals(new BigDecimal("45.25"), withdrawTransaction.getAmount());


        Account accountAfterDeposit = webTarget.path("rest")
                .path("accounts-service")
                .path("fetch")
                .queryParam("accountId", firstCreatedAccountId)
                .request()
                .get()
                .readEntity(Account.class);

        assertEquals(new Long(firstCreatedAccountId), accountAfterDeposit.getId());
        assertEquals(new BigDecimal("55.00"), accountAfterDeposit.getAmount());
    }

    @Test
    public void transferMoneyBetweenValidAccountsTest() {
        MultivaluedMap<String, String> accountRequestParams = new MultivaluedStringMap();
        accountRequestParams.add("amount", "100.25");

        String firstCreatedAccountId = webTarget.path("rest")
                .path("accounts-service")
                .path("create")
                .request()
                .post(Entity.form(accountRequestParams))
                .readEntity(String.class);

        String secondCreatedAccountId = webTarget.path("rest")
                .path("accounts-service")
                .path("create")
                .request()
                .post(Entity.form(accountRequestParams))
                .readEntity(String.class);

        MultivaluedMap<String, String> transactionRequestParams = new MultivaluedStringMap();
        transactionRequestParams.add("accountIdFrom", firstCreatedAccountId);
        transactionRequestParams.add("accountIdTo", secondCreatedAccountId);
        transactionRequestParams.add("amount", "45.25");
        transactionRequestParams.add("comment", "no comment");

        String createdTransactionIdAfterTransferMoney = webTarget.path("rest")
                .path("transactions-service")
                .path("transfer-money")
                .request()
                .post(Entity.form(transactionRequestParams))
                .readEntity(String.class);

        Transaction transferMoneyTransaction = webTarget.path("rest")
                .path("transactions-service")
                .path("fetch")
                .queryParam("transactionId", createdTransactionIdAfterTransferMoney)
                .request()
                .get()
                .readEntity(Transaction.class);

        assertEquals(Long.valueOf(createdTransactionIdAfterTransferMoney), transferMoneyTransaction.getId());
        assertEquals(firstCreatedAccountId, transferMoneyTransaction.getAccountIdFrom());
        assertEquals(secondCreatedAccountId, transferMoneyTransaction.getAccountIdTo());
        assertEquals("no comment", transferMoneyTransaction.getComment());
        assertEquals(new BigDecimal("45.25"), transferMoneyTransaction.getAmount());


        Account firstAccountAfterTransferMoney = webTarget.path("rest")
                .path("accounts-service")
                .path("fetch")
                .queryParam("accountId", firstCreatedAccountId)
                .request()
                .get()
                .readEntity(Account.class);

        assertEquals(new Long(firstCreatedAccountId), firstAccountAfterTransferMoney.getId());
        assertEquals(new BigDecimal("55.00"), firstAccountAfterTransferMoney.getAmount());

        Account secondAccountAfterTransferMoney = webTarget.path("rest")
                .path("accounts-service")
                .path("fetch")
                .queryParam("accountId", secondCreatedAccountId)
                .request()
                .get()
                .readEntity(Account.class);

        assertEquals(new Long(secondCreatedAccountId), secondAccountAfterTransferMoney.getId());
        assertEquals(new BigDecimal("145.50"), secondAccountAfterTransferMoney.getAmount());
    }

    @Test
    public void fetchMethodTest_WhenTryToFetchNotExistedTransaction_ThenReturnStatus204() {
        Response responseAfterQueryWhereTransactionIsNumber = webTarget.path("rest")
                .path("transactions-service")
                .path("fetch")
                .queryParam("transactionId", "999")
                .request()
                .get();

        Response responseAfterQueryWhereTransactionIsText = webTarget.path("rest")
                .path("transactions-service")
                .path("fetch")
                .queryParam("transactionId", "abc")
                .request()
                .get();

        assertEquals(204, responseAfterQueryWhereTransactionIsNumber.getStatus());
        assertEquals(204, responseAfterQueryWhereTransactionIsText.getStatus());
    }

}
