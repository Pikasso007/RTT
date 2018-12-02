package com.rtt.transfer.services.services;

import com.rtt.transfer.services.ServicesManager;
import com.rtt.transfer.services.model.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

@Path("/accounts-service")
@Produces(MediaType.APPLICATION_JSON)
public class AccountsRestService {

    private AccountsService accountsService = ServicesManager.getAccountsService();

    @POST
    @Path("/create")
    public String createAccount(@FormParam("amount") @DefaultValue("0") BigDecimal amount) {
        return accountsService.createAccountAndGetId(amount);
    }

    @GET
    @Path("/fetch")
    public Account fetchAccountInformation(@QueryParam("id") String accountId) {
        return accountsService.getAccount(accountId);
    }
}