package br.com.infox.cdi.producer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;

import br.com.infox.core.server.ApplicationServerService;

public final class JbpmContextProducer {
    
    private static final Map<Transaction, JbpmContext> registeredJbpmContext = new ConcurrentHashMap<>();
    
    public synchronized static JbpmContext getJbpmContext() {
        Transaction transaction = getTransaction();
        if (!registeredJbpmContext.containsKey(transaction)) {
            createJbpmContextTransactional(transaction);
        }
        return registeredJbpmContext.get(transaction);
    }

    public synchronized static void createJbpmContextTransactional() {
        Transaction transaction = getTransaction();
        createJbpmContextTransactional(transaction);
    }
    
    private static void createJbpmContextTransactional(Transaction transaction) {
        JbpmContext jbpmContext = JbpmConfiguration.getInstance().createJbpmContext();
        registerSynchronization(jbpmContext, transaction);
        registeredJbpmContext.put(transaction, jbpmContext);
    }
    
    private static void registerSynchronization(JbpmContext jbpmContext, Transaction transaction) {
        try {
            transaction.registerSynchronization(new JbpmContextSynchronization(jbpmContext));
        } catch (IllegalStateException | RollbackException | SystemException e) {
            throw new IllegalStateException("Error synchronizing jbpmContext ", e);
        }
    }
    
    private static Transaction getTransaction() {
        Transaction transaction = null;
        try {
            transaction = ApplicationServerService.instance().getTransactionManager().getTransaction();
            if (!isTransactionActive(transaction)) {
                throw new IllegalStateException("Transaction required to create a JbpmContext.");
            }
        } catch (SystemException e) {
            throw new IllegalStateException("Error obtaining transaction ", e);
        }
        return transaction;
    }
    
    private static boolean isTransactionActive(Transaction transaction) throws SystemException {
        return transaction != null && transaction.getStatus() == Status.STATUS_ACTIVE;
    }
    
    private static class JbpmContextSynchronization implements Synchronization {
        
        private JbpmContext jbpmContext;
        
        public JbpmContextSynchronization(JbpmContext jbpmContext) {
            this.jbpmContext = jbpmContext;
        }

        @Override
        public void beforeCompletion() {
            jbpmContext.close();
        }

        @Override
        public void afterCompletion(int status) {
            try {
                Transaction transaction  = ApplicationServerService.instance().getTransactionManager().getTransaction();
                registeredJbpmContext.remove(transaction);
            } catch (SystemException e) { }
        }
    }
    
}
