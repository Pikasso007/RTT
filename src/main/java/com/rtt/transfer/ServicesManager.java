package com.rtt.transfer;

import com.rtt.transfer.services.MoneyServiceSimple;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ServicesManager {

    private static MoneyServiceSimple moneyServiceSimple;
    private static EntityManagerFactory entityManagerFactory;

    static void initServices() {
        moneyServiceSimple = new MoneyServiceSimple();
        entityManagerFactory = Persistence.createEntityManagerFactory("rtt");
    }

    public static MoneyServiceSimple getMoneyService() {
        return moneyServiceSimple;
    }

    public static EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}