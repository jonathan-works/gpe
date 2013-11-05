package br.com.infox.core.action.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.util.constants.WarningConstants;
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
public abstract class AbstractCrudAction<T> extends AbstractAction 
											implements Crudable<T> {
	
	private String tab;
	private Object id;
	
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
	
	/**
	 * Devem ser escritas aqui as ações que serão executadas depois da
	 * inserção ou atualização dos dados. 
	 */
	protected void afterSave() {
		//Caso exista alguma ação a ser executada depois da atualização, 
		//então ela deve ser implementada aqui.
	}
	
	@Override
	public String getTab() {
		return tab;
	}

	@Override
	public void setTab(String tab) {
		this.tab = tab;
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	@Override
	public void setId(Object id) {
		if(id != null) {
			this.id = id;
			instance = (T) find(EntityUtil.getParameterizedTypeClass
										  (getClass()), this.id);
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
		return instance != null && contains(instance);
	}
	
	private void mergeWhenNeeded() {
        if (getInstance() != null && isIdDefined() && !contains(instance)) {
            setInstance(EntityUtil.getEntityManager().merge(getInstance()));
        }
    }
	
	private boolean isIdDefined()
	{
	    return getId()!=null && !"".equals( getId() );
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
	@SuppressWarnings(WarningConstants.UNCHECKED)
	@Override
	public void newInstance() {
		instance = (T) EntityUtil.newInstance(getClass());
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
	
}