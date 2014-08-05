package br.com.infox.epp.unidadedecisora.queries;

public interface UnidadeDecisoraColegiadaQuery {
	
	String ID_USUARIO_LOGIN = "idUsuarioLogin";
	
	String SEARCH_UDC_BY_USUARIO = "searchUDCByUsuario";
    String SEARCH_UDC_BY_USUARIO_QUERY = "select udc " +
    		  "from UnidadeDecisoraColegiadaMonocratica udcm " +
    		  "inner join udcm.unidadeDecisoraMonocratica udm " +
    		  "inner join udcm.unidadeDecisoraColegiada udc " +
    		  "where udm.ativo = true and udc.ativo = true and udm.localizacao.idLocalizacao in " +
    		  "			(select distinct up.localizacao.idLocalizacao from UsuarioPerfil up " +
    		  "			 where up.usuarioLogin.idUsuarioLogin = :" + ID_USUARIO_LOGIN + ") ";

}
