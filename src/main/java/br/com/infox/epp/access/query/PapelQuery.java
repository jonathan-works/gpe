package br.com.infox.epp.access.query;

public interface PapelQuery {

    String TABLE_PAPEL = "tb_papel";
    String SEQUENCE_PAPEL = "sq_tb_papel";
    String ID_PAPEL = "id_papel";
    String NOME_PAPEL = "ds_nome";
    String IDENTIFICADOR = "ds_identificador";

    String PARAM_TIPO_MODELO_DOCUMENTO = "tipoModeloDocumento";
    String PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO = "listPapeisNaoAssociadosATipoModeloDocumento";
    String PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO_QUERY = "select o from Papel o where o.idPapel not in ("
            + "select p.papel.idPapel from TipoModeloDocumentoPapel p "
            + "where p.tipoModeloDocumento = :"
            + PARAM_TIPO_MODELO_DOCUMENTO
            + ") order by o.nome ";

    String PARAM_CLASSIFICACAO_DOCUMENTO = "classificacaoDocumento";
    String PAPEIS_NAO_ASSOCIADOS_A_CLASSIFICACAO_DOCUMENTO = "listPapeisNaoAssociadosATipoProcessoDocumento";
    String PAPEIS_NAO_ASSOCIADOS_A_CLASSIFICACAO_DOCUMENTO_QUERY = "select o from Papel o where o not in "
            + "(select p.papel from ClassificacaoDocumentoPapel p "
            + "where p.classificacaoDocumento = :"
            + PARAM_CLASSIFICACAO_DOCUMENTO + ")";

    String PARAM_IDENTIFICADOR = "identificador";
    String PAPEL_BY_IDENTIFICADOR = "findPapelByIdentificador";
    String PAPEL_BY_IDENTIFICADOR_QUERY = "select o from Papel o where o.identificador = :"
            + PARAM_IDENTIFICADOR;

    String PARAM_LISTA_IDENTIFICADORES = "identificadores";
    String PAPEIS_BY_IDENTIFICADORES = "listPapeisByIdentificadores";
    String PAPEIS_BY_IDENTIFICADORES_QUERY = "select p from Papel p where identificador in (:"
            + PARAM_LISTA_IDENTIFICADORES + ")";

    String PARAM_LOCALIZACAO = "localizacao";
    String PAPEIS_BY_LOCALIZACAO = "papeisDeUsuarioByLocalizacao";
    String PAPEIS_BY_LOCALIZACAO_QUERY = "select distinct l.perfilTemplate.papel from UsuarioPerfil l where l.perfilTemplate.localizacao = :"
            + PARAM_LOCALIZACAO;

    String ID_PAPEL_PARAM = "idPapel";
    String PERMISSOES_BY_PAPEL = "listOermissoesByPapel";
    String PERMISSOES_BY_PAPEL_QUERY = "select pe.ds_alvo from tb_permissao pe "
            + "inner join tb_papel pa on pe.ds_destinatario=pa.ds_identificador "
            + "where pa.id_papel = :" + ID_PAPEL_PARAM;
    String COL_TERMO_ADESAO = "in_termo_adesao";

    String PARAM_USUARIO = "usuario";
    
    String HAS_TO_SIGN_TERMO_ADESAO = "hasToSignTermoAdesao";
    String HAS_TO_SIGN_TERMO_ADESAO_QUERY = "select 1 from UsuarioPerfil up inner join up.perfilTemplate pt inner join pt.papel p "
    		+ "where p.termoAdesao = true and up.ativo = true and pt.ativo = true and up.usuarioLogin = :" + PARAM_USUARIO;
    
    String GET_PERFIL_TERMO_ADESAO = "getPerfilTermoAdesao";
    String GET_PERFIL_TERMO_ADESAO_QUERY = "select up from UsuarioPerfil up inner join up.perfilTemplate pt inner join pt.papel p "
    		+ "where p.termoAdesao = true and up.ativo = true and pt.ativo = true and up.usuarioLogin = :" + PARAM_USUARIO;
    
    String PAPEIS_ORDEM_AFABETICA = "getPapeisOrdemAlfabetica";
    String PAPEIS_ORDEM_AFABETICA_QUERY = "select o from Papel o order by o.nome";
}
