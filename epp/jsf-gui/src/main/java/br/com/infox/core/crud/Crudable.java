package br.com.infox.core.crud;

import br.com.infox.core.controller.Controller;

/**
 * Interface que deve ser implementada para os Beans que serão controller de
 * páginas CRUD.
 * 
 * @author Daniel
 * 
 */
public interface Crudable<T> extends Controller {

    /**
     * Informa se a instância parametrizada do Bean está gerenciavel ou não.
     * 
     * @return true se estiver gerenciavel.
     */
    boolean isManaged();

    /**
     * Responsável por persistir ou atualizar a instancia atual.
     * 
     * @return "persisted" ou "updated" se a ação for executada com sucesso.
     */
    String save();

    /**
     * Irá criar um novo objeto da classe tipada no parametro.
     */
    void newInstance();

    /**
     * Retorna a instancia da classe tipada.
     * 
     * @return
     */
    T getInstance();

    void setInstance(T instance);

    String remove();

    String remove(T value);

}
