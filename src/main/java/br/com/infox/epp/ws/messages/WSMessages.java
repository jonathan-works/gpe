package br.com.infox.epp.ws.messages;

public enum WSMessages {
	
	MS_SUCESSO_INSERIR("MS0001", "Registro Inserido com Sucesso"),
	MS_SUCESSO_ATUALIZAR("MS0002", "Registro Atualizado com Sucesso"),
	ME_TOKEN_INVALIDO("ME0001", "Token inválido"),
	ME_ATTR_NOME_INVALIDO("ME0002", "Atributo nome inválido"),
	ME_ATTR_CPF_INVALIDO("ME0003", "Atributo CPF inválido"),
	ME_ATTR_EMAIL_INVALIDO("ME0004", "Atributo email inválido"),
	ME_ATTR_SENHA_INVALIDO("ME0005", "Atributo senha inválido"),
	ME_ATTR_DATANASCIMENTO_INVALIDO("ME0006", "Atributo data de nascimento inválido"),
	ME_ATTR_IDENTIDADE_INVALIDO("ME0007", "Atributo identidade inválido"),
	ME_ATTR_ORGAOEXPEDIDOR_INVALIDO("ME0008", "Atributo órgão expedidor inválido"),
	ME_ATTR_DATAEXPEDICAO_INVALIDO("ME0009", "Atributo data de expedição inválido"),
	ME_ATTR_TELEFONEFIXO_INVALIDO("ME0010", "Atributo telefone fixo inválido"),
	ME_ATTR_TELEFONEMOVEL_INVALIDO("ME0011", "Atributo telefone móvel inválido"),
	ME_ATTR_ESTADOCIVIL_INVALIDO("ME0012", "Atributo estado civil inválido"),
	ME_ATTR_EMAILOPCIONAL1_INVALIDO("ME0013", "Atributo email opcional 1 inválido"),
	ME_ATTR_EMAILOPCIONAL2_INVALIDO("ME0014", "Atributo email opcional 2 inválido"),
	ME_USUARIO_INEXISTENTE("ME0015", "Usuário não existe"),
	ME_PAPEL_INEXISTENTE("ME0016", "Papel não existe"),
	ME_PERFIL_INEXISTENTE("ME0018", "Perfil não existe"),
	ME_ATTR_PAPEL_INVALIDO("ME0019", "Atributo papel inválido"),
	ME_ATTR_CODIGOUNIDADEGESTORA_INVALIDO("ME0020", "Atributo código da Unidade Jurisdicionada Inválido"),
	ME_USUARIO_JA_POSSUI_PERFIL_ASSOCIADO("ME0021", "Usuário já possui perfil associado"),
	ME_UNIDADE_GESTORA_NAO_CADASTRADA("ME0022", "Unidade Jurisdicionada não cadastrada e-TCE"),
	ME_USUARIO_SEM_PERFIL_ASSOCIADO("ME0023", "Usuário não possui perfil associado"),
	ME_MUNICIPIO_NAO_ENCONTRADO("ME0024", "Município não encontrado"),
	ME_ENDERECO_INCOMPLETO("ME0025", "Endereço incompleto"),
	ME_LOCALIZACAO_DA_ESTRUTURA_INEXISTENTE("ME0026", "Localização da Estrutura não existe"),
	ME_LOCALIZACAO_DO_PERFIL_INEXISTENTE("ME0027", "Localização do perfil do usuário não existe"),
	WS_UG_GRAVAR_USUARIO("WS0001", "UnidadeGestora/gravarUsuario"),
	WS_UG_ATUALIZAR_SENHA("WS0002", "UnidadeGestora/atualizarSenha"),
	WS_UG_ADICIONAR_PERFIL("WS0003","UnidadeGestora/adicionarPerfil"),
	WS_UG_REMOVER_PERFIL("WS0004", "UnidadeGestora/removerPerfil");
	
	private String codigo;
	private String label;
	
	private WSMessages(String codigo, String label){
		this.codigo = codigo;
		this.label = label;
	}
	
	public String codigo(){
		return this.codigo;
	}
	
	public String label(){
		return this.label;
	}
	
}
