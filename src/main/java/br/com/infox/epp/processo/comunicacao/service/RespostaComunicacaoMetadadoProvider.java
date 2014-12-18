package br.com.infox.epp.processo.comunicacao.service;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;

public class RespostaComunicacaoMetadadoProvider extends MetadadoProcessoProvider {
	
	public RespostaComunicacaoMetadadoProvider(){}

	public RespostaComunicacaoMetadadoProvider(Processo processo) {
		super(processo);
	}
	
	public static final MetadadoProcessoDefinition RESPOSTA_COMUNICACAO_ATUAL = 
			new MetadadoProcessoDefinition("respostaComunicacaoAtual", Processo.class);
	
}
