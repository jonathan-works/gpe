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
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.annotations.ChildList;
import br.com.infox.annotations.manager.RecursiveManager;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.util.PostgreSQLErrorCode;
import br.com.infox.util.PostgreSQLExceptionManager;
import br.com.infox.util.constants.WarningConstants;
import br.com.itx.component.Util;
import br.com.itx.exception.ApplicationException;
import br.com.itx.util.AnnotationUtil;
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
	
	@In
    private PostgreSQLExceptionManager postgreSQLExceptionManager;
	
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
		} catch (EntityExistsException e) {
			instance().add(StatusMessage.Severity.ERROR, MSG_REGISTRO_CADASTRADO);
			LOG.error(msg+" (" + getObjectClassName(o) + ")", e);			
		} catch (NonUniqueObjectException e) {
			instance().add(StatusMessage.Severity.ERROR, MSG_REGISTRO_CADASTRADO);
			LOG.error(msg+" ("+ getObjectClassName(o) + ")", e);	
		} catch (ApplicationException e){
			throw new ApplicationException("Erro: " + e.getMessage());
		} catch (javax.persistence.PersistenceException e) {
            LOG.error(msg, e);
            PostgreSQLErrorCode errorCode = postgreSQLExceptionManager.discoverErrorCode(e);
            if (errorCode != null) {
                ret = tratarErrosDePersistencia(errorCode.toString());
            }
        }catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConstraintViolationException) {
				instance().add(StatusMessage.Severity.ERROR,
						"Registro já cadastrado!");
				LOG.warn(msg+" (" + getObjectClassName(o) + ")", cause);					
			} else {
				instance().add(StatusMessage.Severity.ERROR, "Erro ao gravar: " +
						e.getMessage(), e);
				LOG.error(msg+" (" + getObjectClassName(o) + ")", e);
			}
		}
		if (!PERSISTED.equals(ret) || !UPDATED.equals(ret)) {
		    Util.rollbackTransactionIfNeeded();
		}
		return ret;
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
	public String inactive(Object o) {
		if(o == null) {
			return null;
		}
		String ret = null;
		StopWatch sw = new StopWatch();
		sw.start();
		if(EntityUtil.isEntity(o)) {
			try {
				inactiveRecursive(o);
				ret = flushObject(o, false);
				instance().add(StatusMessage.Severity.INFO, "Registro inativado com sucesso.");
				LOG.info(".inactive(" + o + ")" + getObjectClassName(o) + 
						"): " + sw.getTime());
			} catch(Exception e) {
			    LOG.error(".inactive()", e);
				instance().add(StatusMessage.Severity.INFO, "Erro ao definir a propriedade " +
						"ativo na entidade: "+getObjectClassName(o)+". Verifique se esse " +
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
	@SuppressWarnings(WarningConstants.UNCHECKED)
	protected String inactiveRecursive(Object o) {
		ComponentUtil.setValue(o, "ativo", false);
		if(!RecursiveManager.isRecursive(o)) {
			return null;
		}
		List<Object> childList = (List<Object>) AnnotationUtil.
											getValue(o, ChildList.class);
		if (childList != null) {
			for (Object child : childList) {
				inactiveRecursive(child);
			}
		}
		return UPDATED;
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
	
	private String tratarErrosDePersistencia(String ret){
        String message = null;
        if (PostgreSQLErrorCode.UNIQUE_VIOLATION.toString().equals(ret)){
            message = MSG_REGISTRO_CADASTRADO;
        }
        if (message != null) {
            FacesMessages.instance().clear();
            FacesMessages.instance().add(Severity.ERROR, message);
        }
        return ret;
    }
	
}