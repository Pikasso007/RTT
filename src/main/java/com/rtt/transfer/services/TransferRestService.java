package com.rtt.transfer.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.*;

@Path("/transfer-service")
public class TransferRestService {

    @GET
    @Path("/health-check")
    @Produces(TEXT_PLAIN)
    public String healthCheck() {
        return "Transfer service is alive!";
    }
}