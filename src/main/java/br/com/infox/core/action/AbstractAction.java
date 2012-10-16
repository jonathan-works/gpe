package br.com.infox.core.action;

import static org.jboss.seam.faces.FacesMessages.instance;

import java.util.List;

import javax.persistence.EntityExistsException;

import org.hibernate.AssertionFailure;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.seam.annotations.In;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.util.StopWatch;

import br.com.infox.annotations.ChildList;
import br.com.infox.annotations.manager.RecursiveManager;
import br.com.infox.core.manager.GenericManager;
import br.com.itx.exception.AplicationException;
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

	/**
	 * Tipos de retornos dos métodos de persistência e alteração dos
	 * dados.
	 */
	public static final String PERSISTED = "persisted";
	public static final String UPDATED = "updated";
	public static final String REMOVED = "removed";
	
	@In
	protected GenericManager genericManager;
	
	/**
	 * Mensagem default para um registro já cadastrado.
	 */
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
		} catch (AssertionFailure e) {
			/* Esperamos a versão 3.5 para resolver o bug do AssertionFailure onde 
			 * o hibernate consegue persistir com sucesso, mas lança um erro. =[ */
			LOG.warn(msg+" (" + getObjectClassName(o) + "): " + e.getMessage());
			Events.instance().raiseEvent("afterPersist");
			ret = PERSISTED;
		} catch (EntityExistsException e) {
			instance().add(StatusMessage.Severity.ERROR, MSG_REGISTRO_CADASTRADO);
			LOG.error(msg+" (" + getObjectClassName(o) + ")", e);			
		} catch (NonUniqueObjectException e) {
			instance().add(StatusMessage.Severity.ERROR, MSG_REGISTRO_CADASTRADO);
			LOG.error(msg+" ("+ getObjectClassName(o) + ")", e);	
		} catch (AplicationException e){
			throw new AplicationException("Erro: " + e.getMessage());
		} catch (Exception e) {
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
		} catch (AssertionFailure af) {
			/* Esperamos a versão 3.5 para resolver o bug do AssertionFailure onde 
			 * o hibernate consegue persistir com sucesso, mas lança um erro. =[ */
		} catch (RuntimeException e) {
			FacesMessages fm = FacesMessages.instance();
			fm.add(StatusMessage.Severity.ERROR, "Não foi possível excluir.");
			e.printStackTrace();
		} catch (Exception e) {
			FacesMessages fm = FacesMessages.instance();
			fm.add(StatusMessage.Severity.ERROR, "Não foi possível excluir.");
			e.printStackTrace();
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
		StopWatch sw = new StopWatch(true);
		if(EntityUtil.isEntity(o)) {
			try {
				inactiveRecursive(o);
				ret = flushObject(o, false);
				instance().add(StatusMessage.Severity.INFO, "Registro inativado com sucesso.");
				LOG.info(".inactive(" + o + ")" + getObjectClassName(o) + 
						"): " + sw.getTime());
			} catch(Exception e) {
				e.printStackTrace();
				instance().add(StatusMessage.Severity.INFO, "Erro ao definir a propriedade " +
						"ativo na entidade: "+getObjectClassName(o)+". Verifique se esse " +
						"campo existe.");
			}
		} else {
			instance().add(StatusMessage.Severity.INFO, "Objecto informado não é uma entidade.");
		}
		return ret;
	}
	
	/**
	 * Inativa todos os registros contidos na árvore abaixo do 
	 * parametro informado.
	 * @param o Registro que deseja inativar.
	 */
	protected void inactiveRecursive(Object o) {
		ComponentUtil.setValue(o, "ativo", false);
		if(!RecursiveManager.isRecursive(o)) {
			return;
		}
		List<Object> childList = (List<Object>) AnnotationUtil.
											getValue(o, ChildList.class);
		if (childList != null) {
			for (Object child : childList) {
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
	
}