package br.com.infox.epp.processo.comunicacao.envio.action;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoDisponivelComunicacaoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.DocumentoComunicacaoService;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.system.Parametros;

@Name(DocumentoComunicacaoAction.NAME)
@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Transactional
public class DocumentoComunicacaoAction implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoComunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(DocumentoComunicacaoAction.class);
	
	@In
	private DocumentoDisponivelComunicacaoList documentoDisponivelComunicacaoList;
	@In
	private ComunicacaoService comunicacaoService;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private PastaManager pastaManager;
	@In
	private DocumentoBinManager documentoBinManager;
	@In
	private GenericManager genericManager;
	@In
	private DocumentoComunicacaoService documentoComunicacaoService;
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private PapelManager papelManager;
	
	private ModeloComunicacao modeloComunicacao;
	
	private List<ClassificacaoDocumento> classificacoes;
	private List<ModeloDocumento> modelosDocumento;
	private List<Pasta> pastas;
	private boolean possuiDocumentoInclusoPorUsuarioInterno = false;
	
	void init() {
		initClassificacoes();
		initEntityLists();
		if (modeloComunicacao.getId() != null) {
			if (!modeloComunicacao.getFinalizada()) {
				this.possuiDocumentoInclusoPorUsuarioInterno = documentoComunicacaoService.getDocumentoInclusoPorUsuarioInterno(modeloComunicacao) != null;
			} else {
				this.possuiDocumentoInclusoPorUsuarioInterno = modeloComunicacao.getDestinatarios().get(0).getDocumentoComunicacao().getDocumentoBin().isBinario();
			}
		}
	}
	
	void persistDocumentos() throws DAOException {
		for (DocumentoModeloComunicacao documento : modeloComunicacao.getDocumentos()) {
			if (documento.getId() == null) {
				genericManager.persist(documento);
			}
		}
	}
	
	void resetEntityState() {
		for (DocumentoModeloComunicacao doc : modeloComunicacao.getDocumentos()) {
			doc.setId(null);
		}
	}
	
	private void initEntityLists() {
		documentoDisponivelComunicacaoList.setProcesso(modeloComunicacao.getProcesso().getProcessoRoot());
		for (DocumentoModeloComunicacao documentoModelo : modeloComunicacao.getDocumentos()) {
			documentoDisponivelComunicacaoList.adicionarIdDocumento(documentoModelo.getDocumento().getDocumentoBin().getId());
		}
	}

	void initClassificacoes() {
		classificacoes = documentoComunicacaoService.getClassificacoesDocumentoDisponiveisComunicacao(modeloComunicacao.getTipoComunicacao());
		if (classificacoes.size() == 1 && modeloComunicacao.getClassificacaoComunicacao() == null) {
			modeloComunicacao.setClassificacaoComunicacao(classificacoes.get(0));
		}
	}
	
	void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
	}
	
	public void adicionarDocumento(Documento documento) {
		DocumentoModeloComunicacao documentoModelo = new DocumentoModeloComunicacao();
		documentoModelo.setDocumento(documento);
		documentoModelo.setModeloComunicacao(modeloComunicacao);
		modeloComunicacao.getDocumentos().add(documentoModelo);
		documentoDisponivelComunicacaoList.adicionarIdDocumento(documento.getId());
		if (!possuiDocumentoInclusoPorUsuarioInterno) {
			List<String> papeisUsuarioInterno = papelManager.getIdentificadoresPapeisHerdeiros(Parametros.PAPEL_USUARIO_INTERNO.getValue());
			possuiDocumentoInclusoPorUsuarioInterno = papeisUsuarioInterno.contains(documento.getPerfilTemplate().getPapel().getIdentificador());
		}
	}
	
	public void removerDocumento(DocumentoModeloComunicacao documentoModelo) {
		modeloComunicacao.getDocumentos().remove(documentoModelo);
		if(documentoModelo.getId() != null){
			try {
				genericManager.remove(documentoModelo);
			} catch (DAOException e) {
				LOG.error("", e);
				actionMessagesService.handleDAOException(e);
			}
		}
		
		documentoDisponivelComunicacaoList.removerIdDocumento(documentoModelo.getDocumento().getId());
		if (possuiDocumentoInclusoPorUsuarioInterno) {
			if (modeloComunicacao.getId() != null) {
				if (modeloComunicacao.getDocumentos().isEmpty())
				{
					possuiDocumentoInclusoPorUsuarioInterno = false;
				}else {
					possuiDocumentoInclusoPorUsuarioInterno = documentoComunicacaoService.getDocumentoInclusoPorUsuarioInterno(modeloComunicacao) != null;
				}
				
			} else {
				List<String> papeisUsuarioInterno = papelManager.getIdentificadoresPapeisHerdeiros(Parametros.PAPEL_USUARIO_INTERNO.getValue());
				possuiDocumentoInclusoPorUsuarioInterno = false;
				for (DocumentoModeloComunicacao documentoModeloComunicacao : modeloComunicacao.getDocumentos()) {
					if (papeisUsuarioInterno.contains(documentoModeloComunicacao.getDocumento().getPerfilTemplate().getPapel().getIdentificador())) {
						possuiDocumentoInclusoPorUsuarioInterno = true;
						break;
					}
				}
			}
		}
	}
	
	public void assignModeloDocumento() {
		if (modeloComunicacao.getModeloDocumento() == null) {
			modeloComunicacao.setTextoComunicacao("");
			return;
		}
		modeloComunicacao.setTextoComunicacao(modeloComunicacao.getModeloDocumento().getModeloDocumento());
	}
	
	public String getLink(DocumentoBin documento) {
		return documentoBinManager.getUrlValidacaoDocumento(documento);
	}
	
	public List<Pasta> getPastas() {
		if (pastas == null) {
			try {
				pastas = pastaManager.getByProcesso(modeloComunicacao.getProcesso().getProcessoRoot());
			} catch (DAOException e) {
				LOG.error("", e);
				actionMessagesService.handleDAOException(e);
			}
		}
		return pastas;
	}
	
	public List<ClassificacaoDocumento> getClassificacoes() {
		return classificacoes;
	}
	
	public boolean isPossuiDocumentoInclusoPorUsuarioInterno() {
		return possuiDocumentoInclusoPorUsuarioInterno;
	}
	
	public List<ModeloDocumento> getModelosDocumento() {
		if (modelosDocumento == null) {
			modelosDocumento = documentoComunicacaoService.getModelosDocumentoDisponiveisComunicacao(modeloComunicacao.getTipoComunicacao());
		}
		return modelosDocumento;
	}
	
	void setModelosDocumento(List<ModeloDocumento> modelosDocumento) {
		this.modelosDocumento = modelosDocumento;
	}
}
