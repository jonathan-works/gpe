package br.com.infox.epp.documento.query;

public interface VariavelQuery {
    String TABLE_VARIAVEL = "tb_variavel";
    String SEQUENCE_VARIAVEL = "sq_tb_variavel";
    String ID_VARIAVEL = "id_variavel";
    String DESCRICAO_VARIAVEL = "ds_variavel";
    String VALOR_VARIAVEL = "vl_variavel";
    String VARIAVEL_ATTRIBUTE = "variavel";
    
    String DESC_VARIAVEL_PARAM = "desc_variavel";
    String ATIVO_PARAM = "ativo";
    String VARIAVEL_BY_DESC_AND_ATIVO = "findVariavelByNomeAndAtivo";
    String VARIAVEL_BY_DESC_AND_ATIVO_QUERY = "select t from Variavel t where t.variavel = :"
            + DESC_VARIAVEL_PARAM + " and t.ativo = :" + ATIVO_PARAM;

}
