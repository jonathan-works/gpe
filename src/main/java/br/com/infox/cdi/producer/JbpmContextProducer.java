package br.com.infox.cdi.producer;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import org.hibernate.Session;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;

import br.com.infox.core.server.ApplicationServerService;

public final class JbpmContextProducer {
    
    private static final Set<JbpmContext> registeredJbpmContext = new HashSet<>();
    
    /*
     * Returns JbpmContext threadLocal if exists else create a new one and register onto Transaction
     */
    public static synchronized JbpmContext getJbpmContext() {
        JbpmContext jbpmContext = JbpmContext.getCurrentJbpmContext();
        if (jbpmContext == null || jbpmContext.isClosed()) {
            jbpmContext = JbpmConfiguration.getInstance().createJbpmContext();
            JbpmContextReaper.start(jbpmContext, Thread.currentThread());
        }
        if (!registeredJbpmContext.contains(jbpmContext)) {
            registerSynchronization(jbpmContext);
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
            registeredJbpmContext.add(jbpmContext);
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
            jbpmContext.autoSave();
            session.flush();
        }

        @Override
        public void afterCompletion(int status) {
            registeredJbpmContext.remove(jbpmContext);
        }
    }
    
    private static class JbpmContextReaper extends Thread {
        
        private JbpmContext jbpmContext; 
        private Thread thread;
        
        public static synchronized void start(JbpmContext jbpmContext, Thread thread) {
            new JbpmContextReaper(jbpmContext, thread).start();
        }
        
        private JbpmContextReaper(JbpmContext jbpmContext, Thread thread) {
            this.jbpmContext = jbpmContext;
            this.thread = thread;
        }
        
        @Override
        public void run() {
            while (thread.getState() == State.RUNNABLE) {
                sleep();
            }
            jbpmContext.closeQuietly();
        }
        
        private void sleep() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        
    }

}
