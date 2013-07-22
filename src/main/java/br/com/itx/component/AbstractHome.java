/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.component;

import static org.jboss.seam.faces.FacesMessages.instance;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.persistence.EntityExistsException;

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
import org.jboss.util.StopWatch;

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
	
	private static final String MSG_INACTIVE_SUCCESS = "Registro inativado com sucesso.";

	private static final String MSG_REMOVE_ERROR = "N�o foi poss�vel excluir.";

	private static final String MSG_REGISTRO_CADASTRADO = "#{messages['constraintViolation.registroCadastrado']}";

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
	 * Lista dos campos que n�o devem ser limpados ao realizar inclus�o no formulario
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
			/*Bug do Hibernate, esperamos a vers�o 3.5*/
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
		String msg = ".persist() (" + getInstanceClassName() + ")";
		try {
			if (beforePersistOrUpdate()) {
				ret = super.persist();
				updateOldInstance();
				afterPersistOrUpdate(ret);
				raiseEventHome("afterPersist");
			}
		} catch (EntityExistsException e) {
			instance().add(StatusMessage.Severity.ERROR, getEntityExistsExceptionMessage());
			LOG.error(".persist() (" + getInstanceClassName() + ")", e);			
		} catch (NonUniqueObjectException e) {
			instance().add(StatusMessage.Severity.ERROR, getNonUniqueObjectExceptionMessage());
			LOG.error(".persist() (" + getInstanceClassName() + ")", e);	
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
            LOG.error(".persist() (" + getInstanceClassName() + ")", e);
		} 
		if (!PERSISTED.equals(ret)) {
			 // Caso ocorra algum erro, � criada uma copia do instance sem o Id e os List
			try {
				Transaction.instance().rollback();
				setInstance((T) EntityUtil.cloneEntity(getInstance(), false));
			} catch (Exception e) {
				LOG.warn(".persist() (" + getInstanceClassName() + "): " + 
						Strings.toString(getInstance()), e);
				newInstance();
			}
		} 
		LOG.info(".persist() (" + getInstanceClassName() + "): " + sw.getTime());
		return ret;
	}
	

	/**
	 * Caso o instance n�o seja null, possua Id n�o esteja managed, � dado um merge.
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
			LOG.warn(".persist() (" + getInstanceClassName() + "): " + e.getMessage());
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
	 * M�todo chamado antes de persistir ou atualizar a entidade
	 * @return true se a entidade pode ser persistida ou atualizada
	 */
	protected boolean beforePersistOrUpdate() {
		return true;
	}
	
	/**
	 * M�todo chamado depois de persistir ou atualizar a entidade
	 * @param ret � o retorno da opera��o de persist�ncia
	 */
	protected String afterPersistOrUpdate(String ret) {
		if (PERSISTED.equals(ret)){
			FacesMessages.instance().clear();
			FacesMessages.instance().add("Registro inserido com sucesso");
			return ret;
		} else if (UPDATED.equals(ret)){
			FacesMessages.instance().clear();
			FacesMessages.instance().add("Registro alterado com sucesso");
			return ret;
		}
		return ret;
	}

	/**
	 * Busca o componente definido por name, se nao achar, cria
	 * 
	 * @param name � o nome do componente
	 * @return retorna o componente j� no tipo esperado
	 */
	public <C> C getComponent(String name) {
		return (C) Component.getInstance(name);
	}
	
	/**
	 * Busca o componente definido por name, se nao achar, cria
	 * 
	 * @param name � o nome do componente
	 * @param scopeType � o escopo em que o componente se encontra
	 * @return retorna o componente j� no tipo esperado
	 */
	public <C> C getComponent(String name, ScopeType scopeType) {
		return (C) Component.getInstance(name, scopeType);
	}	

	/**
	 * Busca o componente definido por name
	 * 
	 * @param name � o nome do componente
	 * @param create se true, cria o componente, sen�o retorna null
	 * @return retorna o componente j� no tipo esperado
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
		StopWatch sw = new StopWatch(true);
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
		StopWatch sw = new StopWatch(true);			
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
	 * Verifica se o registro est� na lista para controlar o �cone do cadeado.
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
	 * Limpa todos os campos que n�o foram marcados.
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
	 * Retorna a lista dos campos que n�o devem ser limpados.
	 * @return
	 */
	public List<String> getLockedFields() {
		return lockedFields;
	}

	/**
	 * Seta a lista dos campos que n�o devem ser limpados.
	 * @param lockedFields - Lista dos campos que n�o devem ser limpados
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
		List<T> beanList = getBeanList().list(10000);
		try {
			if (beanList == null || beanList.isEmpty()) {
				FacesMessages.instance().add(Severity.INFO, "N�o h� dados para exportar!");
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
		if (PostgreSQLErrorCode.unique_violation.toString().equals(ret)){
			message = MSG_REGISTRO_CADASTRADO;
		}
		if (message != null) {
			FacesMessages.instance().clear();
			FacesMessages.instance().add(Severity.ERROR, message);
		}
		return ret;
	}
	
}