package br.com.infox.core.crud;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.List;

import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.exception.RecursiveException;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.Recursive;
import br.com.infox.core.persistence.RecursiveManager;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public abstract class AbstractRecursiveCrudAction<E extends Recursive<E>, M extends Manager<? extends DAO<E>, E>> extends AbstractCrudAction<E, M> {

    private static final long serialVersionUID = 1L;

    private static final LogProvider LOG = Logging.getLogProvider(AbstractRecursiveCrudAction.class);

    private E oldInstance;

    @Override
    public void setInstance(E instance) {
        super.setInstance(instance);
        updateOldInstance(getInstance());
    }

    @SuppressWarnings(UNCHECKED)
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
                || (curRecursive.getPathDescriptor() != null && !curRecursive.getPathDescriptor().equals(oldRecursive.getPathDescriptor()))
                || (curRecursive.getParent() != null && !curRecursive.getParent().equals(oldRecursive.getParent()))
                || (oldRecursive != null && oldRecursive.getParent() != null && !oldRecursive.getParent().equals(curRecursive.getParent()))) {
            updateRecursive(curRecursive);
        }
    }

    private void updateRecursive(final E recursive) {
        RecursiveManager.refactor(recursive);
        final List<E> children = recursive.getChildList();
        for (int i = 0, l = children.size(); i < l; i++) {
            updateRecursive(children.get(i));
        }
    }

    @Override
    @Transactional
    protected boolean isInstanceValid() {
        try {
            updateRecursivePath();
        } catch (RecursiveException e) {
            LOG.error("Não foi possível atualizar o caminho completo da entidade "
                    + getInstance(), e);
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
