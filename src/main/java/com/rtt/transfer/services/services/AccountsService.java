package com.rtt.transfer.services.services;

import com.rtt.transfer.services.model.Account;
import com.rtt.transfer.services.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface AccountsService {
    String createAccountAndGetId(BigDecimal amount);
    Account getAccount(String accountId);
    List<Transaction> getTransactionsByAccountId(String accountId);
    boolean isExistAccount(String accountId);
}