package br.com.infox.core.suggest;

public interface SuggestBean {

    /**
     * Define a expressão a ser utilizada para o suggest
     * 
     * @return expressão EJBQL
     */
    String getEjbql();
}
