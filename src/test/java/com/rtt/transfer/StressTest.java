package com.rtt.transfer;

import com.rtt.transfer.model.Account;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.rtt.transfer.ServicesManager.getAccountsService;
import static com.rtt.transfer.ServicesManager.getTransactionsService;
import static org.junit.Assert.assertEquals;

public class StressTest {

    private static final String TEST_PERSISTENCE_UNIT_NAME = "rtt-test";

    private static final int THREADS_NUMBER = 100;
    private static final int TRANSFERS_COUNT_1 = 60;
    private static final int TRANSFERS_COUNT_2 = 120;

    private ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);


    @Test
    public void stressTest() throws InterruptedException {
        ServicesManager.initServices(TEST_PERSISTENCE_UNIT_NAME);
        String firstAccountId = getAccountsService().createAccountAndGetId(new BigDecimal("100.99"));
        String secondAccountId = getAccountsService().createAccountAndGetId(new BigDecimal("100.52"));
        String thirdAccountId = getAccountsService().createAccountAndGetId(new BigDecimal("100.01"));

        for(int i = 0; i < THREADS_NUMBER; i++) {
            executorService.execute(() -> {
                int decisionPoint = new Random().nextInt(3);
                switch (decisionPoint) {
                    default:
                    case 0:
                        getTransactionsService().transferMoneyAndReturnTransactionId(firstAccountId, secondAccountId, BigDecimal.valueOf(0.92), "");
                        break;

                    case 1:
                        for (int j = 0; j < TRANSFERS_COUNT_1; j++) {
                            getTransactionsService().transferMoneyAndReturnTransactionId(secondAccountId, thirdAccountId, BigDecimal.valueOf(0.57), "");
                        }
                        break;

                    case 2:
                        for (int k = 0; k < TRANSFERS_COUNT_2; k++) {
                            getTransactionsService().transferMoneyAndReturnTransactionId(thirdAccountId, firstAccountId, BigDecimal.valueOf(0.23), "");
                        }
                        break;
                }
            });
        }
        executorService.awaitTermination(25L, TimeUnit.SECONDS);

        Account firstAccountFromDb = getAccountsService().getAccount(firstAccountId);
        Account secondAccountFromDb = getAccountsService().getAccount(secondAccountId);
        Account thirdAccountFromDb = getAccountsService().getAccount(thirdAccountId);

        System.out.println(firstAccountFromDb.getAmount() + " " +
                secondAccountFromDb.getAmount() + " " +
                thirdAccountFromDb.getAmount());

        assertEquals(
                firstAccountFromDb.getAmount()
                        .add(secondAccountFromDb.getAmount())
                        .add(thirdAccountFromDb.getAmount()),
                new BigDecimal("301.52"));
    }

}
