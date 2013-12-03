package br.com.infox.core.action;

import static org.jboss.seam.faces.FacesMessages.instance;

import java.util.List;

import javax.persistence.EntityExistsException;

import org.apache.commons.lang3.time.StopWatch;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;
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
import br.com.itx.component.Util;
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
public abstract class AbstractAction <T> {

	public static final String PERSISTED = "persisted";
	public static final String UPDATED = "updated";
	public static final String REMOVED = "removed";
	
	@In
	private GenericManager genericManager;
	
	protected static final String MSG_REGISTRO_CADASTRADO = "Registro já cadastrado!";

	private static final LogProvider LOG = Logging.getLogProvider(AbstractAction.class);

	protected T find(Class<T> c, Object id) {
		return genericManager.find(c, id);
	}
	
	protected boolean contains(T t) {
		return genericManager.contains(t);
	}
	
	/**
	 * Método que realiza persist ou update, dependendo do parametro 
	 * informado. Foi criado para não replicar o código com os tratamentos
	 * de exceções, que é o mesmo para as duas ações.
	 * @param isPersist true se deve ser persistida a instancia.
	 * @return
	 */
	@Transactional
	private String flushObject(T t, boolean isPersist) {
		String ret = null;
		String msg = isPersist ? "persist()" : "update()";
		try {
			if(isPersist) {
				genericManager.persist(t);
				ret = PERSISTED;
			} else {
				genericManager.update(t);
				ret = UPDATED;
			}
		} catch (EntityExistsException e) {
			instance().add(StatusMessage.Severity.ERROR, MSG_REGISTRO_CADASTRADO);
			LOG.error(msg+" (" + getObjectClassName(t) + ")", e);			
		} catch (NonUniqueObjectException e) {
			instance().add(StatusMessage.Severity.ERROR, MSG_REGISTRO_CADASTRADO);
			LOG.error(msg+" ("+ getObjectClassName(t) + ")", e);	
		} catch (ApplicationException e){
			throw new ApplicationException("Erro: " + e.getMessage());
		} catch (javax.persistence.PersistenceException e) {
            LOG.error(msg, e);
            DAOException daoException = new DAOException(e);
			PostgreSQLErrorCode errorCode = daoException.getPostgreSQLErrorCode();
            if (errorCode != null) {
            	ret = errorCode.toString();
            	FacesMessages.instance().clear();
            	FacesMessages.instance().add(daoException.getLocalizedMessage());
            }
        } catch (DAOException daoException) {
            LOG.error(msg, daoException);
            PostgreSQLErrorCode errorCode = daoException.getPostgreSQLErrorCode();
            if (errorCode != null) {
                ret = errorCode.toString();
                FacesMessages.instance().clear();
                FacesMessages.instance().add(daoException.getLocalizedMessage());
            }
        } catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConstraintViolationException) {
				instance().add(StatusMessage.Severity.ERROR,
						"Registro já cadastrado!");
				LOG.warn(msg+" (" + getObjectClassName(t) + ")", cause);					
			} else {
				instance().add(StatusMessage.Severity.ERROR, "Erro ao gravar: " +
						e.getMessage(), e);
				LOG.error(msg+" (" + getObjectClassName(t) + ")", e);
			}
		}
		if (!(PERSISTED.equals(ret) || UPDATED.equals(ret))) {
		    Util.rollbackTransactionIfNeeded();
		}
		return ret;
	}
	
	/**
	 * Invoca o serviço de persistência para a variável instance.
	 * @return "persisted" se inserido com sucesso.
	 */
	protected String persist(T t) {
		return flushObject(t, true);
	}
	
	/**
	 * Invoca o serviço de persistência para a variável instance.
	 * @return "updated" se alterado com sucesso.
	 */
	protected String update(T t) {
		return flushObject(t, false);		
	}
	
	/**
	 * Método sobrecarregado quando for necessário excluir uma 
	 * entidade já gerênciável.
	 * @param <T>
	 * @param t entidade já gerênciada pelo Hibernate.
	 * @return "removed" se removido com sucesso.
	 */
	@Transactional
	public String remove(T t) {
		String ret = null;
		try {
			genericManager.remove(t);
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
	 * @param t objeto da entidade que se deseja invativar o registro.
	 * @return "updated" se inativado com sucesso.
	 */
	@Transactional
	public String inactive(T t) {
		if(t == null) {
			return null;
		}
		String ret = null;
		StopWatch sw = new StopWatch();
		sw.start();
		if(EntityUtil.isEntity(t)) {
			try {
			    if (t instanceof Recursive) {
			        inactiveRecursive((Recursive<?>)t);
			    } else {
			    	ComponentUtil.setValue(t, "ativo", false);
			    }
				ret = flushObject(t, false);
				instance().add(StatusMessage.Severity.INFO, "Registro inativado com sucesso.");
				LOG.info(".inactive(" + t + ")" + getObjectClassName(t) + 
						"): " + sw.getTime());
			} catch(Exception e) {
			    LOG.error(".inactive()", e);
				instance().add(StatusMessage.Severity.INFO, "Erro ao definir a propriedade " +
						"ativo na entidade: "+getObjectClassName(t)+". Verifique se esse " +
						"campo existe.");
			}
		} else {
			instance().add(StatusMessage.Severity.INFO, "Objeto informado não é uma entidade.");
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
	 * @param t Objeto
	 * @return String referente ao nome da classe do objeto.
	 */
	private String getObjectClassName(T t) {
		return t != null ? t.getClass().getName() : "";
	}
	
	protected GenericManager getGenericManager() {
        return genericManager;
    }
	
}