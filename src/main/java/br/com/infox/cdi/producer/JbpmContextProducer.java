package br.com.infox.cdi.producer;

import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import org.hibernate.Session;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;

import br.com.infox.core.server.ApplicationServerService;

public final class JbpmContextProducer {
    
    /*
     * Returns JbpmContext threadLocal if exists else create a new one and register onto Transaction
     */
    public static JbpmContext getJbpmContext() {
        JbpmContext jbpmContext = JbpmContext.getCurrentJbpmContext();
        if (jbpmContext == null || jbpmContext.isClosed()) {
            jbpmContext = createJbpmContextTransactional();
        }
        return jbpmContext;
    }
    
    public static JbpmContext createJbpmContextTransactional() {
        JbpmContext jbpmContext = JbpmConfiguration.getInstance().createJbpmContext();
        registerSynchronization(jbpmContext);
        return jbpmContext;
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
            Session session = jbpmContext.getSession();
            jbpmContext.close();
            session.flush();
        }

        @Override
        public void afterCompletion(int status) {
        }
    }

}
