package br.com.infox.epp.unidadedecisora.queries;

public interface UnidadeDecisoraMonocraticaQuery {
	
	String ID_UNIDADE_DEC_COLEGIADA = "idUnidadeDecisoraColegiada";
	String ID_USUARIO_LOGIN = "idUsuarioLogin";
	String ID_LOCALIZACAO = "idLocalizacao";
	
    String SEARCH_BY_UNIDADE_DECISORA_COLEGIADA = "searchByUnidadeDecisoraColegiada";
    String SEARCH_BY_UNIDADE_DECISORA_COLEGIADA_QUERY = "select udm " + 
			  "from UnidadeDecisoraMonocratica udm  " +
			  "where udm.ativo = true " +
			  "and not exists (select 1 from UnidadeDecisoraColegiadaMonocratica udcm " +
			  "			   where udcm.unidadeDecisoraColegiada.idUnidadeDecisoraColegiada = :"+ ID_UNIDADE_DEC_COLEGIADA +
			  "			   and udcm.unidadeDecisoraMonocratica = udm )";

    String SEARCH_UDM_BY_USUARIO = "searchUDMByUsuario";
    String SEARCH_UDM_BY_USUARIO_QUERY = "select new map(udmL.nome as nome, " +
    									 "	pt.descricao as perfil) " +
    		  "from UsuarioPerfil up " +
    		  "inner join up.perfilTemplate pt " +
    		  "inner join up.localizacao l " +
    		  "inner join l.unidadeDecisoraMonocraticaList udmL " +
    		  "where up.usuarioLogin.idUsuarioLogin = :" + ID_USUARIO_LOGIN;
    
    String SEARCH_EXISTE_UDM_BY_LOCALIZACAO = "searchExisteUDMByLocalizacao";
    String SEARCH_EXISTE_UDM_BY_LOCALIZACAO_QUERY = "select count(udm) " +
    		 "from UnidadeDecisoraMonocratica udm " +
    		 "where udm.localizacao.idLocalizacao = :" + ID_LOCALIZACAO;
    

}
