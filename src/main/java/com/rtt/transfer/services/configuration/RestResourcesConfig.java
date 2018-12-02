package com.rtt.transfer.services.configuration;

import com.rtt.transfer.services.services.AccountsRestService;
import com.rtt.transfer.services.services.TransactionsRestService;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class RestResourcesConfig extends Application{

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(TransactionsRestService.class);
        classes.add(AccountsRestService.class);
        return classes;
    }
}