package br.com.infox.core.crud;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.lang.reflect.InvocationTargetException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.dao.DAO;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.GenericDatabaseErrorCode;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

/**
 * É um abstractAction, porém possui os métodos implementados de Crudable
 * possibilitando o controle de abas e gerenciamento de instancias para as
 * páginas de cadastros básicos.
 * 
 * @author Daniel
 * 
 *         CRUD = Create, Retrieve, Update, Delete.
 * 
 * @param <T> Entity principal, onde devem ser realizadas as alterações.
 */
/**
 * @author erikliberal
 * 
 * @param <T>
 * @param <M>
 */
@Scope(ScopeType.CONVERSATION)
public abstract class AbstractCrudAction<T, M extends Manager<? extends DAO<T>, T>> extends AbstractAction<T, M> implements Crudable<T> {

    private static final long serialVersionUID = 1L;
    private String tab;
    private Object id;

    protected static final String MSG_REGISTRO_CRIADO = "#{infoxMessages['entity_created']}";
    protected static final String MSG_REGISTRO_ALTERADO = "#{infoxMessages['entity_updated']}";
    protected static final String MSG_REGISTRO_REMOVIDO = "#{infoxMessages['entity_deleted']}";
    protected static final String MSG_REGISTRO_NAO_REMOVIDO_FK = "#{infoxMessages['constraintViolation.foreignKeyViolation']}";
    private static final LogProvider LOG = Logging.getLogProvider(AbstractCrudAction.class);

    /**
     * Variável que será passada como parametro nas ações executadas por esse
     * Bean.
     */
    private T instance;

    @Override
    public void setInstance(final T instance) {
        this.instance = instance;
        if (instance == null) {
            this.id = null;
        } else {
            this.id = EntityUtil.getEntityIdObject(instance);
        }
    }

    @Override
    public T getInstance() {
        if (instance == null) {
            newInstance();
        }
        return instance;
    }

    /**
     * Devem ser escritas aqui as ações que serão executadas antes da inserção
     * ou atualização dos dados.
     */
    protected boolean isInstanceValid() {
        return Boolean.TRUE;
    }

    protected void beforeSave() {
    }

    /**
     * Deprecated use {@link #AbstractCrudAction.afterSave(String)}.
     * {@link #afterSave(String)} instead
     */
    @Deprecated
    protected void afterSave() {
    }

    protected void afterSave(final String ret) {
    }

    @Override
    public String getTab() {
        return tab;
    }

    @Override
    public void setTab(final String tab) {
        this.tab = tab;
    }

    @Override
    public void setId(final Object id) {
        this.setId(id, true);
    }

    public void setId(final Object id, final boolean switchTab) {
        if (id != null && !id.equals(this.id)) {
            this.id = id;
            setInstance(getManager().find(this.id));
            if (switchTab) {
                tab = TAB_FORM;
            }
        } else if (id == null) {
            this.id = null;
        }
    }

    @Override
    public Object getId() {
        return id;
    }

    /**
     * Indica se a instancia é gerenciavel ou não (já está no banco).
     * 
     * @return true se for gerenciavel.
     */
    @Override
    public boolean isManaged() {
        mergeWhenNeeded();
        final T activeEntity = getInstance();
        return activeEntity != null && contains(activeEntity);
    }

    private void mergeWhenNeeded() {
        final T activeEntity = getInstance();
        if (activeEntity != null && isIdDefined() && !contains(activeEntity)) {
            try {
                setInstance(getManager().merge(activeEntity));
            } catch (final DAOException e) {
                BeanManager.INSTANCE.getReference(ActionMessagesService.class).handleGenericException(e);
            }
        }
    }

    private boolean isIdDefined() {
        final Object currentId = getId();
        return currentId != null && !"".equals(currentId);
    }

    /**
     * Registra ou altera a instância atual.
     * 
     * @return "persisted" ou "updated" se obtiver sucesso. Null caso ocorra
     *         alguma falha na execução ou na validação.
     */
    @Override
    public String save() {
        String ret = null;
        final boolean wasManaged = isManaged();
        if (isInstanceValid()) {
            beforeSave();
            ret = wasManaged ? update() : persist();
        }
        final boolean persistFailed = !PERSISTED.equals(ret) && !wasManaged;
        if (persistFailed) {
            try {
                setInstance(EntityUtil.cloneEntity(getInstance(), false));
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error(".save()", e);
            }
        } else {
            // TODO: assim que os testes de crud estiverem prontos, jogar essas
            // duas invocações para fora desse if
            afterSave();
            afterSave(ret);

            if (PERSISTED.equals(ret)) {
                try {
                    final Object id = EntityUtil.getId(getInstance()).getReadMethod().invoke(getInstance());
                    setId(id, false);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    LOG.error(".save()", e);
                }
            }
            if (ret != null) {
                resolveStatusMessage(ret);
            }
        }
        return ret;
    }

    protected void resolveStatusMessage(String ret) {
        final StatusMessages messages = getMessagesHandler();
        if (PERSISTED.equals(ret)) {
            messages.clear();
            messages.add(MSG_REGISTRO_CRIADO);
        } else if (UPDATED.equals(ret)) {
            messages.clear();
            messages.add(MSG_REGISTRO_ALTERADO);
        } else if (REMOVED.equals(ret)) {
            messages.clear();
            messages.add(MSG_REGISTRO_REMOVIDO);
        }
    }

    public void setInstanceId(final Object id) {
        setId(id, false);
    }

    public Object getInstanceId() {
        return this.getId();
    }

    /**
     * Wrapper para o método persist(), pois é necessario definir que a
     * instancia será managed = true a partir de agora.
     * 
     * @return "persisted" se obtiver sucesso na inserção.
     */
    protected String persist() {
        return super.persist(instance);
    }

    protected String update() {
        return super.update(instance);
    }

    /**
     * Cria um novo objeto do tipo parametrizado para a variável instance.
     */
    @SuppressWarnings(UNCHECKED)
    @Override
    public void newInstance() {
        setInstance((T) EntityUtil.newInstance(getClass()));
        id = null;
    }

    /**
     * Wrapper para o método remove(), pois é necessario chamar o método
     * newInstance() para limpar a instancia atual.
     * 
     * @return "removed" se removido com sucesso.
     */
    public String remove() {
        return super.remove(instance);
    }

    @Override
    public String remove(final T obj) {
        final String ret = super.remove(obj);
        resolveStatusMessage(ret);
        return ret;
    }

    @Override
    public void onClickSearchTab() {
        newInstance();
        getManager().clear();
    }

    @Override
    public void onClickFormTab() {
        // Caso haja alguma ação a ser executada assim que a navegação for para
        // a aba de formulário,
        // então deve ser implementada aqui.
    }

    public String getHomeName() {
        String name = null;
        final Name nameAnnotation = this.getClass().getAnnotation(Name.class);
        if (nameAnnotation != null) {
            name = nameAnnotation.value();
        }
        return name;
    }

    protected void onDAOExcecption(final DAOException daoException) {
        final GenericDatabaseErrorCode errorCode = daoException.getDatabaseErrorCode();
        if (errorCode != null) {
            getMessagesHandler().clear();
            getMessagesHandler().add(infoxMessages.get(daoException.getLocalizedMessage()));
        }
    }

}
