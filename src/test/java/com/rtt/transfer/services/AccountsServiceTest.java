package com.rtt.transfer.services;

import com.rtt.transfer.services.model.Account;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class AccountsServiceTest {

    private static final String TEST_PERSISTENCE_UNIT_NAME = "rtt-test";
    private static final String HTTP_LOCALHOST_ADDRESS = "http://localhost:";

    private WebTarget webTarget;


    @Before
    public void startServer() throws Exception {
        ServicesManager.initServices(TEST_PERSISTENCE_UNIT_NAME);
        TransferBootstrap.startEmbeddedServer();

        Client client = ClientBuilder.newClient();
        webTarget = client.target(HTTP_LOCALHOST_ADDRESS + TransferBootstrap.TOMCAT_PORT);
    }

    @After
    public void stopServer() throws Exception {
        TransferBootstrap.stopEmbeddedServer();
    }

    @Test
    public void createAndGetAccountRestServiceTest() {
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
                .queryParam("id", createdAccountId)
                .request()
                .get()
                .readEntity(Account.class);

        assertEquals(new Long(createdAccountId), fetchedAccount.getId());
        assertEquals(new BigDecimal("100.25"), fetchedAccount.getAmount());
    }

    @Test
    public void whenFetchNotExistedAccount_ThenReturn204Status() {
        Response response = webTarget.path("rest")
                .path("accounts-service")
                .path("fetch")
                .queryParam("id", "999")
                .request()
                .get();

        assertEquals(204, response.getStatus());
    }

}
