package br.com.infox.core.crud;

import java.util.List;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.persistence.Recursive;
import br.com.infox.core.persistence.RecursiveManager;
import br.com.itx.util.EntityUtil;

@SuppressWarnings(WarningConstants.UNCHECKED)
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
                ||!curRecursive.getPathDescriptor().equals(oldRecursive.getPathDescriptor()) 
                || (curRecursive.getParent()!=null && !curRecursive.getParent().equals(oldRecursive.getParent()))
                || (oldRecursive != null && oldRecursive.getParent()!=null && !oldRecursive.getParent().equals(curRecursive.getParent()))) {
            updateRecursive(curRecursive);
        }
    }
    
    private void updateRecursive(E recursive) {
        RecursiveManager.refactor(recursive);
        final List<E> childList = recursive.getChildList();
        for(int i=0,l=childList.size();i<l;i++) {
            updateRecursive(childList.get(i));
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
    
    @Override
    protected String persist() {
        String ret = super.persist();
        if (PERSISTED.equals(ret)) {
        }
        
        return ret;
    }
    
    protected abstract void limparTrees();

}
