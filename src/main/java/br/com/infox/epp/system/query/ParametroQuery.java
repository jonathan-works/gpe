package br.com.infox.epp.system.query;

public interface ParametroQuery {

    String PARAM_NOME = "nomeVariavel";
    String PARAM_VALOR = "valorVariavel";
    
    String LIST_PARAMETROS_ATIVOS = "listParametrosAtivos";
    String LIST_PARAMETROS_ATIVOS_QUERY = "select o from Parametro o where o.ativo = true";

    String EXISTE_PARAMETRO = "Parametro.existeParametro";
    String EXISTE_PARAMETRO_QUERY = "select 1 from Parametro o where o.nomeVariavel = :" + PARAM_NOME;
    
    String PARAMETRO_BY_NOME = "Parametro.nome";
    String PARAMETRO_BY_NOME_QUERY = "select p from Parametro p where nomeVariavel = :" + PARAM_NOME; 
    
    String PARAMETRO_BY_VALOR = "Parametro.valor";
    String PARAMETRO_BY_VALOR_QUERY = "select p from Parametro p where valorVariavel = :" + PARAM_VALOR;
 
}
