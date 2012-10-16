package br.com.infox.core.action.crud;

/**
 * Interface que deve ser implementada para os Beans que ser�o
 * controller de p�ginas CRUD.
 * 
 * @author Daniel
 *
 */
public interface Crudable<T> {

	/**
	 * Id na p�gina para a tab de pesquisa no tabPanel.
	 */
	String TAB_SEARCH = "search";
	
	/**
	 * Id na p�gina para a tab de formul�rio no tabPanel.
	 */
	String TAB_FORM = "form";
	
	/**
	 * M�todo que retorna o Id da inst�ncia ger�nciada.
	 * @return Id da inst�ncia ger�nciada.
	 */
	Object getId();

	/**
	 * Define informando o id como buscar o registro referente
	 * a ele no banco e atribu�-lo � inst�ncia.
	 * @param id Chave prim�ria do registro que deve ser buscado.
	 */
	void setId(Object id);
	
	/**
	 * Informa a tab corrente da p�gina, Search ou Form
	 * @return
	 */
	String getTab();
	
	/**
	 * Define a tab que ser� exibida na p�gina.
	 * @param tab Ser� definida como a aba atual.
	 */
	void setTab(String tab);
	
	/**
	 * Informa se a inst�ncia parametrizada do Bean est� gerenciavel
	 * ou n�o.
	 * @return true se estiver gerenciavel.
	 */
	boolean isManaged();

	/**
	 * Respons�vel por persistir ou atualizar a instancia atual.
	 * @return "persisted" ou "updated" se a a��o for executada com sucesso.
	 */
	String save();
	
	/**
	 * Ir� criar um novo objeto da classe tipada no parametro.
	 */
	void newInstance();
	
	/**
	 * Retorna a instancia da classe tipada.
	 * @return
	 */
	T getInstance();

	void setInstance(T instance);
	
}