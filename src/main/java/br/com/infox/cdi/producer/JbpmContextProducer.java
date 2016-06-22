package br.com.infox.cdi.producer;

import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;

import br.com.infox.core.server.ApplicationServerService;

public final class JbpmContextProducer {
    
    /*
     * Returns JbpmContext threadLocal if exists else create a new one and register onto Transaction
     */
    public static JbpmContext getJbpmContext() {
        JbpmContext jbpmContext = JbpmConfiguration.getInstance().getCurrentJbpmContext();
        return jbpmContext == null ? createJbpmContextTransactional() : jbpmContext;
    }
    
    /* 
     * Returns JbpmContext created. Remember to close on final execution.
     */
    public static JbpmContext getJbpmContextNotManaged() {
        return createNewJbpmContext();
    }
    
    public static JbpmContext createJbpmContextTransactional() {
        JbpmContext jbpmContext = JbpmConfiguration.getInstance().createJbpmContext();
        registerSynchronization(jbpmContext);
        return jbpmContext;
    }
    
    public static JbpmContext createNewJbpmContext() {
        return JbpmConfiguration.getInstance().createJbpmContext();
    }
    
    private static void registerSynchronization(JbpmContext jbpmContext) {
        try {
            Transaction transaction = ApplicationServerService.instance().getTransactionManager().getTransaction();
            if (transaction == null) {
                throw new IllegalStateException("For create a JbpmContext transaction is required");
            }
            transaction.registerSynchronization(new JbpmContextSynchronization(jbpmContext));
        } catch (SystemException | RollbackException e) {
            throw new IllegalStateException("Error registering synchronization", e);
        } 
    }
    
    private static class JbpmContextSynchronization implements Synchronization {
        
        private JbpmContext jbpmContext;
        
        public JbpmContextSynchronization(JbpmContext jbpmContext) {
            this.jbpmContext = jbpmContext;
        }

        @Override
        public void beforeCompletion() {
            jbpmContext.autoSave();
            jbpmContext.getSession().flush();
        }

        @Override
        public void afterCompletion(int status) {
            if (status != Status.STATUS_COMMITTED) {
                jbpmContext.setRollbackOnly();
            }
            jbpmContext.close();
        }
    }

}
