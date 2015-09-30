package br.com.infox.epp.unidadedecisora.queries;

public interface UnidadeDecisoraColegiadaQuery {
	
	String ID_USUARIO_LOGIN = "idUsuarioLogin";
	String ID_LOCALIZACAO = "idLocalizacao";
	String CODIGO_LOCALIZACAO = "codigoLocalizacao";
	String MONOCRATICA = "monocratica";
	
	String SEARCH_UDC_BY_USUARIO = "searchUDCByUsuario";
    String SEARCH_UDC_BY_USUARIO_QUERY = "select new map( udc.nome as nome, " +
    		 							" pt.descricao as perfil )  " +
    		  "from UsuarioPerfil up " +
    		  "inner join up.perfilTemplate pt " +
    		  "inner join up.localizacao l " +
    		  "inner join l.unidadeDecisoraColegiada udc " +
    		  "where udc.ativo = true and up.usuarioLogin.idUsuarioLogin = :" + ID_USUARIO_LOGIN +
    		  " and up.ativo = true";
    
    String FIND_UDC_BY_USUARIO_ID = "getUDCByUsuario";
    String FIND_UDC_BY_USUARIO_ID_QUERY = "select udc " +
    		"from UsuarioPerfil up " +
    		"inner join up.localizacao l " +
    		"inner join l.unidadeDecisoraColegiada udc " +
    		"where udc.ativo = true and up.usuarioLogin.idUsuarioLogin = :" + ID_USUARIO_LOGIN +
    		" and up.ativo = true";
    
    String SEARCH_EXISTE_UDC_BY_LOCALIZACAO = "searchExisteUDCByLocalizacao";
    String SEARCH_EXISTE_UDC_BY_LOCALIZACAO_QUERY = "select count(udc) " +
    		 "from UnidadeDecisoraColegiada udc " +
    		 "where udc.localizacao.idLocalizacao = :" + ID_LOCALIZACAO;
    
    String FIND_ALL_ATIVO = "findAllAtivo";
    String FIND_ALL_ATIVO_QUERY = "select o from UnidadeDecisoraColegiada o " +
            "where o.ativo = true order by o.nome";

    String FIND_UDC_BY_CODIGO_LOCALIZACAO = "UnidadeDecisoraColegiada.findUDCByCodigoLocalizacao";
    String FIND_UDC_BY_CODIGO_LOCALIZACAO_QUERY = "select o from UnidadeDecisoraColegiada o inner join o.localizacao l where l.codigo = :" + CODIGO_LOCALIZACAO;
    
    String LIST_COLEGIADA_BY_MONOCRATICA = "listColegiadasByMonocratica";
    String LIST_COLEGIADA_BY_MONOCRATICA_QUERY = "select udc from UnidadeDecisoraColegiadaMonocratica udcm "
    		+ "inner join udcm.unidadeDecisoraMonocratica udm "
    		+ "inner join udcm.unidadeDecisoraColegiada udc "
    		+ "where udm.idUnidadeDecisoraMonocratica = :" + MONOCRATICA;
}
