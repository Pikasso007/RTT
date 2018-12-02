package com.rtt.transfer.services.services;

import com.rtt.transfer.services.ServicesManager;
import com.rtt.transfer.services.model.Account;

import javax.persistence.EntityManager;
import java.math.BigDecimal;

public class AccountsServiceSimple implements AccountsService {

    public String createAccountAndGetId(BigDecimal amount) {
        Account account = new Account(amount);

        EntityManager entityManager = ServicesManager.createEntityManager();
        entityManager.getTransaction().begin();

        entityManager.persist(account);

        entityManager.getTransaction().commit();
        entityManager.close();

        return account.getId().toString();
    }

    public Account getAccount(String id) {
        EntityManager entityManager = ServicesManager.createEntityManager();

        Account account = entityManager.find(Account.class, Long.parseLong(id));

        entityManager.close();

        return account;
    }
}