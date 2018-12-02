package com.rtt.transfer.services.services;

import com.rtt.transfer.services.ServicesManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.math.BigDecimal;

@Path("/transactions-service")
@Produces(MediaType.TEXT_PLAIN)
public class TransactionsRestService {

    private TransactionsService transactionsService = ServicesManager.getTransactionsService();

    @GET
    @Path("/health-check")
    public String healthCheck() {
        return "Transaction service is alive!";
    }

    @POST
    @Path("/transfer-money")
    public String transfer(@FormParam("accountIdFrom") String accountIdFrom,
                           @FormParam("accountIdTo") String accountIdTo,
                           @FormParam("amount") BigDecimal amount,
                           @FormParam("comment") @DefaultValue("") String comment) {

        return transactionsService.transfer(accountIdFrom, accountIdTo, amount, comment);
    }
}