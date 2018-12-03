package com.rtt.transfer.services.services;

import com.rtt.transfer.services.ServicesManager;
import com.rtt.transfer.services.model.Account;
import com.rtt.transfer.services.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

public class AccountsServiceSimple implements AccountsService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountsServiceSimple.class);

    private static final String ACCOUNT_ID_FROM_PARAM = "accountIdFromParam";
    private static final String ACCOUNT_ID_TO_PARAM = "accountIdToParam";
    private static final String SELECT_TRANSACTIONS_BY_ACCOUNT_ID = "Select t from Transaction t where t.accountIdFrom=:accountIdFromParam or t.accountIdTo=:accountIdToParam";

    @Override
    public String createAccountAndGetId(BigDecimal amount) {
        Account account = new Account(amount);

        EntityManager entityManager = ServicesManager.createEntityManager();
        entityManager.getTransaction().begin();

        entityManager.persist(account);

        entityManager.getTransaction().commit();
        entityManager.close();

        return account.getId().toString();
    }

    @Override
    public Account getAccount(String accountId) {
        EntityManager entityManager = ServicesManager.createEntityManager();

        Account account = null;
        try {
            account = entityManager.find(Account.class, Long.parseLong(accountId));
        } catch (NumberFormatException ex) {
            LOG.warn("Account Id '{}' can't be parsed to Long format ", accountId);
        }

        entityManager.close();

        return account;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(String accountId) {
        EntityManager entityManager = ServicesManager.createEntityManager();
        Query query = entityManager.createQuery(SELECT_TRANSACTIONS_BY_ACCOUNT_ID);
        query.setParameter(ACCOUNT_ID_FROM_PARAM, accountId);
        query.setParameter(ACCOUNT_ID_TO_PARAM, accountId);

        List<Transaction> result = query.getResultList();
        entityManager.close();

        return result;
    }

    @Override
    public boolean isExistAccount(String accountId) {
        EntityManager entityManager = ServicesManager.createEntityManager();
        boolean result = false;
        try {
            result = entityManager.find(Account.class, Long.parseLong(accountId)) != null;
        } catch (NumberFormatException ex) {
            LOG.warn("Account Id '{}' can't be parsed to Long format ", accountId);
        }
        entityManager.close();
        return result;
    }
}