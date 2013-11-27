package br.com.infox.core.action;

import java.text.MessageFormat;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.exception.ApplicationException;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.PostgreSQLErrorCode;
import br.com.infox.core.persistence.Recursive;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

/**
 * Classe abstrata que possui algumas implementações comuns aos beans, 
 * como chamada aos serviços de persistencia (já com tratamento para as 
 * mensagens de erro), para inserir, buscar, remover e atualizar dados
 * através do entityManager.
 * @author Daniel
 *
 */
public abstract class AbstractAction {

	public static final String PERSISTED = "persisted";
	public static final String UPDATED = "updated";
	public static final String REMOVED = "removed";
	
	@In
	private GenericManager genericManager;
	
	protected static final String MSG_REGISTRO_CADASTRADO = "Registro já cadastrado!";

	private static final LogProvider LOG = Logging.getLogProvider(AbstractAction.class);

	protected <T> T find(Class<T> c, Object id) {
		return genericManager.find(c, id);
	}
	
	protected boolean contains(Object o) {
		return genericManager.contains(o);
	}
	
	/**
	 * Método que realiza persist ou update, dependendo do parametro 
	 * informado. Foi criado para não replicar o código com os tratamentos
	 * de exceções, que é o mesmo para as duas ações.
	 * @param isPersist true se deve ser persistida a instancia.
	 * @return
	 */
	@Transactional
	private String flushObject(Object o, boolean isPersist) {
		String ret = null;
		String msg = isPersist ? "persist()" : "update()";
		try {
			if(isPersist) {
				genericManager.persist(o);
				ret = PERSISTED;
			} else {
				genericManager.update(o);
				ret = UPDATED;
			}
        } catch (DAOException daoException) {
        	LOG.error(msg, daoException);
        	ret = handleDAOException(daoException);
        }
		
		return ret;
	}
	
	private String handleBeanViolationException(ConstraintViolationException e) {
		FacesMessages.instance().clear();
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			String message = MessageFormat.format("{0}: {1}", violation.getPropertyPath(), violation.getMessage());
			FacesMessages.instance().add(message);
		}
		return null;
	}

	private String handleDAOException(DAOException daoException) {
		PostgreSQLErrorCode errorCode = daoException.getPostgreSQLErrorCode();
		if (errorCode != null) {
			String ret = errorCode.toString();
			FacesMessages.instance().clear();
			FacesMessages.instance().add(daoException.getLocalizedMessage());
			return ret;
		} else {
			Throwable cause = daoException.getCause();
			if (cause instanceof ConstraintViolationException) {
				return handleBeanViolationException((ConstraintViolationException) cause);
			} else {
				FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Erro ao gravar: " + cause.getMessage(), cause);
			}
		}
		return null;
	}

	/**
	 * Invoca o serviço de persistência para a variável instance.
	 * @return "persisted" se inserido com sucesso.
	 */
	protected String persist(Object o) {
		return flushObject(o, true);
	}
	
	/**
	 * Invoca o serviço de persistência para a variável instance.
	 * @return "updated" se alterado com sucesso.
	 */
	protected String update(Object o) {
		return flushObject(o, false);		
	}
	
	/**
	 * Método sobrecarregado quando for necessário excluir uma 
	 * entidade já gerênciável.
	 * @param obj entidade já gerênciada pelo Hibernate.
	 * @return "removed" se removido com sucesso.
	 */
	@Transactional
	public String remove(Object obj) {
		String ret = null;
		try {
			genericManager.remove(obj);
			ret = REMOVED;
		} catch (RuntimeException e) {
			FacesMessages fm = FacesMessages.instance();
			fm.add(StatusMessage.Severity.ERROR, "Não foi possível excluir.");
			LOG.error(".remove()", e);
		} catch (Exception e) {
			FacesMessages fm = FacesMessages.instance();
			fm.add(StatusMessage.Severity.ERROR, "Não foi possível excluir.");
			LOG.error(".remove()", e);
		}
		return ret;
	}

	/**
	 * Inativa o registro informado.
	 * @param o objeto da entidade que se deseja invativar o registro.
	 * @return "updated" se inativado com sucesso.
	 */
	@Transactional
	public String inactive(Object o) {
		if(o == null) {
			return null;
		}
		String ret = null;
		StopWatch sw = new StopWatch();
		sw.start();
		if(EntityUtil.isEntity(o)) {
			try {
			    if (o instanceof Recursive) {
			        inactiveRecursive((Recursive<?>)o);
			    } else {
			    	ComponentUtil.setValue(o, "ativo", false);
			    }
				ret = flushObject(o, false);
				FacesMessages.instance().add(StatusMessage.Severity.INFO, "Registro inativado com sucesso.");
				LOG.info(".inactive(" + o + ")" + getObjectClassName(o) + 
						"): " + sw.getTime());
			} catch(Exception e) {
			    LOG.error(".inactive()", e);
				FacesMessages.instance().add(StatusMessage.Severity.INFO, "Erro ao definir a propriedade " +
						"ativo na entidade: "+getObjectClassName(o)+". Verifique se esse " +
						"campo existe.");
			}
		} else {
			FacesMessages.instance().add(StatusMessage.Severity.INFO, "Objeto informado não é uma entidade.");
		}
		return ret;
	}
	
	/**
	 * Inativa todos os registros contidos na árvore abaixo do 
	 * parametro informado.
	 * @param o Registro que deseja inativar.
	 * @return 
	 */
	@Transactional
	@SuppressWarnings(WarningConstants.UNCHECKED)
	protected void inactiveRecursive(Recursive<?> o) {
		ComponentUtil.setValue(o, "ativo", false);
		List<Recursive<?>> childList = (List<Recursive<?>>) o.getChildList();
		if (childList != null) {
			for (Recursive<?> child : childList) {
				inactiveRecursive(child);
			}
		}
	}
	
	/**
	 * Obtem o nome da Classe do objeto informado.
	 * @param o Objeto
	 * @return String referente ao nome da classe do objeto.
	 */
	private String getObjectClassName(Object o) {
		return o != null ? o.getClass().getName() : "";
	}
	
	protected GenericManager getGenericManager() {
        return genericManager;
    }
	
}