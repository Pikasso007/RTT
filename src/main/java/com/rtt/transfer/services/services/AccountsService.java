package com.rtt.transfer.services.services;

import com.rtt.transfer.services.model.Account;

import java.math.BigDecimal;

public interface AccountsService {
    String createAccountAndGetId(BigDecimal amount);
    Account getAccount(String id);
}