package br.com.infox.core.action.crud;

import br.com.infox.core.action.AbstractAction;
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
	
	public void setInstance(T instance) {
		this.instance = instance;
	}

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
	}
	
	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}
	
	@SuppressWarnings("unchecked")
	public void setId(Object id) {
		if(id != null) {
			this.id = id;
			instance = (T) find(EntityUtil.getParameterizedTypeClass
										  (getClass()), this.id);
			tab = TAB_FORM;
		}
	}
	
	public Object getId() {
		return id;
	}
	
	/**
	 * Indica se a instancia é gerenciavel ou não (já está no banco).
	 * @return true se for gerenciavel.
	 */
	public boolean isManaged() {
		return instance != null && contains(instance);
	}

	/**
	 * Registra ou altera a instância atual.
	 * @return "persisted" ou "updated" se obtiver sucesso. Null caso 
	 * ocorra alguma falha na execução ou na validação.
	 */
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
	@SuppressWarnings("unchecked")
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
		if(isManaged()) {
			instance = null;
			id = null;
		}
	}
	
	/**
	 * Ação executada ao entrar na aba de formulário.
	 */
	public void onClickFormTab() {}
	
}