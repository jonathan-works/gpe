package br.com.infox.core.crud;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

import java.util.List;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.exception.RecursiveException;
import br.com.infox.core.persistence.Recursive;
import br.com.infox.core.persistence.RecursiveManager;
import br.com.itx.util.EntityUtil;

@SuppressWarnings(UNCHECKED)
public abstract class AbstractRecursiveCrudAction<E extends Recursive<E>> extends 
                            AbstractCrudAction<E> {
    
    private static final LogProvider LOG = Logging.getLogProvider(AbstractRecursiveCrudAction.class);
    
    private E oldInstance;
    
    @Override
    public void setInstance(E instance) {
        super.setInstance(instance);
        updateOldInstance(getInstance());
    }
    
    private void updateOldInstance(E recursive) {
        try {
            oldInstance = (E) EntityUtil.cloneObject(recursive, false);
        } catch (Exception e) {
            LOG.error(".updateOldInstance()", e);
        } 
    }
    
    private void updateRecursivePath() {
        final E curRecursive = getInstance();
        final E oldRecursive = oldInstance;
        if (!isManaged()
                ||(curRecursive.getPathDescriptor() != null && !curRecursive.getPathDescriptor().equals(oldRecursive.getPathDescriptor())) 
                || (curRecursive.getParent()!=null && !curRecursive.getParent().equals(oldRecursive.getParent()))
                || (oldRecursive != null && oldRecursive.getParent()!=null && !oldRecursive.getParent().equals(curRecursive.getParent()))) {
            updateRecursive(curRecursive);
        }
    }
    
    private void updateRecursive(final E recursive) {
        RecursiveManager.refactor(recursive);
        final List<E> children = recursive.getChildList();
        for(int i=0,l=children.size();i<l;i++) {
            updateRecursive(children.get(i));
        }
    }
    
    @Override
    protected boolean beforeSave() {
    	try {
    		updateRecursivePath();
    	} catch (RecursiveException e) {
    	    final StatusMessages messagesHandler = getMessagesHandler();
            messagesHandler.clear();
            messagesHandler.add(e.getMessage());
    		return Boolean.FALSE;
    	}
        return Boolean.TRUE;
    }
    
    @Override
    protected void afterSave(String ret) {
        updateOldInstance(getInstance());
        super.afterSave(ret);
    }
    
}