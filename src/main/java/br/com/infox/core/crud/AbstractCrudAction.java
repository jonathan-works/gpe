package br.com.infox.core.crud;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.PostgreSQLErrorCode;
import br.com.itx.util.EntityUtil;

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
@Scope(ScopeType.CONVERSATION)
public abstract class AbstractCrudAction<T> extends AbstractAction<T> implements Crudable<T>, Serializable {

    private static final long serialVersionUID = 1L;
    private String tab;
    private Object id;

    protected static final String MSG_REGISTRO_CRIADO = "#{messages['entity_created']}";
    protected static final String MSG_REGISTRO_ALTERADO = "#{messages['entity_updated']}";
    protected static final String MSG_REGISTRO_REMOVIDO = "#{messages['entity_deleted']}";
    private static final LogProvider LOG = Logging.getLogProvider(AbstractCrudAction.class);

    /**
     * Variável que será passada como parametro nas ações executadas por esse
     * Bean.
     */
    private T instance;

    @Override
    public void setInstance(final T instance) {
        this.instance = instance;
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
    protected boolean beforeSave() {
        return Boolean.TRUE;
    }

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
        if (id != null && !id.equals(this.id)) {
            setInstanceId(id);
            tab = TAB_FORM;
        } else if (id == null) {
            this.id = null;
        }
    }

    @SuppressWarnings(UNCHECKED)
    public void setId(final Object id, final boolean switchTab) {
        if (id != null && !id.equals(this.id)) {
            this.id = id;
            setInstance(find((Class<T>) EntityUtil.getParameterizedTypeClass(getClass()), this.id));
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
                setInstance(getGenericManager().merge(activeEntity));
            } catch (final DAOException e) {
                getMessagesHandler().add(Severity.ERROR, "Merge Entity Error", e);
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
        if (beforeSave()) {
            ret = wasManaged ? update() : persist();
        }
        final boolean persistFailed = ret == null
                || (!PERSISTED.equals(ret) && !wasManaged);
        if (ret != null) {
            afterSave();
            afterSave(ret);

            if (PERSISTED.equals(ret)) {
                try {
                    setInstanceId(EntityUtil.getId(instance).getReadMethod().invoke(instance));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    LOG.error(".save()", e);
                }
                getMessagesHandler().clear();
                getMessagesHandler().add(MSG_REGISTRO_CRIADO);
            } else if (UPDATED.equals(ret)) {
                getMessagesHandler().clear();
                getMessagesHandler().add(MSG_REGISTRO_ALTERADO);
            }
        }
        if (persistFailed) {
            try {
                setInstance(EntityUtil.cloneEntity(getInstance(), false));
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error(".save()", e);
            }
        }
        return ret;
    }

    @SuppressWarnings(UNCHECKED)
    public void setInstanceId(final Object id) {
        this.id = id;
        setInstance(find((Class<T>) EntityUtil.getParameterizedTypeClass(getClass()), this.id));
    }

    public Object getInstanceId() {
        return this.id;
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
        if (REMOVED.equals(ret)) {
            getMessagesHandler().clear();
            getMessagesHandler().add(MSG_REGISTRO_REMOVIDO);
        }
        return ret;
    }

    @Override
    public void onClickSearchTab() {
        newInstance();
        getGenericManager().clear();
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
        final PostgreSQLErrorCode errorCode = daoException.getPostgreSQLErrorCode();
        if (errorCode != null) {
            getMessagesHandler().clear();
            getMessagesHandler().add(daoException.getLocalizedMessage());
        }
    }

}
