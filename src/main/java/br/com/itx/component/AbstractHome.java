package br.com.itx.component;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static org.jboss.seam.faces.FacesMessages.instance;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.time.StopWatch;
import org.hibernate.AssertionFailure;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.internal.SessionImpl;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.util.Strings;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.GenericDatabaseErrorCode;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.util.ComponentUtil;

/**
 * Deprecated use {@link br.com.infox.core.crud.AbstractCrudAction} or
 * {@link br.com.infox.core.controller.AbstractController} instead
 * */
@SuppressWarnings(UNCHECKED)
@Deprecated
public abstract class AbstractHome<T> extends EntityHome<T> {

    private static final String MSG_INACTIVE_SUCCESS = "entity_inactived";
    private static final String MSG_REMOVE_ERROR = "Não foi possível excluir.";
    private static final String MSG_REGISTRO_CRIADO = "#{infoxMessages['entity_created']}";
    private static final String MSG_REGISTRO_ALTERADO = "#{infoxMessages['entity_updated']}";
    private static final String MSG_REGISTRO_CADASTRADO = "#{infoxMessages['constraintViolation.uniqueViolation']}";

    private static final LogProvider LOG = Logging.getLogProvider(AbstractHome.class);

    private static final long serialVersionUID = 1L;

    public static final String PERSISTED = "persisted";
    public static final String UPDATED = "updated";
    public static final String CONSTRAINT_VIOLATED = "constraintViolated";

    private String tab = null;
    private String goBackUrl = null;
    private String goBackId = null;
    private String goBackTab = null;
    private T oldEntity;
    
    @PostConstruct
    public void initialize() {
    	setEntityManager(BeanManager.INSTANCE.getReference(EntityManager.class));
    }

    public T getOldEntity() {
        return oldEntity;
    }

    public void setOldEntity(T oldEntity) {
        this.oldEntity = oldEntity;
    }

    protected String getInactiveSuccess() {
        return InfoxMessages.getInstance().get(MSG_INACTIVE_SUCCESS);
    }

    protected String getRemoveError() {
        return MSG_REMOVE_ERROR;
    }

    protected String getEntityExistsExceptionMessage() {
        return MSG_REGISTRO_CADASTRADO;
    }

    protected String getNonUniqueObjectExceptionMessage() {
        return MSG_REGISTRO_CADASTRADO;
    }

    protected String getConstraintViolationExceptionMessage() {
        return MSG_REGISTRO_CADASTRADO;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getGoBackUrl() {
        return goBackUrl;
    }

    public void setGoBackUrl(String goBackUrl) {
        this.goBackUrl = goBackUrl;
    }

    public void setGoBackId(String goBackId) {
        this.goBackId = goBackId;
    }

    public String getGoBackId() {
        return goBackId;
    }

    public String getGoBackTab() {
        return goBackTab;
    }

    public void setGoBackTab(String goBackTab) {
        this.goBackTab = goBackTab;
    }

    public T getDefinedInstance() {
        return isIdDefined() ? getInstance() : null;
    }

    /**
     * Cria uma instancia nova da entidade tipada.
     */
    public void newInstance() {
        oldEntity = null;
        getEntityManager().clear();
        setId(null);
        clearForm();
        instance = createInstance();
    }

    @Override
    public void setId(Object id) {
        boolean changed = id != null && !id.equals(getId());
        super.setId(id);
        if (changed) {
            try {
                updateOldInstance();
                Events.instance().raiseEvent("logLoadEventNow", instance);
            } catch (Exception e) {
                LOG.error(".setId", e);
            }
        }
    }

    private void updateOldInstance() {
        updateOldInstance(getInstance());
    }

    private void updateOldInstance(T instance) {
        try {
            oldEntity = (T) EntityUtil.cloneObject(instance, false);
        } catch (Exception e) {
            LOG.error(".updateOldInstance()", e);
        }
    }

    @Override
    public String remove() {
        String ret = null;
        try {
            ret = super.remove();
            raiseEventHome("afterRemove");
        } catch (PersistenceException e) {
            LOG.error(".remove()", e);
            DAOException daoException = new DAOException(e);
            GenericDatabaseErrorCode errorCode = daoException.getDatabaseErrorCode();
            if (errorCode != null) {
                ret = errorCode.toString();
                FacesMessages.instance().clear();
                FacesMessages.instance().add(InfoxMessages.getInstance().get(daoException.getLocalizedMessage()));
            }
        } catch (RuntimeException e) {
            FacesMessages fm = FacesMessages.instance();
            fm.add(StatusMessage.Severity.ERROR, getRemoveError());
            LOG.error(".remove()", e);
        } finally {
            rollbackTransactionIfNeeded();
        }
        if ("removed".equals(ret)) {
            FacesMessages fm = instance();
            fm.clear();
            fm.add("#{infoxMessages['entity_deleted']}");
        }
        return ret;
    }

    public String remove(T obj) {
        setInstance(obj);
        return remove();
    }

    public boolean isEditable() {
        return true;
    }

    /**
     * Chama eventos antes e depois de persistir a entidade. Caso ocorra um
     * Exception utiliza um metodo para colocar null no id da entidade
     */
    @Override
    public String persist() {
        StopWatch sw = new StopWatch();
        sw.start();
        String ret = null;
        String msg = getPersistLogMessage();
        try {
            if (beforePersistOrUpdate()) {
                ret = super.persist();
                updateOldInstance();
                afterPersistOrUpdate(ret);
                raiseEventHome("afterPersist");
            }
        } catch (EntityExistsException e) {
            instance().add(StatusMessage.Severity.ERROR, getEntityExistsExceptionMessage());
            LOG.error(getPersistLogMessage(), e);
        } catch (NonUniqueObjectException e) {
            instance().add(StatusMessage.Severity.ERROR, getNonUniqueObjectExceptionMessage());
            LOG.error(getPersistLogMessage(), e);
        } catch (ApplicationException e) {
            throw new ApplicationException("Erro: " + e.getMessage(), e);
        } catch (javax.persistence.PersistenceException e) {
            LOG.error(msg, e);
            DAOException daoException = new DAOException(e);
            GenericDatabaseErrorCode errorCode = daoException.getDatabaseErrorCode();
            if (errorCode != null) {
                ret = errorCode.toString();
                FacesMessages.instance().clear();
                FacesMessages.instance().add(InfoxMessages.getInstance().get(daoException.getLocalizedMessage()));
            }
        } catch (javax.validation.ConstraintViolationException e) {
            LOG.error(msg, e);
            FacesMessages.instance().clear();
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                FacesMessages.instance().add(violation.getPropertyPath() + ": "
                        + violation.getMessage());
            }
        } catch (Exception e) {
            instance().add(StatusMessage.Severity.ERROR, "Erro ao gravar: "
                    + e.getMessage(), e);
            LOG.error(getPersistLogMessage(), e);
        }
        if (!PERSISTED.equals(ret)) {
            rollbackTransactionIfNeeded();
            // Caso ocorra algum erro, é criada uma copia do instance sem o Id e
            // os List
            try {
                setInstance((T) EntityUtil.cloneEntity(getInstance(), false));
            } catch (Exception e) {
                LOG.warn(getPersistLogMessage()
                        + Strings.toString(getInstance()), e);
                newInstance();
            }
        }
        LOG.info(getPersistLogMessage() + sw.getTime());
        return ret;
    }

    private String getPersistLogMessage() {
        return ".persist() (" + getInstanceClassName() + "):";
    }

    /**
     * Caso o instance não seja null, possua Id não esteja managed, é dado um
     * merge.
     */
    @Override
    public boolean isManaged() {
        if (getInstance() != null && isIdDefined() && !super.isManaged()) {
            setInstance(getEntityManager().merge(getInstance()));
        }
        return super.isManaged();
    }

    /**
     * Chama eventos antes e depois de atualizar a entidade
     */
    @Override
    public String update() {
        StopWatch sw = new StopWatch();
        sw.start();
        String ret = null;
        String msg = ".update() (" + getInstanceClassName() + ")";
        try {
            if (beforePersistOrUpdate()) {
                ret = super.update();
                ret = afterPersistOrUpdate(ret);
            }
        } catch (AssertionFailure e) {
            LOG.warn(getPersistLogMessage() + e.getMessage(), e);
            ret = PERSISTED;
        } catch (EntityExistsException e) {
            instance().add(StatusMessage.Severity.ERROR, getEntityExistsExceptionMessage());
            LOG.error(msg, e);
        } catch (NonUniqueObjectException e) {
            instance().add(StatusMessage.Severity.ERROR, getNonUniqueObjectExceptionMessage());
            LOG.error(msg, e);
        } catch (javax.persistence.PersistenceException e) {
            LOG.error(msg, e);
            DAOException daoException = new DAOException(e);
            GenericDatabaseErrorCode errorCode = daoException.getDatabaseErrorCode();
            if (errorCode != null) {
                ret = errorCode.toString();
                FacesMessages.instance().clear();
                FacesMessages.instance().add(InfoxMessages.getInstance().get(daoException.getLocalizedMessage()));
            }
        } catch (javax.validation.ConstraintViolationException e) {
            LOG.error(msg, e);
            FacesMessages.instance().clear();
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                FacesMessages.instance().add(violation.getPropertyPath() + ": "
                        + violation.getMessage());
            }
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof ConstraintViolationException) {
                instance().add(StatusMessage.Severity.ERROR, "Erro de constraint: "
                        + e.getLocalizedMessage());
                LOG.warn(msg, cause);
            } else {
                instance().add(StatusMessage.Severity.ERROR, "Erro ao gravar: "
                        + e.getMessage(), e);
                LOG.error(msg, e);
            }
        }
        LOG.info(msg + sw.getTime());
        rollbackTransactionIfNeeded();
        String name = getEntityClass().getName() + "." + "afterUpdate";
        super.raiseEvent(name, getInstance(), oldEntity);
        if (ret != null) {
            updateOldInstance();
        }
        return ret;
    }

    private void raiseEventHome(String type) {
        raiseEventHome(type, null);
    }

    private void raiseEventHome(String type, T anterior) {
        String name = getEntityClass().getName() + "." + type;
        if (anterior != null) {
            super.raiseEvent(name, getInstance(), anterior);
        } else {
            super.raiseEvent(name, getInstance());
        }
    }

    /**
     * Método chamado antes de persistir ou atualizar a entidade
     * 
     * @return true se a entidade pode ser persistida ou atualizada
     */
    protected boolean beforePersistOrUpdate() {
        return true;
    }

    /**
     * Método chamado depois de persistir ou atualizar a entidade
     * 
     * @param ret é o retorno da operação de persistência
     */
    protected String afterPersistOrUpdate(String ret) {
        if (PERSISTED.equals(ret)) {
            FacesMessages.instance().clear();
            FacesMessages.instance().add(MSG_REGISTRO_CRIADO);
            return ret;
        } else if (UPDATED.equals(ret)) {
            FacesMessages.instance().clear();
            FacesMessages.instance().add(MSG_REGISTRO_ALTERADO);
            return ret;
        }
        return ret;
    }

    /**
     * Busca o componente definido por name, se nao achar, cria
     * 
     * @param name é o nome do componente
     * @return retorna o componente já no tipo esperado
     */
    public <C> C getComponent(String name) {
        return (C) Component.getInstance(name);
    }

    /**
     * Busca o componente definido por name, se nao achar, cria
     * 
     * @param name é o nome do componente
     * @param scopeType é o escopo em que o componente se encontra
     * @return retorna o componente já no tipo esperado
     */
    public <C> C getComponent(String name, ScopeType scopeType) {
        return (C) Component.getInstance(name, scopeType);
    }

    /**
     * Busca o componente definido por name
     * 
     * @param name é o nome do componente
     * @param create se true, cria o componente, senão retorna null
     * @return retorna o componente já no tipo esperado
     */
    public <C> C getComponent(String name, boolean create) {
        return (C) Component.getInstance(name, create);
    }

    public void onClickSearchTab() {
        newInstance();
    }

    public void onClickFormTab() {

    }

    /**
     * Metodo para limpar o formulario com o mesmo nome do Home, caso houver
     * algum Chamado pelo newInstance
     */
    public void clearForm() {
        StopWatch sw = new StopWatch();
        sw.start();
        StringBuilder formName = new StringBuilder(this.getClass().getSimpleName());
        formName.replace(0, 1, formName.substring(0, 1).toLowerCase());
        formName.replace(formName.length() - 4, formName.length(), "");
        formName.append("Form");
        UIComponent form = ComponentUtil.getUIComponent(formName.toString());
        ComponentUtil.clearChildren(form);
        LOG.info(".clearForm() (" + getInstanceClassName() + "): "
                + sw.getTime());
    }

    public String getHomeName() {
        String name = null;
        Name nameAnnotation = this.getClass().getAnnotation(Name.class);
        if (nameAnnotation != null) {
            name = nameAnnotation.value();
        }
        return name;
    }

    public String inactive(T instance) {
        StopWatch sw = new StopWatch();
        sw.start();
        ComponentUtil.setValue(instance, "ativo", false);
        getEntityManager().merge(instance);
        getEntityManager().flush();
        instance().add(StatusMessage.Severity.INFO, getInactiveSuccess());
        LOG.info(".inactive(" + instance + ")" + getInstanceClassName() + "): "
                + sw.getTime());
        return "update";
    }

    private String getInstanceClassName() {
        return getInstance() != null ? getInstance().getClass().getName() : "";
    }

    private void rollbackTransactionIfNeeded() {
        try {
            org.jboss.seam.transaction.UserTransaction ut = Transaction.instance();
            if (ut != null && ut.isMarkedRollback()) {
                SessionImpl session = getEntityManager().unwrap(SessionImpl.class);
                // Aborta o batch JDBC, possivelmente relacionado ao bug
                // HHH-7689. Ver https://hibernate.atlassian.net/browse/HHH-7689
                session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
                ut.rollback();
            }
        } catch (Exception e) {
            throw new ApplicationException(ApplicationException.createMessage("rollback da transação", "rollbackTransaction()", "Util", "ePP"), e);
        }
    }
    
}
