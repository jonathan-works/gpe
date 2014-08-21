package br.com.infox.epp.pessoa.query;

public interface PessoaFisicaQuery {

    String CPF_PARAM = "cpf";
    String LOCALIZACAO_PARAM = "localizacao";
    String SEARCH_BY_CPF = "searchByCpf";
    String SEARCH_BY_CPF_QUERY = "select o from PessoaFisica o where o.cpf = :"
            + CPF_PARAM;
    
    String SEARCH_BY_CPF_AND_IS_USUARIO_AND_LOCALIZACAO = "searchByIsUsuarioAndLocalizacao";
    String SEARCH_BY_CPF_AND_IS_USUARIO_AND_LOCALIZACAO_QUERY = "select pf from UsuarioLogin o " +
    		"inner join o.pessoaFisica pf " +
    		"inner join o.usuarioPerfilList upL " +
    		"where pf.cpf = :" + CPF_PARAM + " and upL.localizacao = :" + LOCALIZACAO_PARAM;
    
}
