package br.com.infox.epp.unidadedecisora.queries;

public interface UnidadeDecisoraColegiadaQuery {
	
	String ID_USUARIO_LOGIN = "idUsuarioLogin";
	String ID_LOCALIZACAO = "idLocalizacao";
	
	String SEARCH_UDC_BY_USUARIO = "searchUDCByUsuario";
    String SEARCH_UDC_BY_USUARIO_QUERY = "select new map( udc.nome as nome, " +
    		 							" pt.descricao as perfil )  " +
    		  "from UsuarioPerfil up " +
    		  "inner join up.perfilTemplate pt " +
    		  "inner join up.localizacao l " +
    		  "inner join l.unidadeDecisoraColegiada udc " +
    		  "where udc.ativo = true and up.usuarioLogin.idUsuarioLogin = :" + ID_USUARIO_LOGIN;
    
    String SEARCH_EXISTE_UDC_BY_LOCALIZACAO = "searchExisteUDCByLocalizacao";
    String SEARCH_EXISTE_UDC_BY_LOCALIZACAO_QUERY = "select count(udc) " +
    		 "from UnidadeDecisoraColegiada udc " +
    		 "where udc.localizacao.idLocalizacao = :" + ID_LOCALIZACAO;

}
