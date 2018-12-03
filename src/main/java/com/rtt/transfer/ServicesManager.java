package com.rtt.transfer;

import com.rtt.transfer.services.AccountsService;
import com.rtt.transfer.services.AccountsServiceSimple;
import com.rtt.transfer.services.TransactionsService;
import com.rtt.transfer.services.TransactionsServiceSimple;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ServicesManager {

    private static AccountsService accountsService;
    private static TransactionsService transactionsService;

    private static EntityManagerFactory entityManagerFactory;


    public static void initServices(String persistenceUnitName) {
        accountsService = new AccountsServiceSimple();
        transactionsService = new TransactionsServiceSimple(accountsService);

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