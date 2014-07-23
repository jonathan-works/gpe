package br.com.infox.epp.unidadedecisora.queries;

public interface UnidadeDecisoraMonocraticaQuery {
	
	String ID_UNIDADE_DEC_COLEGIADA = "idUnidadeDecisoraColegiada";
    String SEARCH_BY_UNIDADE_DECISORA_COLEGIADA = "searchByUnidadeDecisoraColegiada";
    String SEARCH_BY_UNIDADE_DECISORA_COLEGIADA_QUERY = "select udm " + 
			  "from UnidadeDecisoraMonocratica udm  " +
			  "where udm.ativo = true " +
			  "and not exists (select 1 from UnidadeDecisoraColegiadaMonocratica udcm " +
			  "			   where udcm.unidadeDecisoraColegiada.idUnidadeDecisoraColegiada = :"+ ID_UNIDADE_DEC_COLEGIADA +
			  "			   and udcm.unidadeDecisoraMonocratica = udm )";

}
