package br.com.infox.jbpm.application;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jbpm.JbpmContext;

public class JbpmContextReaper extends Thread {
    
    private static final Set<JbpmContextRegistered> jbpmContextRegisteredList = new HashSet<>();
    private static JbpmContextReaper INSTANCE;
    private static final Integer WAITING = 1;
    
    private JbpmContextReaper() {
        setName(JbpmContextReaper.class.getCanonicalName());
        start();
    }
    
    @Override
    public void run() {
        
        while(true) {
            
            synchronized (jbpmContextRegisteredList) {
                Iterator<JbpmContextRegistered> iterator = jbpmContextRegisteredList.iterator();
                while (iterator.hasNext()) {
                    JbpmContextRegistered jbpmContextRegistered = iterator.next();
                    if (jbpmContextRegistered.isNotRunning()) {
                        jbpmContextRegistered.close();
                    }
                }
            }
            sleep();
        }
    }

    public static synchronized JbpmContextReaper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JbpmContextReaper();
        }
        return INSTANCE;
    }
    
    public void register(JbpmContext jbpmContext, Thread thread) {
        JbpmContextRegistered jbpmContextRegistered = new JbpmContextRegistered(jbpmContext, thread);
        synchronized (jbpmContextRegisteredList) {
            jbpmContextRegisteredList.add(jbpmContextRegistered);
        }
    }
    
    private void sleep() {
        try {
            Thread.sleep(WAITING);
        } catch (InterruptedException e) {
        }
    }
    
    private static class JbpmContextRegistered {
        
        private JbpmContext jbpmContext;
        private Thread thread;
        
        public JbpmContextRegistered(JbpmContext jbpmContext, Thread thread) {
            this.jbpmContext = jbpmContext;
            this.thread = thread;
        }

        public boolean isNotRunning() {
            return thread.getState() != State.RUNNABLE;
        }
        
        public void close() {
            if (!jbpmContext.isClosed()) {
                jbpmContext.closeQuietly();
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((jbpmContext == null) ? 0 : jbpmContext.hashCode());
            result = prime * result + ((thread == null) ? 0 : thread.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof JbpmContextRegistered))
                return false;
            JbpmContextRegistered other = (JbpmContextRegistered) obj;
            if (jbpmContext == null) {
                if (other.jbpmContext != null)
                    return false;
            } else if (!jbpmContext.equals(other.jbpmContext))
                return false;
            if (thread == null) {
                if (other.thread != null)
                    return false;
            } else if (!thread.equals(other.thread))
                return false;
            return true;
        }
        
    }
}
