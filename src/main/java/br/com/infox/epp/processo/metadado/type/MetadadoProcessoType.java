package br.com.infox.epp.processo.metadado.type;

public enum MetadadoProcessoType {
	
	UNIDADE_DECISORA_MONOCRATICA("Unidade Decisora Monocrática"),
	UNIDADE_DECISORA_COLEGIADA("Unidade Decisora Colegiada"),
	RELATOR("Relator do Processo"),
	LOCALIZACAO_DESTINO("Destino para Localizacao"),
	PESSOA_DESTINATARIO("Destinatário"),
	ITEM_DO_PROCESSO("Item do Processo"),
	TIPO_PROCESSO("Tipo de Processo");
	
	private String label;
	
	private MetadadoProcessoType(String label){
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
