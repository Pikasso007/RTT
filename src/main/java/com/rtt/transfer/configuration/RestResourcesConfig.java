package com.rtt.transfer.configuration;

import com.rtt.transfer.services.MoneyRestService;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class RestResourcesConfig extends Application{

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(MoneyRestService.class);
        return classes;
    }
}