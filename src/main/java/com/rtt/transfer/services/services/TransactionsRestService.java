package com.rtt.transfer.services.services;

import com.rtt.transfer.services.ServicesManager;
import com.rtt.transfer.services.model.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.math.BigDecimal;

@Path("/transactions-service")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsRestService {

    private TransactionsService transactionsService = ServicesManager.getTransactionsService();

    @GET
    @Path("/fetch")
    public Transaction getTransactionById(@QueryParam("transactionId") String transactionId) {
        return transactionsService.getTransactionById(transactionId);
    }

    @POST
    @Path("/deposit")
    public String deposit(@FormParam("accountIdTo") String accountIdTo,
                           @FormParam("amount") BigDecimal amount,
                           @FormParam("comment") @DefaultValue("") String comment) {

        return transactionsService.transferMoneyAndReturnTransactionId(null, accountIdTo, amount, comment);
    }

    @POST
    @Path("/withdraw")
    public String withdraw(@FormParam("accountIdFrom") String accountIdFrom,
                           @FormParam("amount") BigDecimal amount,
                           @FormParam("comment") @DefaultValue("") String comment) {

        return transactionsService.transferMoneyAndReturnTransactionId(accountIdFrom, null, amount, comment);
    }

    @POST
    @Path("/transfer-money")
    public String transfer(@FormParam("accountIdFrom") String accountIdFrom,
                           @FormParam("accountIdTo") String accountIdTo,
                           @FormParam("amount") BigDecimal amount,
                           @FormParam("comment") @DefaultValue("") String comment) {

        return transactionsService.transferMoneyAndReturnTransactionId(accountIdFrom, accountIdTo, amount, comment);
    }
}