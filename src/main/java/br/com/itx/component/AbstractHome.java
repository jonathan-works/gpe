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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.persistence.EntityExistsException;

import org.apache.commons.lang3.time.StopWatch;
import org.hibernate.AssertionFailure;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.util.Strings;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.util.PostgreSQLErrorCode;
import br.com.infox.util.PostgreSQLExceptionManager;
import br.com.itx.component.grid.GridQuery;
import br.com.itx.exception.AplicationException;
import br.com.itx.exception.ExcelExportException;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;
import br.com.itx.util.ExcelExportUtil;

@SuppressWarnings("unchecked")
public abstract class AbstractHome<T> extends EntityHome<T> {
	
	private static final int TAMANHO_XLS_PADRAO = 10000;

    private static final String MSG_INACTIVE_SUCCESS = "Registro inativado com sucesso.";

	private static final String MSG_REMOVE_ERROR = "Não foi possível excluir.";

	private static final String MSG_REGISTRO_CADASTRADO = "#{messages['constraintViolation.registroCadastrado']}";
	private static final String MSG_REGISTRO_CRIADO = "#{messages['entity_created']}";
	private static final String MSG_REGISTRO_ALTERADO = "#{messages['entity_updated']}";

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
	
	@In
	private PostgreSQLExceptionManager postgreSQLExceptionManager;
	
	public T getOldEntity() {
		return oldEntity;
	}
	public void setOldEntity(T oldEntity)	{
		this.oldEntity = oldEntity;
	}

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
		
		getEntityManager().clear();
				
		if(lockedFields.size() > 0){
			try {
				clearUnlocked();
			} catch (Exception e) {
			    LOG.error(".newInstance()", e);
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
		} catch (AssertionFailure af) {
			/*Bug do Hibernate, esperamos a versão 3.5*/
		} catch (RuntimeException e) {
			FacesMessages fm = FacesMessages.instance();
			fm.add(StatusMessage.Severity.ERROR, getRemoveError());
			LOG.error(".remove()", e);
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
		} catch (AplicationException e){
			throw new AplicationException("Erro: " + e.getMessage(), e);
		} catch (javax.persistence.PersistenceException e) {
            LOG.error(msg, e);
            PostgreSQLErrorCode errorCode = postgreSQLExceptionManager.discoverErrorCode(e);
            if (errorCode != null) {
            	ret = tratarErrosDePersistencia(errorCode.toString());
            }
        } catch (Exception e) {
            instance().add(StatusMessage.Severity.ERROR,
                    "Erro ao gravar: " + e.getMessage(), e);
            LOG.error(getPersistLogMessage(), e);
		} 
		if (!PERSISTED.equals(ret)) {
			 // Caso ocorra algum erro, é criada uma copia do instance sem o Id e os List
			try {
				Transaction.instance().rollback();
				setInstance((T) EntityUtil.cloneEntity(getInstance(), false));
			} catch (Exception e) {
				LOG.warn(getPersistLogMessage() + Strings.toString(getInstance()), e);
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
			LOG.warn(getPersistLogMessage()  + e.getMessage());
			ret = PERSISTED;
		} catch (EntityExistsException e) {
			instance().add(StatusMessage.Severity.ERROR, getEntityExistsExceptionMessage());
			LOG.error(msg, e);			
		} catch (NonUniqueObjectException e) {
			instance().add(StatusMessage.Severity.ERROR, getNonUniqueObjectExceptionMessage());
			LOG.error(msg, e);	
		} catch (javax.persistence.PersistenceException e) {
			LOG.error(msg, e);
		    PostgreSQLErrorCode errorCode = postgreSQLExceptionManager.discoverErrorCode(e);
            if (errorCode != null) {
            	ret = tratarErrosDePersistencia(errorCode.toString());
            }
            
		} catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConstraintViolationException) {
				instance().add(StatusMessage.Severity.ERROR, "Erro de constraint: " + e.getLocalizedMessage());
				LOG.warn(msg, cause);					
			} else {
				instance().add(StatusMessage.Severity.ERROR, "Erro ao gravar: " +
						e.getMessage(), e);
				LOG.error(msg, e);
			}
		}  
		LOG.info(msg + sw.getTime());		
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
		if (PERSISTED.equals(ret)){
			FacesMessages.instance().clear();
			FacesMessages.instance().add(MSG_REGISTRO_CRIADO);
			return ret;
		} else if (UPDATED.equals(ret)){
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

	public void onClickSearchTab(){
		newInstance();
	}
	
	public void onClickFormTab(){
		
	}
	
	/**
	 * Metodo para limpar o formulario com o mesmo nome do Home, caso houver algum
	 * Chamado pelo newInstance
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
		LOG.info(".clearForm() (" + getInstanceClassName() + 
				"): " + sw.getTime());		
	}
	
	public void refreshGrid(String gridId) {
		StopWatch sw = new StopWatch();
		sw.start();
		GridQuery<?> g = getComponent(gridId, false);
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
		LOG.info(sb.toString());			
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
		StopWatch sw = new StopWatch();
		sw.start();
		ComponentUtil.setValue(instance, "ativo", false);
		getEntityManager().merge(instance);
		getEntityManager().flush();
		instance().add(StatusMessage.Severity.INFO, getInactiveSuccess());
		LOG.info(".inactive(" + instance + ")" + getInstanceClassName() + 
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
	@SuppressWarnings("rawtypes")
	public void toggleFields(String idField, AbstractHome homeRef){
		if(homeRef.getLockedFields().contains(idField)) {
			homeRef.getLockedFields().remove(idField);
		} else {
			homeRef.getLockedFields().add(idField);
		}
	}
	
	/**
	 * Limpa todos os campos que não foram marcados.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */
	public void clearUnlocked() throws InstantiationException, IllegalAccessException, InvocationTargetException {
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
	
	public String getTemplate(){
		return null;
	}
	public String getDownloadXlsName(){
		return null;
	}
	
	public EntityList<T> getBeanList() {
		return null;
	}
	
	public void exportarXLS() {
		List<T> beanList = getBeanList().list(TAMANHO_XLS_PADRAO);
		try {
			if (beanList == null || beanList.isEmpty()) {
				FacesMessages.instance().add(Severity.INFO, "Não há dados para exportar!");
			} else {
				exportarXLS(getTemplate(), beanList);
			}
		} catch (ExcelExportException e) {
			FacesMessages.instance().add(Severity.ERROR, "Erro ao exportar arquivo." + e.getMessage());
		}	
	}
	
	private void exportarXLS (String template, List<T> beanList) throws ExcelExportException {
		String urlTemplate = new Util().getContextRealPath() + template;
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuilder className = new StringBuilder(getEntityClass().getSimpleName());
		className = className.replace(0, 1, className.substring(0, 1).toLowerCase());
		map.put(className.toString(), beanList);
		ExcelExportUtil.downloadXLS(urlTemplate, map, getDownloadXlsName());
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