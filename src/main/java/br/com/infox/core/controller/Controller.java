package br.com.infox.core.controller;

import java.io.Serializable;

public interface Controller extends Serializable {

    /**
     * Id na página para a tab de pesquisa no tabPanel.
     */
    String TAB_SEARCH = "search";

    /**
     * Id na página para a tab de formulário no tabPanel.
     */
    String TAB_FORM = "form";

    /**
     * Método que retorna o Id da instância gerênciada.
     * 
     * @return Id da instância gerênciada.
     */
    Object getId();

    /**
     * Define informando o id como buscar o registro referente a ele no banco e
     * atribuí-lo à instância.
     * 
     * @param id Chave primária do registro que deve ser buscado.
     */
    void setId(Object id);

    /**
     * Informa a tab corrente da página, Search ou Form
     * 
     * @return
     */
    String getTab();

    /**
     * Define a tab que será exibida na página.
     * 
     * @param tab Será definida como a aba atual.
     */
    void setTab(String tab);

    /**
     * Ao mudar para a aba de pesquisa é criada uma nova instancia.
     */
    void onClickSearchTab();

    /**
     * Ação executada ao entrar na aba de formulário.
     */
    void onClickFormTab();
}
