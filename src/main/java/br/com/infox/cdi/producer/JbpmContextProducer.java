package br.com.infox.cdi.producer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.core.server.ApplicationServerService;

public final class JbpmContextProducer {
    
    private static final Map<Transaction, JbpmContext> registeredJbpmContext = Collections.synchronizedMap(new HashMap<Transaction, JbpmContext>());
    
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
            transaction.registerSynchronization(new JbpmContextSynchronization(transaction, jbpmContext));
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
        
        private Transaction transaction;
        private JbpmContext jbpmContext;
        
        public JbpmContextSynchronization(Transaction transaction, JbpmContext jbpmContext) {
            this.transaction = transaction;
            this.jbpmContext = jbpmContext;
        }

        @Override
        public void beforeCompletion() {
            jbpmContext.close();
        }
        
        @Override
        public void afterCompletion(int status) {
            if (Status.STATUS_COMMITTED == status) clearLogs();
            registeredJbpmContext.remove(transaction);
        }

        private void clearLogs() {
            Set<ProcessInstance> autoSaveProcessInstances = jbpmContext.getAutoSaveProcessInstances();
            if (autoSaveProcessInstances != null) {
                for (ProcessInstance processInstance : autoSaveProcessInstances) {
                    processInstance.getLoggingInstance().getLogs().clear();
                }
            }
        }
    }
    
}
