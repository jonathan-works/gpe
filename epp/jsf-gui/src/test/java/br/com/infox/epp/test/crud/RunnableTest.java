package br.com.infox.epp.test.crud;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.seam.contexts.TestLifecycle;
import org.jboss.seam.servlet.ServletSessionMap;

public abstract class RunnableTest<E> extends AbstractCrudActions<E> {
    
    public static abstract class ActionContainer<R> {
        private R entity;

        public ActionContainer() {
            entity = null;
        }
        
        public ActionContainer(final R entity) {
            if (entity == null) {
                throw new NullPointerException("Null entity not allowed for EntityActionContainer");
            }
            this.entity = entity;
        }

        public final void execute(final R entity, final CrudActions<R> crudActions) {
            this.setEntity(entity);
            this.execute(crudActions);
        }
        
        public abstract void execute(final CrudActions<R> crudActions);
        
        public R getEntity() {
            return entity;
        }

        public void setEntity(final R entity) {
            this.entity = entity;
        }
    }
    
    private E entity;
    private ActionContainer<E> actionContainer;
    
    public RunnableTest(final String componentName) {
        super(componentName);
    }
    
    protected abstract void testComponent() throws Exception;
    
    public final E getEntity() {
        return entity;
    }
    
    public final E runTest(final ServletContext servletContext, final HttpSession session) throws Exception {
        return this.runTest(null, null, servletContext, session);
    }
    
    public final E runTest(final E entity, final ServletContext servletContext, final HttpSession session) throws Exception {
        return this.runTest(null, entity, servletContext, session);
    }
    
    public final E runTest(final ActionContainer<E> actionContainer, final ServletContext servletContext, final HttpSession session) throws Exception {
        return this.runTest(actionContainer, actionContainer.getEntity(), servletContext, session);
    }
    
    public final E runTest(final ActionContainer<E> actionContainer, final E entity, final ServletContext servletContext, final HttpSession session) throws Exception {
        this.entity = entity;
        this.actionContainer = actionContainer;

        try {
            TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
            testComponent();
            if (this.actionContainer != null) {
                this.actionContainer.entity = entity;
                this.actionContainer.execute(this);
            }
        } finally {
            TestLifecycle.endTest();
        }
        return this.entity;
    }

    public final void setEntity(final E entity) {
        this.entity = entity;
    }

 }
