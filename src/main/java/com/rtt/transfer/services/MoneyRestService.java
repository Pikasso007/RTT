package com.rtt.transfer.services;

import com.rtt.transfer.ServicesManager;

import javax.ws.rs.*;

import java.math.BigDecimal;

import static javax.ws.rs.core.MediaType.*;

@Path("/transfer-service")
@Produces(TEXT_PLAIN)
public class MoneyRestService {

    private MoneyServiceSimple moneyServiceSimple = ServicesManager.getMoneyService();

    @GET
    @Path("/health-check")
    public String healthCheck() {
        return "Transfer service is alive!";
    }

    @GET
    @Path("/create-account")
    public String createAccount() {
        return moneyServiceSimple.createAccount();
    }

    @GET
    @Path("/account-inf")
    public String getAccountInformation(@QueryParam("id") String accountId) {
        return moneyServiceSimple.getAccount(accountId).toString();
    }

    @POST
    @Path("/transfer-money")
    public String transfer(@FormParam("accountIdFrom") String accountIdFrom,
                           @FormParam("accountIdTo") String accountIdTo,
                           @FormParam("amount") BigDecimal amount,
                           @FormParam("comment") @DefaultValue("") String comment) {

        return moneyServiceSimple.transfer(accountIdFrom, accountIdTo, amount, comment);
    }
}