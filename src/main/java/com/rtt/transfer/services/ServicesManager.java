package com.rtt.transfer.services;

import com.rtt.transfer.services.services.AccountsService;
import com.rtt.transfer.services.services.AccountsServiceSimple;
import com.rtt.transfer.services.services.TransactionsService;
import com.rtt.transfer.services.services.TransactionsServiceSimple;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ServicesManager {

    private static TransactionsService transactionsService;
    private static AccountsService accountsService;

    private static EntityManagerFactory entityManagerFactory;


    static void initServices(String persistenceUnitName) {
        transactionsService = new TransactionsServiceSimple();
        accountsService = new AccountsServiceSimple();

        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public static TransactionsService getTransactionsService() {
        return transactionsService;
    }

    public static AccountsService getAccountsService() {
        return accountsService;
    }

    public static EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}