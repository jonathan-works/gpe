package br.com.infox.epp.processo.comunicacao.service;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.service.ProcessoAnaliseDocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;

@Name(RespostaComunicacaoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class RespostaComunicacaoService {
	public static final String NAME = "respostaComunicacaoService";
	
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
	@In
	private ProcessoAnaliseDocumentoService processoAnaliseDocumentoService;
	
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
		Processo processoResposta = processoAnaliseDocumentoService.criarProcessoAnaliseDocumentos(processoComunicacao);
		MetadadoProcesso metadado = new ComunicacaoMetadadoProvider()
			.gerarMetadado(ComunicacaoMetadadoProvider.RESPOSTA_COMUNICACAO_ATUAL, processoComunicacao, processoResposta.getIdProcesso().toString());
		metadadoProcessoManager.persist(metadado);
		
		return processoResposta;
	}

	public void inicializarFluxoDocumento(Processo processoResposta) throws DAOException {
		processoAnaliseDocumentoService.inicializarFluxoDocumento(processoResposta);
		metadadoProcessoManager.removerMetadado(ComunicacaoMetadadoProvider.RESPOSTA_COMUNICACAO_ATUAL, processoResposta.getProcessoPai());
	}
}
