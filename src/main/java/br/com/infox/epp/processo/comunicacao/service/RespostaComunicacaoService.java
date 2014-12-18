package br.com.infox.epp.processo.comunicacao.service;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
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
	
	public Documento gravarResposta(Documento resposta, Processo processoComunicacao) throws DAOException {
		if (resposta.getId() == null) {
			resposta.setDataInclusao(new Date());
			resposta = documentoManager.gravarDocumentoNoProcesso(processoComunicacao, resposta);
			criarMetadadoResposta(processoComunicacao, resposta.getId().toString());
		} else {
			resposta.setDataAlteracao(new Date());
			resposta.setUsuarioAlteracao(Authenticator.getUsuarioLogado());
			resposta = documentoManager.update(resposta);
		}
		return resposta;
	}
	
	private void criarMetadadoResposta(Processo processoComunicacao, String idDocumento) throws DAOException {
		MetadadoProcesso metadado = new MetadadoProcesso();
		metadado.setClassType(Documento.class);
		metadado.setMetadadoType(RESPOSTA_COMUNICACAO);
		metadado.setProcesso(processoComunicacao);
		metadado.setValor(idDocumento);
		metadadoProcessoManager.persist(metadado);
	}
	
	public static final String RESPOSTA_COMUNICACAO = "respostaComunicacao";
}
