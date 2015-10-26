package br.com.infox.epp.unidadedecisora.queries;

public interface UnidadeDecisoraMonocraticaQuery {
	
	String ID_UNIDADE_DEC_COLEGIADA = "idUnidadeDecisoraColegiada";
	String ID_USUARIO_LOGIN = "idUsuarioLogin";
	String ID_LOCALIZACAO = "idLocalizacao";
	String CODIGO_LOCALIZACAO = "codigo";
	
    String SEARCH_BY_UNIDADE_DECISORA_COLEGIADA = "searchByUnidadeDecisoraColegiada";
    String SEARCH_BY_UNIDADE_DECISORA_COLEGIADA_QUERY = "select udm " + 
			  "from UnidadeDecisoraMonocratica udm  " +
			  "where udm.ativo = true " +
			  "and not exists (select 1 from UnidadeDecisoraColegiadaMonocratica udcm " +
			  "			   where udcm.unidadeDecisoraColegiada.idUnidadeDecisoraColegiada = :"+ ID_UNIDADE_DEC_COLEGIADA +
			  "			   and udcm.unidadeDecisoraMonocratica = udm )";

    String SEARCH_UDM_BY_USUARIO = "searchUDMByUsuario";
    String SEARCH_UDM_BY_USUARIO_QUERY = "select new map(udm.nome as nome, " +
    									 "	pt.descricao as perfil) " +
    		  "from UsuarioPerfil up " +
    		  "inner join up.perfilTemplate pt " +
    		  "inner join up.localizacao l " +
    		  "inner join l.unidadeDecisoraMonocratica udm " +
    		  "where udm.ativo = true and up.usuarioLogin.idUsuarioLogin = :" + ID_USUARIO_LOGIN + 
    		  " and up.ativo = true";
    
    String SEARCH_EXISTE_UDM_BY_LOCALIZACAO = "searchExisteUDMByLocalizacao";
    String SEARCH_EXISTE_UDM_BY_LOCALIZACAO_QUERY = "select udm " +
    		 "from UnidadeDecisoraMonocratica udm " +
    		 "where udm.localizacao.idLocalizacao = :" + ID_LOCALIZACAO;
    
    String FIND_UDM_BY_CODIGO_LOCALIZACAO = "UnidadeDecisoraMonocratica.findUDMByCodigoLocalizacao";
    String FIND_UDM_BY_CODIGO_LOCALIZACAO_QUERY = "select o from UnidadeDecisoraMonocratica o inner join o.localizacao l where l.codigo = :" + CODIGO_LOCALIZACAO;
    

}
