package br.com.infox.epp.processo.comunicacao.envio.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoDisponivelComunicacaoList;
import br.com.infox.epp.processo.comunicacao.service.DocumentoComunicacaoService;
import br.com.infox.epp.processo.documento.bean.PastaRestricaoBean;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.documento.manager.PastaRestricaoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.marcador.MarcadorSearch;
import br.com.infox.epp.processo.marcador.MarcadorService;
import br.com.infox.epp.system.Parametros;

@Named(DocumentoComunicacaoAction.NAME)
@ViewScoped
public class DocumentoComunicacaoAction implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoComunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(DocumentoComunicacaoAction.class);
	
	@Inject
	private ActionMessagesService actionMessagesService;
	@Inject
	private PastaManager pastaManager;
	@Inject
	private DocumentoBinManager documentoBinManager;
	@Inject
	private DocumentoComunicacaoService documentoComunicacaoService;
	@Inject
	private PapelManager papelManager;
	@Inject
	private PastaRestricaoManager pastaRestricaoManager;
	@Inject
	private DocumentoDisponivelComunicacaoList documentoDisponivelComunicacaoList;
	@Inject
    private MarcadorService marcadorService;
	@Inject
	private MarcadorSearch marcadorSearch;
	
	private ModeloComunicacao modeloComunicacao;
	
	private List<ClassificacaoDocumento> classificacoes;
	private List<ModeloDocumento> modelosDocumento;
	private List<Pasta> pastas;
	private boolean possuiDocumentoInclusoPorUsuarioInterno = false;
	private Map<Integer, PastaRestricaoBean> restricoesPasta = new HashMap<>();
	
	public void init() {
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
	
	public void persistDocumentos() throws DAOException {
		documentoComunicacaoService.persistDocumentos(modeloComunicacao.getDocumentos());
	}
	
	public void resetEntityState() {
		for (DocumentoModeloComunicacao doc : modeloComunicacao.getDocumentos()) {
			doc.setId(null);
		}
	}
	
	private void initEntityLists() {
		documentoDisponivelComunicacaoList.setProcesso(modeloComunicacao.getProcesso().getProcessoRoot());
		for (DocumentoModeloComunicacao documentoModelo : modeloComunicacao.getDocumentos()) {
			documentoDisponivelComunicacaoList.adicionarIdDocumento(documentoModelo.getDocumento().getId());
		}
	}

	public void initClassificacoes() {
		classificacoes = documentoComunicacaoService.getClassificacoesDocumentoDisponiveisComunicacao(modeloComunicacao.getTipoComunicacao());
		if (classificacoes.size() == 1 && modeloComunicacao.getClassificacaoComunicacao() == null) {
			modeloComunicacao.setClassificacaoComunicacao(classificacoes.get(0));
		}
	}
	
	public void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
	}
	
	public void adicionarDocumento(Documento documento) {
		DocumentoModeloComunicacao documentoModelo = new DocumentoModeloComunicacao();
		documentoModelo.setDocumento(documento);
		documentoModelo.setModeloComunicacao(modeloComunicacao);
		modeloComunicacao.getDocumentos().add(documentoModelo);
		documentoDisponivelComunicacaoList.adicionarIdDocumento(documento.getId());
		if (!possuiDocumentoInclusoPorUsuarioInterno) {
			possuiDocumentoInclusoPorUsuarioInterno = papelManager.isPapelHerdeiro(documento.getPerfilTemplate().getPapel().getIdentificador(), Parametros.PAPEL_USUARIO_INTERNO.getValue());
		}
	}
	
	public void removerDocumento(DocumentoModeloComunicacao documentoModelo) {
		modeloComunicacao.getDocumentos().remove(documentoModelo);
		try {
			documentoComunicacaoService.removerDocumento(documentoModelo);
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
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
			    Processo processo = modeloComunicacao.getProcesso().getProcessoRoot();
				pastas = pastaManager.getByProcesso(processo);
				restricoesPasta = pastaRestricaoManager.loadRestricoes(processo,
				        Authenticator.getUsuarioLogado(),
				        Authenticator.getLocalizacaoAtual(),
				        Authenticator.getPapelAtual());
			} catch (DAOException e) {
				LOG.error("", e);
				actionMessagesService.handleDAOException(e);
			}
		}
		return pastas;
	}
	
    public List<String> autoCompleteMarcadores(String query) {
    	query = query.toUpperCase();
        List<String> marcadores = marcadorSearch.listCodigoByProcessoAndNotInListCodigos(modeloComunicacao.getProcesso().getIdProcesso(), query, modeloComunicacao.getCodigosMarcadores());
        if (!marcadores.contains(query) && (modeloComunicacao.getCodigosMarcadores() == null || !modeloComunicacao.getCodigosMarcadores().contains(query))) {
            marcadores.add(0, query);
        }
        return marcadores;
    }
	
	public boolean isPermittedAddMarcador() {
        return marcadorService.isPermittedAdicionarMarcador();
    }

	public boolean canSee(Pasta pasta) {
	    PastaRestricaoBean restricaoBean = restricoesPasta.get(pasta.getId());
        return restricaoBean != null && restricaoBean.getRead();
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
	
	public void setModelosDocumento(List<ModeloDocumento> modelosDocumento) {
		this.modelosDocumento = modelosDocumento;
	}
	
}
