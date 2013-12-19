package br.com.infox.core.crud;

import static br.com.infox.core.constants.WarningConstants.*;

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
 * possibilitando o controle de abas e gerenciamento de instancias para
 * as páginas de cadastros básicos.
 * @author Daniel
 *
 * CRUD = Create, Retrieve, Update, Delete.
 *
 * @param <T> Entity principal, onde devem ser realizadas as 
 * alterações.
 */
@Scope(ScopeType.CONVERSATION)
public abstract class AbstractCrudAction<T> extends AbstractAction<T> 
											implements Crudable<T> {
	
	private String tab;
	private Object id;
	
    private static final String MSG_REGISTRO_CRIADO = "#{messages['entity_created']}";
    private static final String MSG_REGISTRO_ALTERADO = "#{messages['entity_updated']}";
    private static final LogProvider LOG = Logging.getLogProvider(AbstractCrudAction.class);
	
	/**
	 * Variável que será passada como parametro nas ações executadas
	 * por esse Bean.
	 */
	private T instance;
	
    @Override
	public void setInstance(T instance) {
		this.instance = instance;
	}

	@Override
	public T getInstance() {
		if(instance == null) {
			newInstance();
		}
		return instance;
	}
	
	/**
	 * Devem ser escritas aqui as ações que serão executadas antes da
	 * inserção ou atualização dos dados. 
	 */
	protected boolean beforeSave() {
		return true;
	}
	
	protected void afterSave() {
	    
	}
	
    protected void afterSave(String ret) {
    }
	
	@Override
	public String getTab() {
		return tab;
	}

	@Override
	public void setTab(String tab) {
		this.tab = tab;
	}
	
	@SuppressWarnings(UNCHECKED)
	@Override
	public void setId(Object id) {
		if(id != null && !id.equals(this.id)) {
			this.id = id;
			setInstance(find((Class<T>) EntityUtil.getParameterizedTypeClass(getClass()), this.id));
			tab = TAB_FORM;
		}
	}
	
	@Override
	public Object getId() {
		return id;
	}
	
	/**
	 * Indica se a instancia é gerenciavel ou não (já está no banco).
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
            } catch (DAOException e) {
                getMessagesHandler().add(Severity.ERROR, "Merge Entity Error",e);
            }
        }
    }
	
	private boolean isIdDefined()
	{
	    Object currentId = getId();
        return currentId!=null && !"".equals( currentId );
	}

	/**
	 * Registra ou altera a instância atual.
	 * @return "persisted" ou "updated" se obtiver sucesso. Null caso 
	 * ocorra alguma falha na execução ou na validação.
	 */
	@Override
	public String save() {
		String ret = null;
		if(beforeSave()) {
			ret = isManaged() ? update() : persist();
		}
		if(ret != null) {
		    afterSave();
			afterSave(ret);
			
	        if (PERSISTED.equals(ret)){
	            
                try {
                    setId(EntityUtil.getId(instance).getReadMethod().invoke(instance));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    LOG.error(".save()",e);
                }
	            getMessagesHandler().clear();
	            getMessagesHandler().add(MSG_REGISTRO_CRIADO);
	        } else if (UPDATED.equals(ret)){
	            getMessagesHandler().clear();
	            getMessagesHandler().add(MSG_REGISTRO_ALTERADO);
	        }
		}
		return ret;
	}
    
	/**
	 * Wrapper para o método persist(), pois é necessario definir que
	 * a instancia será managed = true a partir de agora.
	 * @return "persisted" se obtiver sucesso na inserção.
	 */
	protected String persist() {
		return super.persist(instance);
	}
		
	protected String update() {
		return super.update(instance);
	}
	
	/**
	 * Cria um novo objeto do tipo parametrizado para a variável
	 * instance.
	 */
	@SuppressWarnings(UNCHECKED)
	@Override
	public void newInstance() {
		setInstance((T) EntityUtil.newInstance(getClass()));
		id = null;
	}

	/**
	 * Wrapper para o método remove(), pois é necessario chamar o
	 * método newInstance() para limpar a instancia atual.
	 * @return "removed" se removido com sucesso.
	 */
	public String remove() {
		return super.remove(instance);
	}

	/**
	 * Ao mudar para a aba de pesquisa é criada uma nova instancia.
	 */
	public void onClickSearchTab() {
		newInstance();
	}
	
	/**
	 * Ação executada ao entrar na aba de formulário.
	 */
	public void onClickFormTab() {
		//Caso haja alguma ação a ser executada assim que a navegação for para a aba de formulário,
		//então deve ser implementada aqui.
	}
	
	public String getHomeName() {
        String name = null;
        Name nameAnnotation = this.getClass().getAnnotation(Name.class);
        if (nameAnnotation != null) {
            name = nameAnnotation.value();
        }
        return name ;
    }
	
	protected void onDAOExcecption(DAOException daoException){
        PostgreSQLErrorCode errorCode = daoException.getPostgreSQLErrorCode();
        if (errorCode != null) {
            getMessagesHandler().clear();
            getMessagesHandler().add(daoException.getLocalizedMessage());
        }
	}
	
}