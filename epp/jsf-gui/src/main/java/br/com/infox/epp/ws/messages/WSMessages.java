package br.com.infox.epp.ws.messages;

import java.util.HashMap;
import java.util.Map;

public enum WSMessages {	
	
	MS_SUCESSO_INSERIR("MS0001", "Registro Inserido com Sucesso"),
	MS_SUCESSO_ATUALIZAR("MS0002", "Registro Atualizado com Sucesso"),
	ME_TOKEN_INVALIDO("ME0001", "Token de autenticação inválido"),
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
	ME_PERFIL_INEXISTENTE("ME0017", "Perfil não existe"),
	ME_ATTR_PAPEL_INVALIDO("ME0018", "Atributo papel inválido"),
	ME_USUARIO_JA_POSSUI_PERFIL_ASSOCIADO("ME0019", "Usuário já possui perfil associado"),
	ME_LOCALIZACAO_DA_ESTRUTURA_INEXISTENTE("ME0020", "Localização da Estrutura não existe"),
	ME_ATRIBUTO_INVALIDO("ME0021", "Atributo inválido detectado"),
	ME_OBJETO_NAO_ENCONTRADO("ME0022", "Objeto não encontrado");
	
	
	private final String codigo;
	private final String label;
	private static Map<String, WSMessages> mapaMensagens;
	
	private WSMessages(String codigo, String label){
		this.codigo = codigo;
		this.label = label;
		adicionar(codigo, this);
	}
	
	private void adicionar(String codigo, WSMessages mensagem) {
		if(mapaMensagens == null) {
			 mapaMensagens = new HashMap<>();			
		}
		if(mapaMensagens.containsKey(codigo)) {
			throw new RuntimeException("Código já adicionado: " + codigo);
		}
		WSMessages.mapaMensagens.put(codigo, mensagem);
	}
	
	public final String codigo(){
		return this.codigo;
	}
	
	public final String label(){
		return this.label;
	}
	
	public static WSMessages get(String codigo) {
		return mapaMensagens.get(codigo);
	}

}
