package br.com.infox.jta;

import javax.transaction.Synchronization;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.transaction.Synchronizations;

import br.com.infox.core.transaction.TransactionSyncronizationsUtil;

@Name("org.jboss.seam.transaction.synchronizations")
@Scope(ScopeType.EVENT)
@Install(precedence=Install.DEPLOYMENT)
@BypassInterceptors
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
