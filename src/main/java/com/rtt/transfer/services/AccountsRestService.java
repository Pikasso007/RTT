package com.rtt.transfer.services;

import com.rtt.transfer.ServicesManager;
import com.rtt.transfer.model.Account;
import com.rtt.transfer.model.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;

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
    public Account fetchAccountInformation(@QueryParam("accountId") String accountId) {
        return accountsService.getAccount(accountId);
    }

    @GET
    @Path("/transactions-by-account")
    public List<Transaction> getTransactionsByAccountId(@QueryParam("accountId") String accountId) {
        return accountsService.getTransactionsByAccountId(accountId);
    }

    @GET
    @Path("/exist")
    public boolean isExistAccount(@QueryParam("accountId") String accountId) {
        return accountsService.isExistAccount(accountId);
    }
}