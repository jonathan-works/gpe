package br.com.infox.epp.processo.metadado.type;

public enum MetadadoProcessoType {
	
	UNIDADE_DECISORA_MONOCRATICA("unidadeDecisoraMonocratica"),
	UNIDADE_DECISORA_COLEGIADA("unidadeDecisoraColegiada"),
	RELATOR("relator"),
	LOCALIZACAO_DESTINO("destino"),
	PESSOA_DESTINATARIO("destinatario"),
	ITEM_DO_PROCESSO("itemDoProcesso");
	
	private String label;
	
	private MetadadoProcessoType(String label){
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
