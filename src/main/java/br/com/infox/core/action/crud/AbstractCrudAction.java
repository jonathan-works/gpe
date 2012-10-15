package br.com.infox.core.action.crud;

import br.com.infox.core.action.AbstractAction;
import br.com.itx.util.EntityUtil;

/**
 * � um abstractAction, por�m possui os m�todos implementados de Crudable
 * possibilitando o controle de abas e gerenciamento de instancias para
 * as p�ginas de cadastros b�sicos.
 * @author Daniel
 *
 * CRUD = Create, Retrieve, Update, Delete.
 *
 * @param <T> Entity principal, onde devem ser realizadas as 
 * altera��es.
 */
public abstract class AbstractCrudAction<T> extends AbstractAction 
											implements Crudable<T> {
	
	private String tab;
	private Object id;
	
	/**
	 * Vari�vel que ser� passada como parametro nas a��es executadas
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
	 * Devem ser escritas aqui as a��es que ser�o executadas antes da
	 * inser��o ou atualiza��o dos dados. 
	 */
	protected boolean beforeSave() {
		return true;
	}
	
	/**
	 * Devem ser escritas aqui as a��es que ser�o executadas depois da
	 * inser��o ou atualiza��o dos dados. 
	 */
	protected void afterSave() {
		//Caso exista alguma a��o a ser executada depois da atualiza��o, 
		//ent�o ela deve ser implementada aqui.
	}
	
	@Override
	public String getTab() {
		return tab;
	}

	@Override
	public void setTab(String tab) {
		this.tab = tab;
	}
	
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
	 * Indica se a instancia � gerenciavel ou n�o (j� est� no banco).
	 * @return true se for gerenciavel.
	 */
	@Override
	public boolean isManaged() {
		return instance != null && contains(instance);
	}

	/**
	 * Registra ou altera a inst�ncia atual.
	 * @return "persisted" ou "updated" se obtiver sucesso. Null caso 
	 * ocorra alguma falha na execu��o ou na valida��o.
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
	 * Wrapper para o m�todo persist(), pois � necessario definir que
	 * a instancia ser� managed = true a partir de agora.
	 * @return "persisted" se obtiver sucesso na inser��o.
	 */
	protected String persist() {
		return super.persist(instance);
	}
		
	protected String update() {
		return super.update(instance);
	}
	
	/**
	 * Cria um novo objeto do tipo parametrizado para a vari�vel
	 * instance.
	 */
	@Override
	public void newInstance() {
		instance = (T) EntityUtil.newInstance(getClass());
		id = null;
	}

	/**
	 * Wrapper para o m�todo remove(), pois � necessario chamar o
	 * m�todo newInstance() para limpar a instancia atual.
	 * @return "removed" se removido com sucesso.
	 */
	public String remove() {
		return super.remove(instance);
	}
	
	/**
	 * Ao mudar para a aba de pesquisa � criada uma nova instancia.
	 */
	public void onClickSearchTab() {
		if(isManaged()) {
			instance = null;
			id = null;
		}
	}
	
	/**
	 * A��o executada ao entrar na aba de formul�rio.
	 */
	public void onClickFormTab() {
		//Caso haja alguma a��o a ser executada assim que a navega��o for para a aba de formul�rio,
		//ent�o deve ser implementada aqui.
	}
	
}