package br.com.infox.epp.test.crud;

import java.util.List;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.test.core.messages.MockMessagesHandler;
import br.com.infox.epp.test.infra.MockGenericManager;

public abstract class AbstractMockCrudAction<T> extends AbstractCrudAction<T> {
    
    public AbstractMockCrudAction() {
        super();
        setGenericManager(new MockGenericManager());
    }
    
    public boolean contains(T entity) {
        return getGenericManager().contains(entity);
    }
    
    public abstract List<T> getAll();
    
    @Override
    protected final MockMessagesHandler getMessagesHandler() {
        return MockMessagesHandler.instance();
    }
    
}
