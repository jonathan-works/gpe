package br.com.infox.epp.mail.query;

public interface ListaEmailQuery {

    String TABLE_LISTA_EMAIL = "tb_lista_email";
    String SEQUENCE_LISTA_EMAIL = "sq_tb_lista_email";
    String ID_LISTA_EMAIL = "id_lista_email";
    String ID_GRUPO_EMAIL = "id_grupo_email";
    String ID_LOCALIZACAO = "id_localizacao";
    String ID_PAPEL = "id_papel";
    String ID_ESTRUTURA = "id_estrutura";

    String MAXIMO_ID_GRUPO_EMAIL_IN_LISTA_EMAIL = "maxIdGrupoEmailInListaEmail";
    String MAXIMO_ID_GRUPO_EMAIL_IN_LISTA_EMAIL_QUERY = "select max(o.idGrupoEmail) from ListaEmail o";

    String ID_GRUPO_EMAIL_PARAM = "idGrupo";
    String LISTA_EMAIL_BY_ID_GRUPO = "listListaEmailByIdGrupoEmail";
    String LISTA_EMAIL_BY_ID_GRUPO_QUERY = "select o from ListaEmail o where o.idGrupoEmail = :"
            + ID_GRUPO_EMAIL_PARAM;

    String RESOLVE_LISTA_EMAIL_BY_ID_GRUPO = "resolveListaEmailByIdGrupoEmail";
    String RESOLVE_LISTA_EMAIL_BY_ID_GRUPO_QUERY = "select distinct u.email from UsuarioLogin u join u.usuarioPerfilList ul where exists "
            + "(select o from ListaEmail o where o.idGrupoEmail = :"
            + ID_GRUPO_EMAIL_PARAM
            + " and "
            + "((ul.perfil.perfilTemplate.localizacao = o.localizacao and (ul.perfil.perfilTemplate.papel = o.papel or o.papel is null) and "
            + "(ul.perfil.paiDaEstrutura = o.estrutura or o.estrutura is null)) or (ul.perfil.perfilTemplate.papel = o.papel and "
            + "(ul.perfil.perfilTemplate.localizacao = o.localizacao or o.localizacao is null) and "
            + "(ul.perfil.paiDaEstrutura = o.estrutura or o.estrutura is null)) or (ul.perfil.paiDaEstrutura = o.estrutura and "
            + "(ul.perfil.perfilTemplate.localizacao = o.localizacao or o.localizacao is null) and "
            + "(ul.perfil.perfilTemplate.papel = o.papel or o.papel is null))))";

}
