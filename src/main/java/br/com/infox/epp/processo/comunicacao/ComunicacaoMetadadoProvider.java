package br.com.infox.epp.processo.comunicacao;

import java.util.Date;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;

public class ComunicacaoMetadadoProvider extends MetadadoProcessoProvider {
	
	public static final MetadadoProcessoDefinition MEIO_EXPEDICAO = 
			new MetadadoProcessoDefinition("meioExpedicaoComunicacao", "Meio de Expedição", MeioExpedicao.class);
	
	public static final MetadadoProcessoDefinition DESTINATARIO = 
			new MetadadoProcessoDefinition("destinatarioComunicacao", DestinatarioModeloComunicacao.class);
	
	public static final MetadadoProcessoDefinition PRAZO_DESTINATARIO = 
			new MetadadoProcessoDefinition("prazoDestinatarioComunicacao", Integer.class);

	public static final MetadadoProcessoDefinition COMUNICACAO = 
			new MetadadoProcessoDefinition("comunicacao", Documento.class);
	
	public static final MetadadoProcessoDefinition IMPRESSA = 
			new MetadadoProcessoDefinition("impressa", "Impressa", Boolean.class);
	
	public static final MetadadoProcessoDefinition DATA_CIENCIA = 
			new MetadadoProcessoDefinition("dataCiencia", "Data de Ciência", Date.class);
	
	public static final MetadadoProcessoDefinition DATA_CUMPRIMENTO = 
			new MetadadoProcessoDefinition("dataCumprimento", "Data de Cumprimento", Date.class);

	public static final MetadadoProcessoDefinition RESPONSAVEL_CIENCIA = 
			new MetadadoProcessoDefinition("responsavelCiencia", "Responsável Ciência", UsuarioLogin.class);
	
	public static final MetadadoProcessoDefinition RESPONSAVEL_CUMPRIMENTO = 
			new MetadadoProcessoDefinition("responsavelCumprimento", "Responsável Cumprimento", UsuarioLogin.class);
	
	public static final MetadadoProcessoDefinition DOCUMENTO_COMPROVACAO_CIENCIA = 
			new MetadadoProcessoDefinition("documentoComprovacaoCiencia", Documento.class);
	
	public static final MetadadoProcessoDefinition LIMITE_DATA_CIENCIA = 
            new MetadadoProcessoDefinition("limiteDataCiencia", Date.class);
	
	/**
	 * Primeiro prazo de cumprimento, ele é calculado após confirmação da ciência.
	 */
	public static final MetadadoProcessoDefinition LIMITE_DATA_CUMPRIMENTO_INICIAL = 
            new MetadadoProcessoDefinition("limiteDataCumprimentoInicial", Date.class);
	
	/**
	 * Inicialmente é igual ao prazo de cumprimento inicial porém ele pode ser alterado quando houver prorrogação de prazo.
	 * Este é o prazo utilizado para que o sistema dê cumprimento à uma comunicação.
	 */
	public static final MetadadoProcessoDefinition LIMITE_DATA_CUMPRIMENTO = 
            new MetadadoProcessoDefinition("limiteDataCumprimento", "Limite para Cumprimento", Date.class);
	
	public static final MetadadoProcessoDefinition DATA_PEDIDO_PRORROGACAO = 
            new MetadadoProcessoDefinition("dataPedidoProrrogacao", "Data de Solicitação da Prorrogação", Date.class);
	
	public static final MetadadoProcessoDefinition DATA_ANALISE_PRORROGACAO = 
            new MetadadoProcessoDefinition("dataAnaliseProrrogacao", "Data da Análise da Prorrogação", Date.class);
}
