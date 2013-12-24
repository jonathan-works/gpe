package br.com.infox.epp.documento.query;

public interface VariavelQuery {
    
    String TABLE_VARIAVEL = "tb_variavel";
    String SEQUENCE_VARIAVEL = "public.sq_tb_variavel";
    String ID_VARIAVEL = "id_variavel";
    String DESCRICAO_VARIAVEL = "ds_variavel";
    String VALOR_VARIAVEL = "vl_variavel";
    String VARIAVEL_ATTRIBUTE = "variavel";
    
    String PARAM_TIPO = "tipo";
    String VARIAVEL_BY_TIPO_MODELO_DOCUMENTO = "listVariavelByTipoModeloDocumento";
    String VARIAVEL_BY_TIPO_MODELO_QUERY = "select o from Variavel o join o.variavelTipoModeloList tipos "
                                                    + "where tipos.tipoModeloDocumento = :" + PARAM_TIPO;

}
