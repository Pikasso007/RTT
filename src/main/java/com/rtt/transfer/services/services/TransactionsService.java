package com.rtt.transfer.services.services;

import java.math.BigDecimal;

public interface TransactionsService {
    String transfer(String accountIdFrom, String accountIdTo, BigDecimal amount, String comment);
}