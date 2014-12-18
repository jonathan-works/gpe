package br.com.infox.epp.processo.comunicacao;

import java.util.Date;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;

public class ComunicacaoMetadadoProvider extends MetadadoProcessoProvider {
	
	public ComunicacaoMetadadoProvider(){}

	public ComunicacaoMetadadoProvider(Processo processo) {
		super(processo);
	}

	public static final MetadadoProcessoDefinition MEIO_EXPEDICAO = 
			new MetadadoProcessoDefinition("meioExpedicaoComunicacao", "Meio de Expedição", MeioExpedicao.class, true);
	
	public static final MetadadoProcessoDefinition DESTINATARIO = 
			new MetadadoProcessoDefinition("destinatarioComunicacao", DestinatarioModeloComunicacao.class);
	
	public static final MetadadoProcessoDefinition PRAZO_DESTINATARIO = 
			new MetadadoProcessoDefinition("prazoDestinatarioComunicacao", "Prazo (dias)", Integer.class, true);

	public static final MetadadoProcessoDefinition COMUNICACAO = 
			new MetadadoProcessoDefinition("comunicacao", Documento.class);
	
	public static final MetadadoProcessoDefinition IMPRESSA = 
			new MetadadoProcessoDefinition("impressa", "Impressa", Boolean.class, true);
	
	public static final MetadadoProcessoDefinition DATA_CIENCIA = 
			new MetadadoProcessoDefinition("dataCiencia", "Data de Ciência", Date.class, true);
	
	public static final MetadadoProcessoDefinition DATA_CUMPRIMENTO = 
			new MetadadoProcessoDefinition("dataCumprimento", "Data de Cumprimento", Date.class, true);

	public static final MetadadoProcessoDefinition RESPONSAVEL_CIENCIA = 
			new MetadadoProcessoDefinition("responsavelCiencia", "Responsável Ciência", UsuarioLogin.class, true);
	
	public static final MetadadoProcessoDefinition DOCUMENTO_COMPROVACAO_CIENCIA = 
			new MetadadoProcessoDefinition("documentoComprovacaoCiencia", Documento.class);
	
	public static final MetadadoProcessoDefinition RESPOSTA_COMUNICACAO_ATUAL = 
			new MetadadoProcessoDefinition("respostaComunicacaoAtual", Processo.class);
}
