/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.component;

import static org.jboss.seam.faces.FacesMessages.instance;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.persistence.EntityExistsException;

import org.hibernate.AssertionFailure;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;
import org.jboss.util.StopWatch;

import br.com.itx.component.grid.GridQuery;
import br.com.itx.exception.AplicationException;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@SuppressWarnings("unchecked")
public abstract class AbstractHome<T> extends EntityHome<T> {
	
	private static final String MSG_INACTIVE_SUCCESS = "Registro inativado com sucesso.";

	private static final String MSG_REMOVE_ERROR = "Não foi possível excluir.";

	private static final String MSG_REGISTRO_CADASTRADO = "Registro já cadastrado!";

	private static final LogProvider log = Logging.getLogProvider(AbstractHome.class);

	private static final long serialVersionUID = 1L;
	
	public static final String PERSISTED = "persisted";
	public static final String UPDATED = "updated";
	
	private String tab = null;
	private String goBackUrl = null;
	private String goBackId = null;
	private String goBackTab = null;
	private T oldEntity;
	
	protected String getInactiveSuccess() {
		return MSG_INACTIVE_SUCCESS;
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
	
	/**
	 * Lista dos campos que não devem ser limpados ao realizar inclusão no formulario
	 */
	private List<String> lockedFields = new ArrayList<String>();
	
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
				
//  ATENCAO!!! UM MÉTODO NEWINSTANCE NAO DEVE DAR REFRESH E SIM CLEAR
//		PARA FAZER AS ENTIDADES SEREM LIDAS NOVAMENTE, QUANDO FOR NECESSARIO.
//  	SE HOUVER QUALQUER PROBLEMA AVISE PD&I MAS NAO REMOVA ESSE COMENTARIO
//		
		if (super.isManaged()) {
			getEntityManager().clear();
		}
		if(lockedFields.size() > 0){
			try {
				clearUnlocked();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {			
			setId(null);
			clearForm();
			instance = createInstance();
		}
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
				e.printStackTrace();
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
			e.printStackTrace();
		} 
	}	

	@Override
	public String remove() {
		String ret = null;
		try {
			ret = super.remove();
			raiseEventHome("afterRemove");
		} catch (AssertionFailure af) {
			/*Bug do Hibernate, esperamos a versão 3.5*/
		} catch (RuntimeException e) {
			FacesMessages fm = FacesMessages.instance();
			fm.add(StatusMessage.Severity.ERROR, getRemoveError());
			e.printStackTrace();
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
	 * Chama eventos antes e depois de persistir a entidade.
	 * Caso ocorra um Exception utiliza um metodo para
	 * colocar null no id da entidade 
	 */
	@Override
	public String persist() {
		StopWatch sw = new StopWatch(true);
		String ret = null;
		try {
			if (beforePersistOrUpdate()) {
				ret = super.persist();
				updateOldInstance();
				afterPersistOrUpdate(ret);
				raiseEventHome("afterPersist");
			}
		} catch (AssertionFailure e) {
			//Resolver o bug do AssertionFailure onde o hibernate consegue persistir com sucesso,
			//mas lança um erro.
			log.warn(".persist() (" + getInstanceClassName() + "): " + e.getMessage());
			ret = "persisted";
			updateOldInstance();
			raiseEventHome("afterPersist");
		} catch (EntityExistsException e) {
			instance().add(StatusMessage.Severity.ERROR, getEntityExistsExceptionMessage());
			log.error(".persist() (" + getInstanceClassName() + ")", e);			
		} catch (NonUniqueObjectException e) {
			instance().add(StatusMessage.Severity.ERROR, getNonUniqueObjectExceptionMessage());
			log.error(".persist() (" + getInstanceClassName() + ")", e);	
		} catch (AplicationException e){
			throw new AplicationException("Erro: " + e.getMessage(), e);
		} catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConstraintViolationException) {
				instance().add(StatusMessage.Severity.ERROR, getConstraintViolationExceptionMessage());
				log.warn(".persist() (" + getInstanceClassName() + ")", cause);					
			} else {
				instance().add(StatusMessage.Severity.ERROR, "Erro ao gravar: " +
						e.getMessage(), e);
				log.error(".persist() (" + getInstanceClassName() + ")", e);
			}
		} 
		if (ret == null) {
			 // Caso ocorra algum erro, é criada uma copia do instance sem O Id e os List
			try {
				setInstance((T) EntityUtil.cloneEntity(getInstance(), false));
			} catch (Exception e) {
				log.warn(".persist() (" + getInstanceClassName() + "): " + 
						Strings.toString(getInstance()), e);
				newInstance();
			}
		} 
		log.info(".persist() (" + getInstanceClassName() + "): " + sw.getTime());
		return ret;
	}
	

	/**
	 * Caso o instance não seja null, possua Id não esteja managed, é dado um merge.
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
		StopWatch sw = new StopWatch(true);		
		String ret = null;
		String msg = ".update() (" + getInstanceClassName() + ")";
		try {
			if (beforePersistOrUpdate()) {
				ret = super.update();
				ret = afterPersistOrUpdate(ret);
			}
		} catch (AssertionFailure e) {
			//Resolver o bug do AssertionFailure onde o hibernate consegue persistir com sucesso,
			//mas lança um erro.
			log.warn(".persist() (" + getInstanceClassName() + "): " + e.getMessage());
			ret = "persisted";
		} catch (EntityExistsException e) {
			instance().add(StatusMessage.Severity.ERROR, getEntityExistsExceptionMessage());
			log.error(msg, e);			
		} catch (NonUniqueObjectException e) {
			instance().add(StatusMessage.Severity.ERROR, getNonUniqueObjectExceptionMessage());
			log.error(msg, e);			
		} catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConstraintViolationException) {
				instance().add(StatusMessage.Severity.ERROR, "Erro de constraint: " + e.getLocalizedMessage());
				log.warn(msg, cause);					
			} else {
				instance().add(StatusMessage.Severity.ERROR, "Erro ao gravar: " +
						e.getMessage(), e);
				log.error(msg, e);
			}
		}  
		log.info(msg + sw.getTime());		
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
	 * @return true se a entidade pode ser persistida ou atualizada
	 */
	protected boolean beforePersistOrUpdate() {
		return true;
	}
	
	/**
	 * Método chamado depois de persistir ou atualizar a entidade
	 * @param ret é o retorno da operação de persistência
	 */
	protected String afterPersistOrUpdate(String ret) {
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

	/**
	 * Evento acionado quando o usuário entra na aba de pesquisa.
	 */
	public void onClickSearchTab() {
		newInstance();
	}
	
	public void onClickFormTab() {
	}

	/**
	 * Metodo para limpar o formulario com o mesmo nome do Home, caso houver algum
	 * Chamado pelo newInstance
	 */
	public void clearForm() {
		StopWatch sw = new StopWatch(true);
		StringBuilder formName = new StringBuilder(this.getClass().getSimpleName());
		formName.replace(0, 1, formName.substring(0, 1).toLowerCase());
		formName.replace(formName.length() - 4, formName.length(), "");
		formName.append("Form");
		UIComponent form = ComponentUtil.getUIComponent(formName.toString());
		ComponentUtil.clearChildren(form);
		log.info(".clearForm() (" + getInstanceClassName() + 
				"): " + sw.getTime());		
	}
	
	public void refreshGrid(String gridId) {
		StopWatch sw = new StopWatch(true);		
		GridQuery g = getComponent(gridId, false);
		if (g != null) {
			g.refresh();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(".refreshGrid (");
		sb.append(gridId);
		sb.append(") ");
		sb.append(getInstanceClassName());
		sb.append("): ");
		sb.append(sw.getTime());
		log.info(sb.toString());			
	}
	
	public String getHomeName() {
		String name = null;
		Name nameAnnotation = this.getClass().getAnnotation(Name.class);
		if (nameAnnotation != null) {
			name = nameAnnotation.value();
		}
		return name ;
	}
	
	public String inactive(T instance) {
		StopWatch sw = new StopWatch(true);			
		ComponentUtil.setValue(instance, "ativo", false);
		getEntityManager().merge(instance);
		getEntityManager().flush();
		instance().add(StatusMessage.Severity.ERROR, getInactiveSuccess());
		log.info(".inactive(" + instance + ")" + getInstanceClassName() + 
				"): " + sw.getTime());		
		return "update";
	}

	private String getInstanceClassName() {
		return getInstance() != null ? getInstance().getClass().getName() : "";
	}
	
	/**
	 * Verifica se o registro está na lista para controlar o ícone do cadeado.
	 * @param idField - Nome do atributo da Entity referente ao campo
	 * @param homeRef - Home da Entity do atributo informado
	 */
	public void toggleFields(String idField, AbstractHome homeRef){
		if(homeRef.getLockedFields().contains(idField)) {
			homeRef.getLockedFields().remove(idField);
		} else {
			homeRef.getLockedFields().add(idField);
		}
	}
	
	/**
	 * Limpa todos os campos que não foram marcados.
	 * @throws Exception
	 */
	public void clearUnlocked() throws Exception {
		PropertyDescriptor[] pds = ComponentUtil.getPropertyDescriptors(getInstance());	 
		T t = (T) getInstance().getClass().newInstance();
		for (PropertyDescriptor pd : pds) {
			if(lockedFields.contains(pd.getName())) {
				ComponentUtil.setValue(t, pd.getName(), pd.getReadMethod().invoke(getInstance()));
			}
		}
		setId(null);
		clearForm();
		instance = t;
	}
	
	/**
	 * Retorna a lista dos campos que não devem ser limpados.
	 * @return
	 */
	public List<String> getLockedFields() {
		return lockedFields;
	}

	/**
	 * Seta a lista dos campos que não devem ser limpados.
	 * @param lockedFields - Lista dos campos que não devem ser limpados
	 */
	public void setLockedFields(List<String> lockedFields) {
		this.lockedFields = lockedFields;
	}
	
}