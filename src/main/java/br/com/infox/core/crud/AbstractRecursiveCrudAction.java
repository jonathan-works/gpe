package br.com.infox.core.crud;

import java.util.List;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.persistence.Recursive;
import br.com.infox.core.persistence.RecursiveManager;
import br.com.itx.util.EntityUtil;

@SuppressWarnings(WarningConstants.UNCHECKED)
public abstract class AbstractRecursiveCrudAction<E> extends 
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
        final Recursive<E> curRecursive =(Recursive<E>)getInstance();
        final Recursive<E> oldRecursive = (Recursive<E>)oldInstance;
        if (!isManaged()
                ||!curRecursive.getPathDescriptor().equals(oldRecursive.getPathDescriptor()) 
                || (!curRecursive.getParent().equals(oldRecursive.getParent()))) {
            updateRecursive(curRecursive);
        }
    }
    
    private void updateRecursive(Recursive<E> recursive) {
        RecursiveManager.refactor(recursive);
        final List<E> childList = recursive.getChildList();
        for(int i=0,l=childList.size();i<l;i++) {
            updateRecursive((Recursive<E>)childList.get(i));
        }
    }
    
    @Override
    protected boolean beforeSave() {
        updateRecursivePath();
        return super.beforeSave();
    }
    
    @Override
    protected void afterSave() {
        updateOldInstance();
        super.afterSave();
    }
    
    private void updateOldInstance() {
        updateOldInstance(getInstance());
    }

}
