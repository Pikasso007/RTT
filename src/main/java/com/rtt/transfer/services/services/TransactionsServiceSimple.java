package com.rtt.transfer.services.services;

import com.rtt.transfer.services.ServicesManager;
import com.rtt.transfer.services.model.Account;
import com.rtt.transfer.services.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionsServiceSimple implements TransactionsService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionsServiceSimple.class);

    public static final String NOT_INVOKED_TRANSACTION_ID = "0";

    private Map<Long, Lock> lockForAccounts = new ConcurrentHashMap<>();
    private final Object objectForSyncWhenLockIsCreating = new Object();
    private AccountsService accountsService;


    public TransactionsServiceSimple(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @Override
    public Transaction getTransactionById(String transactionId) {
        LOG.info("Fetching transaction with id '{}'", transactionId);

        EntityManager entityManager = ServicesManager.createEntityManager();

        Transaction transaction = null;
        try {
            transaction = entityManager.find(Transaction.class, Long.parseLong(transactionId));
        } catch (NumberFormatException ex) {
            LOG.warn("Transaction Id '{}' can't be parsed to Long format ", transactionId);
        }

        entityManager.close();

        return transaction;
    }

    @Override
    public String transferMoneyAndReturnTransactionId(String accountIdFrom, String accountIdTo, BigDecimal amount, String comment)  {
        LOG.info("Money transferring is started");
        String resultTransactionId = NOT_INVOKED_TRANSACTION_ID;
        if (Objects.equals(accountIdFrom, accountIdTo)) {
            LOG.warn("Account Ids can't be the same for money transferring!");
            return resultTransactionId;
        }

        boolean isExistAccountIdFrom = accountsService.isExistAccount(accountIdFrom);
        boolean isExistAccountIdTo = accountsService.isExistAccount(accountIdTo);

        if (isExistAccountIdFrom && accountIdTo == null) {
            LOG.info("Withdraw money from accountId '{}'", accountIdFrom);
            resultTransactionId = withdrawOrDepositMoney(accountIdFrom, accountIdTo, amount, comment);
        } else if (isExistAccountIdTo && accountIdFrom == null) {
            LOG.info("Deposit money to accountId '{}'", accountIdTo);
            resultTransactionId = withdrawOrDepositMoney(accountIdFrom, accountIdTo, amount, comment);
        } else if (isExistAccountIdFrom && isExistAccountIdTo) {
            LOG.info("Transfer money from accountId '{}' to accountId '{}'", accountIdFrom, accountIdTo);
            resultTransactionId = transferMoney(accountIdFrom, accountIdTo, amount, comment);
        } else {
            LOG.error("Accounts are invalid!");
        }

        return resultTransactionId;
    }

    private String transferMoney(String accountIdFrom, String accountIdTo, BigDecimal amount, String comment) {
        final Lock firstLock;
        final Lock secondLock;
        long lAccountIdFrom = Long.parseLong(accountIdFrom);
        long lAccountIdTo = Long.parseLong(accountIdTo);
        if (lAccountIdFrom > lAccountIdTo) {
            firstLock = getLock(lAccountIdFrom);
            secondLock = getLock(lAccountIdTo);
        } else {
            firstLock = getLock(lAccountIdTo);
            secondLock = getLock(lAccountIdFrom);
        }

        EntityManager entityManager = ServicesManager.createEntityManager();
        firstLock.lock();
        secondLock.lock();
        try {
            Account accountFrom = entityManager.find(Account.class, lAccountIdFrom);
            Account accountTo = entityManager.find(Account.class, lAccountIdTo);

            Transaction transaction = null;
            try {
                entityManager.getTransaction().begin();

                accountFrom.setAmount(accountFrom.getAmount().subtract(amount));
                accountTo.setAmount(accountTo.getAmount().add(amount));

                transaction = new Transaction(accountIdFrom, accountIdTo, amount, comment);

                entityManager.persist(transaction);

                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                LOG.error("Transaction with number '" + transaction.getId() + "' has been rollbacked!");
            }

            return transaction.getId().toString();
        } finally {
            entityManager.close();
            firstLock.unlock();
            secondLock.unlock();
        }
    }

    private String withdrawOrDepositMoney(String accountIdFrom, String accountIdTo, BigDecimal amount, String comment) {
        long lAccountId = Long.parseLong(accountIdFrom == null ? accountIdTo : accountIdFrom);
        final Lock lock = getLock(lAccountId);

        EntityManager entityManager = ServicesManager.createEntityManager();
        lock.lock();
        try {
            Account account = entityManager.find(Account.class, lAccountId);

            Transaction transaction = null;
            try {
                entityManager.getTransaction().begin();

                account.setAmount(accountIdFrom == null
                        ? account.getAmount().add(amount)
                        : account.getAmount().subtract(amount));

                transaction = new Transaction(accountIdFrom, accountIdTo, amount, comment);

                entityManager.persist(transaction);

                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                LOG.error("Transaction with number '" + transaction.getId() + "' has been rollback!");
            }

            return transaction.getId().toString();
        } finally {
            entityManager.close();
            lock.unlock();
        }
    }

    private Lock getLock(Long accountId) {
        Lock lockForAccountId = lockForAccounts.get(accountId);
        if (lockForAccountId == null) {
            synchronized (objectForSyncWhenLockIsCreating) {
                lockForAccounts.putIfAbsent(accountId, new ReentrantLock());
                lockForAccountId = lockForAccounts.get(accountId);
            }
        }

        return lockForAccountId;
    }
}