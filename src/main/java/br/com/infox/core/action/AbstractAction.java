package br.com.infox.core.action;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.constants.WarningConstants;
import br.com.infox.core.dao.DAO;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.Recursive;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

/**
 * Classe abstrata que possui algumas implementações comuns aos beans, como
 * chamada aos serviços de persistencia (já com tratamento para as mensagens de
 * erro), para inserir, buscar, remover e atualizar dados através do
 * entityManager.
 * 
 * @author Daniel
 * @param <K>
 * 
 */
@Transactional
public abstract class AbstractAction<T, M extends Manager<? extends DAO<T>, T>> implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String PERSISTED = "persisted";
    public static final String UPDATED = "updated";
    public static final String REMOVED = "removed";

    private M manager;

    protected static final String MSG_REGISTRO_CADASTRADO = "Registro já cadastrado!";

    private static final LogProvider LOG = Logging.getLogProvider(AbstractAction.class);

    @In
    private ActionMessagesService actionMessagesService;
    @In
    protected InfoxMessages infoxMessages;

    @SuppressWarnings(WarningConstants.UNCHECKED)
    @Create
    public void init() {
        this.manager = (M) Component.getInstance(getManagerName());
    }

    protected T find(Object id) {
        return getManager().find(id);
    }

    protected boolean contains(T t) {
        return getManager().contains(t);
    }

    /**
     * Método que realiza persist ou update, dependendo do parametro informado.
     * Foi criado para não replicar o código com os tratamentos de exceções, que
     * é o mesmo para as duas ações.
     * 
     * @param isPersist true se deve ser persistida a instancia.
     * @return
     */
    @Transactional
    private String flushObject(T t, boolean isPersist) {
        String ret = null;
        try {
            if (isPersist) {
                getManager().persist(t);
                ret = PERSISTED;
            } else {
                getManager().update(t);
                ret = UPDATED;
            }
        } catch (Exception e) {
        	final String msg = isPersist ? "persist()" : "update()";
            LOG.error(msg, e);
            actionMessagesService.handleGenericException(e,  "Registro alterado por outro usuário, tente novamente");
            if (e instanceof DAOException && ((DAOException) e).getDatabaseErrorCode() != null) {
            	ret = ((DAOException) e).getDatabaseErrorCode().toString();
            }
        }

        return ret;
    }

    /**
     * Invoca o serviço de persistência para a variável instance.
     * 
     * @return "persisted" se inserido com sucesso.
     */
    protected String persist(T t) {
        return flushObject(t, true);
    }

    /**
     * Invoca o serviço de persistência para a variável instance.
     * 
     * @return "updated" se alterado com sucesso.
     */
    protected String update(T t) {
        return flushObject(t, false);
    }

    /**
     * Método sobrecarregado quando for necessário excluir uma entidade já
     * gerênciável.
     * 
     * @param <T>
     * @param t entidade já gerênciada pelo Hibernate.
     * @return "removed" se removido com sucesso.
     */
    @Transactional
    public String remove(T t) {
        String ret = null;
        try {
            getManager().remove(t);
            ret = REMOVED;
        } catch (DAOException daoException) {
            LOG.error(".remove()", daoException);
            ret = actionMessagesService.handleDAOException(daoException);
        }
        return ret;
    }

    /**
     * Inativa o registro informado.
     * 
     * @param t objeto da entidade que se deseja invativar o registro.
     * @return "updated" se inativado com sucesso.
     */
    @Transactional
    public String inactive(T t) {
        if (t == null) {
            return null;
        }
        String ret = null;
        StopWatch sw = new StopWatch();
        sw.start();
        final StatusMessages messages = getMessagesHandler();
        if (EntityUtil.isEntity(t)) {
            final String objectClassName = getObjectClassName(t);
            try {
                if (t instanceof Recursive) {
                    inactiveRecursive((Recursive<?>) t);
                } else {
                    ComponentUtil.setValue(t, "ativo", false);
                }
                ret = flushObject(t, false);
                
                messages.add(infoxMessages.get("entity_inactived"));
                final String message = format(".inactive({0}){1}): {2}", t, objectClassName, sw.getTime());
                LOG.info(message);
            } catch (final Exception e) {
                LOG.error(".inactive()", e);
                final String message = format(infoxMessages.get("entity.ativo.error"), objectClassName);
                messages.add(StatusMessage.Severity.ERROR, message);
            }
        } else {
            messages.add("Objeto informado não é uma entidade.");
        }
        return ret;
    }

    protected StatusMessages getMessagesHandler() {
        return FacesMessages.instance();
    }

    /**
     * Inativa todos os registros contidos na árvore abaixo do parametro
     * informado.
     * 
     * @param o Registro que deseja inativar.
     * @return
     */
    @Transactional
    protected <R extends Recursive<R>> void inactiveRecursive(Recursive<R> o) {
        ComponentUtil.setValue(o, "ativo", false);
        List<R> childList = o.getChildList();
        if (childList != null) {
            for (R child : childList) {
                inactiveRecursive(child);
            }
        }
    }

    /**
     * Obtem o nome da Classe do objeto informado.
     * 
     * @param t Objeto
     * @return String referente ao nome da classe do objeto.
     */
    private String getObjectClassName(T t) {
        return t != null ? t.getClass().getName() : "";
    }

    protected M getManager() {
        return manager;
    }

    @SuppressWarnings(WarningConstants.UNCHECKED)
    protected String getManagerName() {
        ParameterizedType superType = (ParameterizedType) getClass().getGenericSuperclass();
        Class<M> managerClass = (Class<M>) superType.getActualTypeArguments()[1];
        Name name = managerClass.getAnnotation(Name.class);
        return name.value();
    }
}
