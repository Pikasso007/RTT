package com.rtt.transfer.services.services;

import com.rtt.transfer.services.model.Account;
import com.rtt.transfer.services.model.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.Objects;

public class TransactionsServiceSimple implements TransactionsService {

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