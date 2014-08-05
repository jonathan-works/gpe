package br.com.infox.epp.unidadedecisora.queries;

public interface UnidadeDecisoraMonocraticaQuery {
	
	String ID_UNIDADE_DEC_COLEGIADA = "idUnidadeDecisoraColegiada";
	String ID_USUARIO_LOGIN = "idUsuarioLogin";
    String SEARCH_BY_UNIDADE_DECISORA_COLEGIADA = "searchByUnidadeDecisoraColegiada";
    String SEARCH_BY_UNIDADE_DECISORA_COLEGIADA_QUERY = "select udm " + 
			  "from UnidadeDecisoraMonocratica udm  " +
			  "where udm.ativo = true " +
			  "and not exists (select 1 from UnidadeDecisoraColegiadaMonocratica udcm " +
			  "			   where udcm.unidadeDecisoraColegiada.idUnidadeDecisoraColegiada = :"+ ID_UNIDADE_DEC_COLEGIADA +
			  "			   and udcm.unidadeDecisoraMonocratica = udm )";

    String SEARCH_UDM_BY_USUARIO = "searchUDMByUsuario";
    String SEARCH_UDM_BY_USUARIO_QUERY = "select udm from UnidadeDecisoraMonocratica udm " +
    		  "where udm.ativo = true and udm.localizacao.idLocalizacao in " +
    		  "			(select distinct up.localizacao.idLocalizacao from UsuarioPerfil up " +
    		  "			 where up.usuarioLogin.idUsuarioLogin = :" + ID_USUARIO_LOGIN + ") ";

}
