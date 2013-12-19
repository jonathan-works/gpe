package br.com.infox.epp.access.query;

public interface UsuarioLocalizacaoQuery {
    
    String TABLE_USUARIO_LOCALIZACAO = "tb_usuario_localizacao";
    String SEQUENCE_USUARIO_LOCALIZACAO = "public.sq_tb_usuario_localizacao";
    String ID_USUARIO_LOCALIZACAO = "id_usuario_localizacao";
    String USUARIO = "id_usuario";
    String PAPEL = "id_papel";
    String LOCALIZACAO = "id_localizacao";
    String ESTRUTURA = "id_estrutura";
    String RESPONSAVEL_LOCALIZACAO = "in_responsavel_localizacao";
    String CONTABILIZAR = "in_contabilizar";
	
	String PARAM_PAPEL = "papel";
	String PARAM_USUARIO = "usuario";
	String PARAM_ESTRUTURA = "estrutura";
	String PARAM_LOCALIZACAO = "localizacao";
	
	String EXISTE_USUARIO_LOCALIZACAO_QUERY = "select count(o) from UsuarioLocalizacao o"
			+ " where o.papel = :" + PARAM_PAPEL + " and o.usuario = :" + PARAM_USUARIO
			+ " and o.localizacao = :" + PARAM_LOCALIZACAO;
	
	String ESTRUTURA_NULL_CONDITION = " and o.estrutura is null";
	String ESTRUTURA_CONDITION = " and o.estrutura = :" + PARAM_ESTRUTURA;
}
