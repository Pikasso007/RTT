package com.rtt.transfer.services;

import com.rtt.transfer.model.Account;
import java.math.BigDecimal;

public interface MoneyService {

    String createAccount();

    Account getAccount(String id);

    String transfer(String accountIdFrom, String accountIdTo, BigDecimal amount, String comment);
}