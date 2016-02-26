package br.com.infox.core.transaction;

import javax.transaction.TransactionSynchronizationRegistry;

import br.com.infox.epp.cdi.util.JNDI;

public final class TransactionSyncronizationsUtil {
    
    private static TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    public static TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
        TransactionSynchronizationRegistry tsr = JNDI.<TransactionSynchronizationRegistry>lookup("java:comp/TransactionSynchronizationRegistry");
        if (tsr == null) {
            tsr = transactionSynchronizationRegistry;
        }
        return tsr;
    }

    public static void setTransactionSynchronizationRegistry(TransactionSynchronizationRegistry transactionSynchronizationRegistry) {
        TransactionSyncronizationsUtil.transactionSynchronizationRegistry = transactionSynchronizationRegistry;
    }
    
}
