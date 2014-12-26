package br.com.infox.epp.processo.comunicacao.service;

import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import org.jboss.seam.transaction.Transaction;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.processo.type.TipoProcesso;

@Name(RespostaComunicacaoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class RespostaComunicacaoService {
	public static final String NAME = "respostaComunicacaoService";
	private static final LogProvider LOG = Logging.getLogProvider(RespostaComunicacaoService.class);
	
	@In
	private DocumentoManager documentoManager;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	@In
	private ProcessoManager processoManager;
	@In
	private DocumentoBinManager documentoBinManager;
	@In
	private DocumentoBinarioManager documentoBinarioManager;
	@In(required = false)
	private String codigoFluxoDocumento;
	@In
	private FluxoManager fluxoManager;
	@In
	private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
	@In
	private IniciarProcessoService iniciarProcessoService;
	
	public Documento gravarDocumentoResposta(Documento resposta, Processo processoResposta) throws DAOException {
		if (resposta.getId() == null) {
			resposta.setDataInclusao(new Date());
			resposta = documentoManager.gravarDocumentoNoProcesso(processoResposta, resposta);
		} else {
			resposta.setDataAlteracao(new Date());
			resposta.setUsuarioAlteracao(Authenticator.getUsuarioLogado());
			resposta = documentoManager.update(resposta);
		}
		return resposta;
	}

	public Processo criarProcessoResposta(Processo processoComunicacao) throws DAOException {
		Fluxo fluxoDocumento = getFluxoDocumento();
		List<NaturezaCategoriaFluxo> ncfs = naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxoDocumento);
		if (ncfs == null || ncfs.isEmpty()) {
			throw new DAOException("Não existe Natureza/Categoria para o fluxo " + fluxoDocumento.getFluxo());
		}
		
		Processo processoResposta = new Processo();
		processoResposta.setNaturezaCategoriaFluxo(ncfs.get(0));
		processoResposta.setProcessoPai(processoComunicacao);
		processoResposta.setLocalizacao(Authenticator.getLocalizacaoAtual());
		processoResposta.setNumeroProcesso("");
		processoResposta.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
		processoManager.persist(processoResposta);
		
		MetadadoProcesso metadado = new ComunicacaoMetadadoProvider()
			.gerarMetadado(ComunicacaoMetadadoProvider.RESPOSTA_COMUNICACAO_ATUAL, processoComunicacao, processoResposta.getIdProcesso().toString());
		metadadoProcessoManager.persist(metadado);
		criarMetadadosProcessoResposta(processoResposta);
		
		return processoResposta;
	}

	public void removerDocumento(Documento documento) throws DAOException {
		DocumentoBin documentoBin = documento.getDocumentoBin();
		documentoManager.remove(documento);
		try {
			Transaction.instance().commit(); // O documento sempre deve ser removido
		} catch (Exception e) {
			throw new DAOException(e);
		}
		
		try {
			Transaction.instance().begin(); // Mas o bin associado apenas se ninguém mais o referenciar
			Integer idDocumentoBin = documentoBin.getId();
			documentoBinManager.remove(documentoBin);
			if (documentoBin.getExtensao() != null) {
				documentoBinarioManager.remove(idDocumentoBin);
			}
		} catch (Exception e) {
			LOG.warn("", e);
		}
	}
	
	public void inicializarFluxoDocumento(Processo processoResposta) throws DAOException {
		iniciarProcessoService.iniciarProcesso(processoResposta);
		metadadoProcessoManager.removerMetadado(ComunicacaoMetadadoProvider.RESPOSTA_COMUNICACAO_ATUAL, processoResposta.getProcessoPai());
	}
	
	private Fluxo getFluxoDocumento() throws DAOException {
		if (codigoFluxoDocumento == null) {
			throw new DAOException("Fluxo de documento não encontrado");
		}
		Fluxo fluxo = fluxoManager.getFluxoByCodigo(codigoFluxoDocumento);
		if (fluxo == null) {
			throw new DAOException("Fluxo de documento não encontrado");
		}
		return fluxo;
	}
	
	private void criarMetadadosProcessoResposta(Processo processoResposta) throws DAOException {
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processoResposta);
		MetadadoProcesso metadado = metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.TIPO_PROCESSO, TipoProcesso.DOCUMENTO);
		metadadoProcessoManager.persist(metadado);
		
		metadado = metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.LOCALIZACAO_DESTINO, processoResposta.getProcessoPai().getLocalizacao().getIdLocalizacao().toString());
		metadadoProcessoManager.persist(metadado);
	}
}
