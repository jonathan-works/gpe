package br.com.infox.epp.system.query;

public interface ParametroQuery {

    String TABLE_NAME = "tb_parametro";
    String COLUMN_ID = "id_parametro";
    String COLUMN_NOME = "nm_variavel";
    String COLUMN_DESCRICAO = "ds_variavel";
    String COLUMN_VALOR = "vl_variavel";
    String COLUMN_DATA_ATUALIZACAO = "dt_atualizacao";
    String COLUMN_IN_SISTEMA = "in_sistema";
    String COLUMN_IN_ATIVO = "in_ativo";

    String PARAM_NOME = "nomeVariavel";
    String PARAM_VALOR = "valorVariavel";

    String LIST_PARAMETROS_ATIVOS = "listParametrosAtivos";
    String LIST_PARAMETROS_ATIVOS_QUERY = "select o from Parametro o where o.ativo = true";

    String EXISTE_PARAMETRO = "Parametro.existeParametro";
    String EXISTE_PARAMETRO_QUERY = "select 1 from Parametro o where o.nomeVariavel = :" + PARAM_NOME;

    String PARAMETRO_BY_NOME = "Parametro.nome";
    String PARAMETRO_BY_NOME_QUERY = "select p from Parametro p where p.nomeVariavel = :" + PARAM_NOME;

    String PARAMETRO_BY_VALOR = "Parametro.valor";
    String PARAMETRO_BY_VALOR_QUERY = "select p from Parametro p where p.valorVariavel = :" + PARAM_VALOR;

}
