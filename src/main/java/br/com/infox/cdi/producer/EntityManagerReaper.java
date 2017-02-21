package br.com.infox.cdi.producer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

public class EntityManagerReaper extends Thread implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<EntityManagerWaiting> entityManagerList = new ArrayList<>();
    
    private static EntityManagerReaper INSTANCE;

    private EntityManagerReaper() {
        start();
    }

    public static synchronized EntityManagerReaper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EntityManagerReaper();
        }
        return INSTANCE;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (entityManagerList) {
                Iterator<EntityManagerWaiting> iterator = entityManagerList.iterator();
                while (iterator.hasNext()) {
                    EntityManagerWaiting entityManagerWaiting = iterator.next();
                    if (entityManagerWaiting.isNotRunning()) {
                        entityManagerWaiting.close();
                        iterator.remove();
                    }
                }
            }
            sleep();
        }
    }

    public void register(EntityManager entityManager, Thread thread) {
        EntityManagerWaiting entityManagerWaiting = new EntityManagerWaiting(entityManager, thread);
        synchronized (entityManagerList) {
            entityManagerList.add(entityManagerWaiting);
        }
    }
    
    public void remove(EntityManager entityManager, Thread thread) {
        EntityManagerWaiting entityManagerWaiting = new EntityManagerWaiting(entityManager, thread);
        synchronized (entityManagerList) {
            entityManagerList.remove(entityManagerWaiting);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) { // do nothing
        }
    }

    public static class EntityManagerWaiting {

        private EntityManager entityManager;
        private Thread thread;

        public EntityManagerWaiting(EntityManager entityManager, Thread thread) {
            this.entityManager = entityManager;
            this.thread = thread;
        }

        public EntityManager getEntityManager() {
            return entityManager;
        }

        public Thread getThread() {
            return thread;
        }
        
        public void close() {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        public boolean isNotRunning() {
            return !entityManager.isOpen() || thread.getState() != State.RUNNABLE;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((entityManager == null) ? 0 : entityManager.hashCode());
            result = prime * result + ((thread == null) ? 0 : thread.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EntityManagerWaiting other = (EntityManagerWaiting) obj;
            if (entityManager == null) {
                if (other.entityManager != null)
                    return false;
            } else if (!entityManager.equals(other.entityManager))
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
