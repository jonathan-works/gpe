package br.com.infox.epp.test.crud;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.test.core.messages.MockMessagesHandler;

public abstract class AbstractGenericCrudTest<T> {
    private AbstractMockCrudAction<T> mockCrudAction;

    private List<T> inactivateList = new ArrayList<>(0);
    private List<T> inactivateListFail = new ArrayList<>(0);
    private List<T> persistList = new ArrayList<>(0);
    private List<T> persistFailList = new ArrayList<>(0);
    private List<T> removeList = new ArrayList<>(0);
    private List<T> removeFailList = new ArrayList<>(0);
    private List<EntityActionContainer<T>> updateList = new ArrayList<>(0);
    private List<EntityActionContainer<T>> updateFailList = new ArrayList<>(0);
    
    private final String inactivate() {
        final T entity = mockCrudAction.getInstance();
        return mockCrudAction.inactive(entity);
    }
    
    private final String remove(T entity) {
        return mockCrudAction.remove(entity);
    }
    
    private final String save(T entity) {
        mockCrudAction.setInstance(entity);
        return mockCrudAction.save();
    }
    
    protected abstract void initLists();
    
    protected final void setInactivateFailList(List<T> entityList) {
        this.inactivateListFail = entityList;
    }
    
    protected final void setInactivateList(List<T> entityList) {
        this.inactivateList = entityList;
    }
    
    protected final void setPersistFailList(List<T> entityList) {
        this.persistFailList = entityList;
    }
    
    protected final void setPersistList(List<T> entityList) {
        this.persistList = entityList;
    }
    
    protected final void setRemoveFailList(List<T> entityList) {
        this.removeFailList = entityList;
    }

    protected final void setRemoveList(List<T> entityList) {
        this.removeList = entityList;
    }

    protected final void setUpdateFailList(List<EntityActionContainer<T>> entityContainerList) {
        this.updateFailList = entityContainerList;
    }
    
    protected final void setUpdateList(List<EntityActionContainer<T>> entityContainerList) {
        this.updateList = entityContainerList;
    }
    
    protected final String fillStr(String string, int topLength) {
        if (string == null || string.length() < 1) {
            string = "-";
        }
        
        StringBuilder sb = new StringBuilder(string);
        int length = string.length();
        if (length < topLength) {
            for (int i = 0, l = topLength-length; i < l; i++) {
                sb.append(" ");
            }
        }
        return sb.substring(0, topLength);
    }
    protected abstract AbstractMockCrudAction<T> getMockCrudAction();

    @After
    public void afterTest() {
        MockMessagesHandler.instance().clear();
    }

    @Before
    public void beforeTest() {
        mockCrudAction = getMockCrudAction();
        initLists();
    }
    
    @Test
    public final void testInactivate() {
        for (T entity : inactivateList) {
            final String returnSave = save(entity);
            final boolean wasSaved = AbstractAction.PERSISTED.equals(returnSave);
            Assert.assertTrue(wasSaved);
            
            final String returnInactivate = inactivate();
            final boolean wasInactivated = AbstractAction.UPDATED.equals(returnInactivate);
            Assert.assertTrue(wasInactivated);
            mockCrudAction.newInstance();
        }
    }
    
    @Test
    public final void testInactivateFail() {
        for (T entity : inactivateListFail) {
            final String returnSave = save(entity);
            final boolean wasSaved = AbstractAction.PERSISTED.equals(returnSave);
            Assert.assertTrue(wasSaved);
            
            final String returnInactivate = inactivate();
            final boolean wasInactivated = AbstractAction.UPDATED.equals(returnInactivate);
            Assert.assertFalse(wasInactivated);
            mockCrudAction.newInstance();
        }
    }
    
    @Test
    public final void testPersist() {
        for (T entity : persistList) {
            Assert.assertTrue(AbstractAction.PERSISTED.equals(save(entity)));
            mockCrudAction.newInstance();
        }
    }
    
    @Test
    public final void testPersistFail() {
        for (T entity : this.persistFailList) {
            Assert.assertFalse(AbstractAction.PERSISTED.equals(save(entity)));
            mockCrudAction.newInstance();
        }
    }

    @Test
    public final void testRemove() {
        for (T entity : removeList) {
            Assert.assertTrue(AbstractAction.PERSISTED.equals(save(entity)));
            mockCrudAction.newInstance();
        }
        
        for (T entity : mockCrudAction.getAll()) {
            Assert.assertTrue(AbstractAction.REMOVED.equals(remove(entity)));
        }
    }
    
    @Test
    public final void testRemoveFail() {
        for (T entity : removeFailList) {
            Assert.assertTrue(AbstractAction.PERSISTED.equals(save(entity)));
            mockCrudAction.newInstance();
        }
        for (T entity : mockCrudAction.getAll()) {
            Assert.assertFalse(AbstractAction.REMOVED.equals(remove(entity)));
        }
    }
    
    @Test
    public final void testUpdate() {
        for (EntityActionContainer<T> entityHolder : updateList) {
            T entity = entityHolder.getEntity();
            Assert.assertTrue(AbstractAction.PERSISTED.equals(save(entity)));

            entity = mockCrudAction.getInstance();
            entityHolder.run(entity);
            
            Assert.assertTrue(AbstractAction.UPDATED.equals(save(entity)));
        }
    }    
    
    @Test
    public final void testUpdateFail() {
        for (EntityActionContainer<T> entityHolder : updateFailList) {
            T entity = entityHolder.getEntity();
            Assert.assertTrue(AbstractAction.PERSISTED.equals(save(entity)));

            entity = mockCrudAction.getInstance();
            entityHolder.run(entity);
            
            Assert.assertFalse(AbstractAction.UPDATED.equals(save(entity)));
        }
    }
}
