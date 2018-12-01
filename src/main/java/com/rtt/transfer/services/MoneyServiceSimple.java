package com.rtt.transfer.services;

import com.rtt.transfer.ServicesManager;
import com.rtt.transfer.model.Account;
import com.rtt.transfer.model.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

public class MoneyServiceSimple implements MoneyService {

    public String createAccount() {
        Account account = new Account();
        account.setAmount(new BigDecimal(0));

        EntityManager entityManager = ServicesManager.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(account);
        entityManager.getTransaction().commit();
        entityManager.close();

        return account.getId().toString();
    }

    public Account getAccount(String id) {
        Account account = new Account();
        account.setAmount(new BigDecimal(0));

        EntityManager entityManager = ServicesManager.createEntityManager();
        return entityManager.find(Account.class, Long.parseLong(id));
    }

    public String transfer(String accountIdFrom, String accountIdTo, BigDecimal amount, String comment)  {
        if (Objects.equals(accountIdFrom, accountIdTo)) {
            return "AccountIds can't be the same!";
        }

        transferMoney(accountIdFrom, accountIdTo, amount, comment);
        return "full transfer compited";
    }

    private Transaction transferMoney(String accountIdFrom, String accountIdTo, BigDecimal amount, String comment) {
        //TODO: check accounts exist


        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("rtt");
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Account accountFrom = em.find(Account.class, Long.parseLong(accountIdFrom));
        Account accountTo = em.find(Account.class, Long.parseLong(accountIdTo));

        accountFrom.setAmount(accountFrom.getAmount().subtract(amount));
        accountTo.setAmount(accountTo.getAmount().add(amount));

        Transaction transaction = new Transaction(accountIdFrom, accountIdTo, amount, comment);
        em.persist(transaction);

        em.getTransaction().commit();
        em.close();
        return transaction;

    }
}