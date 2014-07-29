package br.com.infox.epp.pessoa.documento.type;

public enum TipoDocumentoPessoaEnum {
	
	CI("Carteira de Identidade"), CT("Carteira de Trabalho"),
	CM("Carteira de Motorista"), CN("Certidão de Nascimento"),
	CC("Certidão de Casamento"), AM("Alistamento Militar"),
	CP("Carteira Profissional"), TE("Título de Eleitor"),
	OD("Outros Documentos");
	
	private String label;
	
	private TipoDocumentoPessoaEnum(String label){
		this.label = label;
	}
	
	public String getLabel(){
		return this.label;
	}

}
