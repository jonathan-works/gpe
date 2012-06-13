package br.com.infox.core.action.crud;

/**
 * Interface que deve ser implementada para os Beans que serão
 * controller de páginas CRUD.
 * 
 * @author Daniel
 *
 */
public interface Crudable<T> {

	/**
	 * Id na página para a tab de pesquisa no tabPanel.
	 */
	public static final String TAB_SEARCH = "search";
	
	/**
	 * Id na página para a tab de formulário no tabPanel.
	 */
	public static final String TAB_FORM = "form";
	
	/**
	 * Método que retorna o Id da instância gerênciada.
	 * @return Id da instância gerênciada.
	 */
	public Object getId();

	/**
	 * Define informando o id como buscar o registro referente
	 * a ele no banco e atribuí-lo à instância.
	 * @param id Chave primária do registro que deve ser buscado.
	 */
	public void setId(Object id);
	
	/**
	 * Informa a tab corrente da página, Search ou Form
	 * @return
	 */
	public String getTab();
	
	/**
	 * Define a tab que será exibida na página.
	 * @param tab Será definida como a aba atual.
	 */
	public void setTab(String tab);
	
	/**
	 * Informa se a instância parametrizada do Bean está gerenciavel
	 * ou não.
	 * @return true se estiver gerenciavel.
	 */
	public boolean isManaged();

	/**
	 * Responsável por persistir ou atualizar a instancia atual.
	 * @return "persisted" ou "updated" se a ação for executada com sucesso.
	 */
	public String save();
	
	/**
	 * Irá criar um novo objeto da classe tipada no parametro.
	 */
	public void newInstance();
	
	/**
	 * Retorna a instancia da classe tipada.
	 * @return
	 */
	public T getInstance();

	public void setInstance(T instance);
	
}