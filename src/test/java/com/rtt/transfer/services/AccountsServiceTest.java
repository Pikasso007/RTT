package com.rtt.transfer.services;

import com.rtt.transfer.ServicesManager;
import com.rtt.transfer.TransferBootstrap;
import com.rtt.transfer.model.Account;
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
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AccountsServiceTest {

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
    public void createAndFetchMethodsTest() {
        MultivaluedMap<String, String> requestParams = new MultivaluedStringMap();
        requestParams.add("amount", "100.25");

        String createdAccountId = webTarget.path("rest")
                .path("accounts-service")
                .path("create")
                .request()
                .post(Entity.form(requestParams))
                .readEntity(String.class);

        Account fetchedAccount = webTarget.path("rest")
                .path("accounts-service")
                .path("fetch")
                .queryParam("accountId", createdAccountId)
                .request()
                .get()
                .readEntity(Account.class);

        assertEquals(new Long(createdAccountId), fetchedAccount.getId());
        assertEquals(new BigDecimal("100.25"), fetchedAccount.getAmount());
    }

    @Test
    public void fetchMethodTest_WhenTryToFetchNotExistedAccount_ThenReturnStatus204() {
        Response responseAfterQueryWhereAccountIsNumber = webTarget.path("rest")
                .path("accounts-service")
                .path("fetch")
                .queryParam("accountId", "999")
                .request()
                .get();

        Response responseAfterQueryWhereAccountIsText = webTarget.path("rest")
                .path("accounts-service")
                .path("fetch")
                .queryParam("accountId", "abc")
                .request()
                .get();

        assertEquals(204, responseAfterQueryWhereAccountIsNumber.getStatus());
        assertEquals(204, responseAfterQueryWhereAccountIsText.getStatus());
    }

    @Test
    public void existMethodTest() {
        MultivaluedMap<String, String> requestParams = new MultivaluedStringMap();
        requestParams.add("amount", "100.25");

        String createdAccountId = webTarget.path("rest")
                .path("accounts-service")
                .path("create")
                .request()
                .post(Entity.form(requestParams))
                .readEntity(String.class);

        Boolean existedAccount = webTarget.path("rest")
                .path("accounts-service")
                .path("exist")
                .queryParam("accountId", createdAccountId)
                .request()
                .get()
                .readEntity(Boolean.class);

        Boolean isExistAccountInTextFormat = webTarget.path("rest")
                .path("accounts-service")
                .path("exist")
                .queryParam("accountId", "abc")
                .request()
                .get()
                .readEntity(Boolean.class);

        Boolean isExistAccount = webTarget.path("rest")
                .path("accounts-service")
                .path("exist")
                .queryParam("accountId", "9999")
                .request()
                .get()
                .readEntity(Boolean.class);

        assertTrue(existedAccount);
        assertFalse(isExistAccountInTextFormat);
        assertFalse(isExistAccount);
    }

    @Test
    public void transactionsByAccountIdSearchTest() {
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

        MultivaluedMap<String, String> transactionRequestParams2 = new MultivaluedStringMap();
        transactionRequestParams2.add("accountIdFrom", "999");
        transactionRequestParams2.add("accountIdTo", secondCreatedAccountId);
        transactionRequestParams2.add("amount", "45.25");
        transactionRequestParams2.add("comment", "no comment");

        String createdSecondTransactionIdAfterTransferMoney = webTarget.path("rest")
                .path("transactions-service")
                .path("transfer-money")
                .request()
                .post(Entity.form(transactionRequestParams2))
                .readEntity(String.class);

        List transactionsListForSecondAccount = webTarget.path("rest")
                .path("accounts-service")
                .path("transactions-by-account")
                .queryParam("accountId", secondCreatedAccountId)
                .request()
                .get()
                .readEntity(List.class);

        List transactionsListForFirstAccount = webTarget.path("rest")
                .path("accounts-service")
                .path("transactions-by-account")
                .queryParam("accountId", firstCreatedAccountId)
                .request()
                .get()
                .readEntity(List.class);

        List transactionsListForNotExistedAccount = webTarget.path("rest")
                .path("accounts-service")
                .path("transactions-by-account")
                .queryParam("accountId", "999")
                .request()
                .get()
                .readEntity(List.class);

        assertEquals(0, transactionsListForNotExistedAccount.size());
        assertEquals(1, transactionsListForFirstAccount.size());
        assertEquals(2, transactionsListForSecondAccount.size());
    }

}
