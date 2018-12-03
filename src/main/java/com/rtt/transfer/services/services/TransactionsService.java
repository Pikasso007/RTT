package com.rtt.transfer.services.services;

import com.rtt.transfer.services.model.Transaction;

import java.math.BigDecimal;

public interface TransactionsService {
    Transaction getTransactionById(String transactionId);
    String transferMoneyAndReturnTransactionId(String accountIdFrom, String accountIdTo, BigDecimal amount, String comment);
}