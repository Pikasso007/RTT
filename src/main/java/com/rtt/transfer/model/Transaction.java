package com.rtt.transfer.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    private String accountIdFrom;

    private String accountIdTo;

    private BigDecimal amount;

    private String comment;


    public Transaction(String accountIdFrom, String accountIdTo, BigDecimal amount, String comment) {
        this.accountIdFrom = accountIdFrom;
        this.accountIdTo = accountIdTo;
        this.amount = amount;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountIdFrom() {
        return accountIdFrom;
    }

    public void setAccountIdFrom(String accountIdFrom) {
        this.accountIdFrom = accountIdFrom;
    }

    public String getAccountIdTo() {
        return accountIdTo;
    }

    public void setAccountIdTo(String accountIdTo) {
        this.accountIdTo = accountIdTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
