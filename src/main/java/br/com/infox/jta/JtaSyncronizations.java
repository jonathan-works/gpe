package br.com.infox.jta;

import javax.transaction.Synchronization;

import org.jboss.seam.transaction.Synchronizations;

import br.com.infox.core.transaction.TransactionSyncronizationsUtil;

public class JtaSyncronizations implements Synchronizations {
    
    @Override
    public void afterTransactionBegin() {
        
        //noop, let JTA notify us
        
    }

    @Override
    public void afterTransactionCommit(boolean success) {
        
        //noop, let JTA notify us
        
    }

    @Override
    public void afterTransactionRollback() {
        
        //noop, let JTA notify us
        
    }

    @Override
    public void beforeTransactionCommit() {
    
        //noop, let JTA notify us
        
    }

    @Override
    public void registerSynchronization(Synchronization sync) {
        TransactionSyncronizationsUtil.getTransactionSynchronizationRegistry().registerInterposedSynchronization(sync);
    }

    @Override
    public boolean isAwareOfContainerTransactions() {
        return true;
    }
}
