package br.com.infox.jta;

import static org.jboss.seam.annotations.Install.DEPLOYMENT;

import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.transaction.Synchronizations;

import br.com.infox.epp.cdi.util.JNDI;

@Name("org.jboss.seam.transaction.synchronizations")
@Scope(ScopeType.EVENT)
@Install(precedence=DEPLOYMENT)
@BypassInterceptors
public class JtaSynchronizations implements Synchronizations {

    @Override
    public void afterTransactionBegin() {
        
    }

    @Override
    public void afterTransactionCommit(boolean success) {
        
    }

    @Override
    public void afterTransactionRollback() {
        
    }

    @Override
    public void beforeTransactionCommit() {
        
    }

    @Override
    public void registerSynchronization(Synchronization sync) {
        JNDI.<TransactionSynchronizationRegistry>lookup("java:comp/TransactionSynchronizationRegistry").registerInterposedSynchronization(sync);
    }

    @Override
    public boolean isAwareOfContainerTransactions() {
        return true;
    }

}
