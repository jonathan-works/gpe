package br.com.infox.epp.processo.documento.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import br.com.infox.certificado.CertificateSignatures;
import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.ArrayUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.certificado.DefaultSignableDocumentImpl;
import br.com.infox.epp.certificado.SignDocuments;
import br.com.infox.epp.certificado.SignableDocument;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.type.ExpressionResolver;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.bean.DadosUpload;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoTemporario;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoTemporarioManager;
import br.com.infox.epp.processo.documento.service.DocumentoUploaderService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.marcador.Marcador;
import br.com.infox.epp.processo.marcador.MarcadorSearch;
import br.com.infox.epp.processo.marcador.MarcadorService;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.ibpm.task.home.VariableTypeResolver;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.util.ComponentUtil;

@Named
@ViewScoped
public class AnexarDocumentosView implements Serializable {
    
	private static final long serialVersionUID = 1L;

	private static final LogProvider LOG = Logging.getLogProvider(AnexarDocumentosView.class);

	@Inject
	private DocumentoTemporarioManager documentoTemporarioManager;
	@Inject
	private ActionMessagesService actionMessagesService;
	@Inject
	private DocumentoBinManager documentoBinManager;
	@Inject
	private DocumentoUploaderService documentoUploaderService;
	@Inject
	private MetadadoProcessoManager metadadoProcessoManager;
	@Inject
	private ModeloDocumentoManager modeloDocumentoManager;
	@Inject
	private ClassificacaoDocumentoPapelManager classificacaoDocumentoPapelManager;
	@Inject
	private InfoxMessages infoxMessages;
	@Inject
	private MarcadorSearch marcadorSearch;
	@Inject
	private MarcadorService marcadorService;

	// Propriedades da classe
	private Processo processo;
	private Processo processoReal;
	private List<DocumentoTemporarioWrapper> documentoTemporarioList;
	private Pasta pastaDefault = null;

	// Controle do uploader
	private ClassificacaoDocumento classificacaoDocumentoUploader;
	private String descricaoUploader;
	private Pasta pastaUploader;
	private List<Marcador> marcadoresUpload;
	private List<DadosUpload> dadosUploader = new ArrayList<>();
	private boolean showUploader;

	// Controle do editor
	private DocumentoTemporario documentoEditor;
	private List<ModeloDocumento> modeloDocumentoList;
	private ModeloDocumento modeloDocumento;
	private boolean showModeloDocumentoCombo;
	private List<Marcador> marcadoresEditor;
	private ExpressionResolver expressionResolver;

	// Controle da assinatura
	private List<DocumentoTemporario> documentosAssinaveis;
	private List<DocumentoTemporario> documentosNaoAssinaveis;
	private List<DocumentoTemporario> documentosMinutas;
	private DocumentoTemporario docTempMostrarAssinaturas;
	private String tokenAssinatura;
	private CertificateSignatures certificateSignatures = ComponentUtil.getComponent(CertificateSignatures.NAME);
	private AssinaturaDocumentoService assinaturaDocumentoService = ComponentUtil
			.getComponent(AssinaturaDocumentoService.NAME);

	// Controle do envio
	private List<String> faltaAssinar = new ArrayList<>();
	private List<DocumentoTemporarioWrapper> documentosParaEnviar = new ArrayList<>();

	// Controle da dataTable
	private String orderedColumn;
	private String order;
	private static final String DEFAULT_ORDER = "o.id";
	
	public void newEditorInstance() {
		DocumentoTemporario newEditor = new DocumentoTemporario();
		newEditor.setDocumentoBin(new DocumentoBin());
		newEditor.setAnexo(Boolean.TRUE);
		if (pastaDefault != null) {
			newEditor.setPasta(pastaDefault);
		}
		marcadoresEditor = null;
		setDocumentoEditor(newEditor);
		setModeloDocumentoList(null);
		setModeloDocumento(null);
		setShowModeloDocumentoCombo(false);
	}
	
	public List<Marcador> autoCompleteMarcadoresUpload(String query) {
	    return autoCompleteMarcadores(query, marcadoresUpload);
    }
	
	public List<Marcador> autoCompleteMarcadoresEditor(String query) {
        return autoCompleteMarcadores(query, marcadoresEditor);
    }

    private List<Marcador> autoCompleteMarcadores(String query, List<Marcador> marcadoresSelectionados) {
        Marcador marcadorTemp = new Marcador(query.toUpperCase());
        List<String> codigosMarcadores = ArrayUtil.convertToList(marcadoresSelectionados, MarcadorService.CONVERT_MARCADOR_CODIGO);
        List<Marcador> marcadores = marcadorSearch.listMarcadorByProcessoAndCodigo(getProcesso().getIdProcesso(), query, codigosMarcadores);
        if (!marcadores.contains(marcadorTemp) 
                && (marcadoresSelectionados == null || !marcadoresSelectionados.contains(marcadorTemp))) {
            marcadores.add(0, marcadorTemp);
        }
        return marcadores;
    }
	
	public boolean isPermittedAddMarcador() {
	    return marcadorService.isPermittedAdicionarMarcador();
	}
	
	public void fileUploadListener(FileUploadEvent fileUploadEvent) throws IOException {
		UploadedFile uploadedFile = fileUploadEvent.getUploadedFile();
		try {
			DadosUpload dadosUpload = new DadosUpload(uploadedFile);
			documentoUploaderService.validaDocumento(dadosUpload.getUploadedFile(), classificacaoDocumentoUploader);
			dadosUploader.add(dadosUpload);
		} catch (Exception e) {
			if (e instanceof EJBException) {
				e = (Exception) e.getCause();
			}
			FacesMessages.instance().add(e.getMessage());
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.addHeader("Error-Message", "Falha: " + e.getMessage());
            response.sendError(500);
		}
	}
	
	public void removeDocumentoUpload() {
        String fileName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fileName");
        if ("ALL_FILES".equals(fileName)) {
            for (DadosUpload dadosUpload : dadosUploader) {
            	dadosUpload.delete();
            }
            dadosUploader.clear();
        } else if (fileName != null) {
            DadosUpload dadosUploadToRemove = null;
            for (DadosUpload dadosUpload : dadosUploader) {
                if (dadosUpload.getUploadedFile().getName().equals(fileName)) {
                    dadosUploadToRemove = dadosUpload;
                    break;
                }
            }
            if (dadosUploadToRemove != null) {
                dadosUploader.remove(dadosUploadToRemove);
                dadosUploadToRemove.delete();
            }
        }
    }

	public void onChangeUploadClassificacaoDocumento() {
		clearUploadFile();
	}

	private void resetUploader() {
		clearUploadFile();
		classificacaoDocumentoUploader = null;
		descricaoUploader = null;
		pastaUploader = pastaDefault;
		showUploader = false;
		marcadoresUpload = null;
	}

	public void clearUploadFile() {
		dadosUploader.clear();
		setShowUploader(classificacaoDocumentoUploader != null);
	}
	
	public void onChangeEditorClassificacaoDocumento() {
		checkVinculoClassificacaoDocumento();
		getDocumentoEditor().getDocumentoBin().setModeloDocumento("");
	}

	private void checkVinculoClassificacaoDocumento() {
		TipoModeloDocumento tipoModeloDocumento = getDocumentoEditor().getClassificacaoDocumento()
				.getTipoModeloDocumento();
		if (tipoModeloDocumento != null) {
			setShowModeloDocumentoCombo(true);
			setModeloDocumentoList(modeloDocumentoManager.getModeloDocumentoByTipo(tipoModeloDocumento));
			setModeloDocumento(null);
		} else {
			setShowModeloDocumentoCombo(false);
		}
	}

	public void onSelectModeloDocumento() {
		if (getModeloDocumento() != null) {
			String documentoConvertido = modeloDocumentoManager.evaluateModeloDocumento(getModeloDocumento(),
					expressionResolver);
			getDocumentoEditor().getDocumentoBin().setModeloDocumento(documentoConvertido);
		} else {
			getDocumentoEditor().getDocumentoBin().setModeloDocumento("");
		}
	}

	private DocumentoTemporario gravarArquivoUpload(DadosUpload dadosUpload) throws Exception {
		DocumentoTemporario retorno = new DocumentoTemporario();
		retorno.setDescricao(getDescricaoUploader());
		retorno.setDocumentoBin(documentoUploaderService.createProcessoDocumentoBin(dadosUpload.getUploadedFile()));
		retorno.setAnexo(Boolean.TRUE);
		retorno.setClassificacaoDocumento(classificacaoDocumentoUploader);
		retorno.setPasta(pastaUploader == null ? pastaDefault : pastaUploader);
		retorno.setProcesso(processo);
		retorno.getDocumentoBin().setMarcadores(marcadoresUpload != null ? new HashSet<>(marcadoresUpload) : new HashSet<Marcador>());
		documentoTemporarioManager.gravarDocumentoTemporario(retorno, dadosUpload.getUploadedFile().getInputStream()); 
		return retorno;
	}

	public void persistUpload(String toRender) {
	    if (pastaDefault == null) {
	        FacesMessages.instance().add(infoxMessages.get("documento.erro.processSemPasta"));
	        return;
	    }
		try {
			for (DadosUpload dadosUpload : dadosUploader) {
				DocumentoTemporario documentoGerado = gravarArquivoUpload(dadosUpload);
				getDocumentoTemporarioList().add(new DocumentoTemporarioWrapper(documentoGerado));
				dadosUpload.getUploadedFile().delete();
			}
			resetUploader();
		    JsfUtil.instance().render(toRender);
		} catch (DAOException e) {
			FacesMessages.instance().add("Não foi possível enviar o arquivo. Tente novamente");
			LOG.error("Erro ao gravar documento temporário", e);
		} catch (Exception e) {
			FacesMessages.instance().add(e.getMessage());
			LOG.error("", e);
		}
	}

	private List<DocumentoTemporarioWrapper> loadDocumentoTemporarioList() {
		List<DocumentoTemporarioWrapper> dtList = new ArrayList<>();
		Localizacao localizacao = Authenticator.getLocalizacaoAtual();
		if (getProcesso() != null) {
			List<DocumentoTemporario> listByProcesso = documentoTemporarioManager.listByProcesso(getProcesso(),
					localizacao, getOrder());
			for (DocumentoTemporario documentoTemporario : listByProcesso) {
				dtList.add(new DocumentoTemporarioWrapper(documentoTemporario));
			}
		}
		return dtList;
	}

	public void persistEditor() {
		try {
			if (getDocumentoEditor().getId() == null) {
				getDocumentoEditor().setProcesso(getProcesso());
				if (getDocumentoEditor().getPasta() == null) {
				    if (pastaDefault == null) {
				        FacesMessages.instance().add(infoxMessages.get("documento.erro.processSemPasta"));
				        return;
				    }
					getDocumentoEditor().setPasta(pastaDefault);
				}
				getDocumentoEditor().getDocumentoBin().setMarcadores(marcadoresEditor != null ? new HashSet<>(marcadoresEditor) : new HashSet<Marcador>());
				documentoTemporarioManager.gravarDocumentoTemporario(getDocumentoEditor());
				getDocumentoTemporarioList().add(new DocumentoTemporarioWrapper(getDocumentoEditor()));
			} else {
				DocumentoTemporarioWrapper wrapper = (DocumentoTemporarioWrapper) getDocumentoEditor();
				documentoTemporarioManager.update(wrapper.getDocumentoTemporario());
			}
			newEditorInstance();
			marcadoresEditor = null;
		} catch (DAOException e) {
			FacesMessages.instance().add("Não foi possível atualizar o arquivo. Tente novamente");
			LOG.error("Erro ao atualizar documento temporário", e);
		}
	}

	public void onClickExcluirButton() {
		List<DocumentoTemporario> documentosMarcados = new ArrayList<>();
		for (Iterator<DocumentoTemporarioWrapper> iterator = getDocumentoTemporarioList().iterator(); iterator.hasNext();) {
			DocumentoTemporarioWrapper wrapper = iterator.next();
			if (wrapper.getCheck()) {
				documentosMarcados.add(wrapper.getDocumentoTemporario());
				iterator.remove();
			}
		}
		if (documentosMarcados.isEmpty()) {
			FacesMessages.instance().add("Nenhum documento foi selecionado.");
		}
		try {
			documentoTemporarioManager.removeAll(documentosMarcados);
			newEditorInstance();
		} catch (DAOException e) {
		    for (DocumentoTemporario documentoTemporario : documentosMarcados) {
		        DocumentoTemporarioWrapper documentoTemporarioWrapper = new DocumentoTemporarioWrapper(documentoTemporario);
		        documentoTemporarioWrapper.setCheck(true);
		        getDocumentoTemporarioList().add(documentoTemporarioWrapper);
		    }
			FacesMessages.instance().add("Não foi possível remover os documentos marcados. Tente novamente");
			LOG.error("Erro ao excluir documentos temporários", e);
		}
	}

	public void onClickEnviarButton() {
		List<DocumentoTemporarioWrapper> documentosMarcados = new ArrayList<>();
		for (DocumentoTemporarioWrapper wrapper : getDocumentoTemporarioList()) {
			if (wrapper.getCheck()) {
				documentosMarcados.add(wrapper);
			}
		}
		setDocumentosParaEnviar(documentosMarcados);
		setFaltaAssinar(validarAssinaturas(documentosMarcados));
	}

	private List<String> validarAssinaturas(List<DocumentoTemporarioWrapper> documentosMarcados) {
		List<String> totalValidations = new ArrayList<>();
		for (DocumentoTemporarioWrapper documentoTemporario : documentosMarcados) {
			if (documentoTemporario.getDocumentoBin().getMinuta()) {
				String mensagem = infoxMessages.get("anexarDocumentos.erroEnviarMinuta");
				totalValidations.add(String.format(mensagem, documentoTemporario.getDescricao()));
				continue;
			}
			if (assinaturaDocumentoService.isDocumentoTotalmenteAssinado(documentoTemporario.getDocumentoBin(), documentoTemporario.getClassificacaoDocumento())) {
				continue;
			}

			List<ClassificacaoDocumentoPapel> cdpList = classificacaoDocumentoPapelManager
					.getByClassificacaoDocumento(documentoTemporario.getClassificacaoDocumento());
			List<String> localValidations = new ArrayList<>();
			for (ClassificacaoDocumentoPapel classificacaoDocumentoPapel : cdpList) {
				DocumentoBin bin = documentoTemporario.getDocumentoBin();
				Papel papel = classificacaoDocumentoPapel.getPapel();
				boolean isAssinado = documentoBinManager.isDocumentoBinAssinadoPorPapel(bin, papel);
				if (TipoAssinaturaEnum.O.equals(classificacaoDocumentoPapel.getTipoAssinatura())) {
					if (!isAssinado) {
						String mensagem = infoxMessages.get("anexarDocumentos.faltaAssinaturaPapel");
						localValidations.add(String.format(mensagem, documentoTemporario.getDescricao(),
								classificacaoDocumentoPapel.getPapel()));
					}
				} else if (TipoAssinaturaEnum.S.equals(classificacaoDocumentoPapel.getTipoAssinatura())) {
					String mensagem = infoxMessages.get("anexarDocumentos.faltaAssinaturaPapel");
					localValidations.add(String.format(mensagem, documentoTemporario.getDescricao(),
							classificacaoDocumentoPapel.getPapel()));
				}
			}
			totalValidations.addAll(localValidations);
		}
		return totalValidations;
	}

	public void enviarDocumentosMarcados() {
		try {
			List<DocumentoTemporario> documentoTemporarioParaEnviar = new ArrayList<>();
			for (DocumentoTemporarioWrapper wrapper : getDocumentosParaEnviar()) {
				documentoTemporarioParaEnviar.add(wrapper.getDocumentoTemporario());
			}
			documentoTemporarioManager.transformarEmDocumento(documentoTemporarioParaEnviar);
			documentoTemporarioManager.removeAllSomenteTemporario(documentoTemporarioParaEnviar);
			getDocumentoTemporarioList().removeAll(getDocumentosParaEnviar());
			FacesMessages.instance().add("Documento(s) enviado(s) com sucesso!");
		} catch (DAOException e) {
			actionMessagesService.handleDAOException(e);			
			LOG.error("Erro ao enviar documentos para o processo", e);
			setDocumentoTemporarioList(loadDocumentoTemporarioList());
		}
	}

	public boolean isShowUploader() {
		return showUploader;
	}

	public void setShowUploader(boolean showUploader) {
		this.showUploader = showUploader;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		if (processo == null) {
			this.processo = null;
			setProcessoReal(null);
		} else {
			this.processo = processo.getProcessoRoot();
			this.processoReal = processo;
			List<MetadadoProcesso> metaPastas = metadadoProcessoManager.getMetadadoProcessoByType(getProcessoReal(),
					EppMetadadoProvider.PASTA_DEFAULT.getMetadadoType());
			if (!metaPastas.isEmpty()) {
				pastaDefault = metaPastas.get(0).getValue();
			} else {
				metaPastas = metadadoProcessoManager.getMetadadoProcessoByType(getProcesso(),
						EppMetadadoProvider.PASTA_DEFAULT.getMetadadoType());
				if (!metaPastas.isEmpty()) {
					pastaDefault = metaPastas.get(0).getValue();
				}
			}
			pastaUploader = pastaDefault;
			newEditorInstance();
			createExpressionResolver();
		}
	}

	public void signDocuments() {
		try {
			CertificateSignatureBundleBean bundle = getSignatureBundle();

			UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
			for (CertificateSignatureBean bean : bundle.getSignatureBeanList()) {
				DocumentoBin docBin = getDocumentoTemporarioByUuid(bean.getDocumentUuid());
				if (docBin != null) {
					if (!isAssinadoPor(docBin, usuarioPerfil)) {
						assinaturaDocumentoService.assinarDocumento(docBin, usuarioPerfil, bean.getCertChain(),
								bean.getSignature());
					}
				} else {
					throw new ApplicationException("Documento não localizado!");
				}
			}
			setDocumentoTemporarioList(loadDocumentoTemporarioList());
			FacesMessages.instance().add(InfoxMessages.getInstance().get("anexarDocumentos.sucessoAssinatura"));
			setDocumentosAssinaveis(new ArrayList<DocumentoTemporario>());
		}catch(AssinaturaException | CertificadoException e){
			LOG.error("Erro signDocuments ", e);
			FacesMessages.instance().add(Severity.ERROR,e.getMessage());
		}catch (Exception e) {
			LOG.error("Erro signDocuments ", e);
			FacesMessages.instance().add(Severity.ERROR,
					InfoxMessages.getInstance().get("anexarDocumentos.erroAssinarDocumentos"));
		}
	}

	private boolean isAssinadoPor(DocumentoBin docBin, UsuarioPerfil usuarioPerfil) {
		List<AssinaturaDocumento> assinaturas = docBin.getAssinaturas();
		if (assinaturas != null) {
			for (AssinaturaDocumento assinatura : assinaturas) {
				if (usuarioPerfil.equals(assinatura.getUsuarioPerfil())) {
					return true;
				}
			}
		}
		return false;
	}

	private DocumentoBin getDocumentoTemporarioByUuid(String uuid) {
		for (DocumentoTemporario documentoTemporario : documentosAssinaveis) {
			UUID wrapperUuid = documentoTemporario.getDocumentoBin().getUuid();
			if (uuid.equals(wrapperUuid.toString()))
				return documentoBinManager.getByUUID(wrapperUuid);
		}
		return null;
	}

	private CertificateSignatureBundleBean getSignatureBundle() throws CertificadoException {
		CertificateSignatureBundleBean bundle = certificateSignatures.get(tokenAssinatura);
		if (bundle == null) {
			throw new CertificadoException(infoxMessages.get("assinatura.error.hashExpired"));
		} else if (CertificateSignatureBundleStatus.ERROR.equals(bundle.getStatus())
				|| CertificateSignatureBundleStatus.UNKNOWN.equals(bundle.getStatus())) {
			throw new CertificadoException("Erro de Certificado " + bundle);
		}
		return bundle;
	}

	public void selectSignableDocuments() {
		documentosAssinaveis = new ArrayList<DocumentoTemporario>();
		documentosNaoAssinaveis = new ArrayList<DocumentoTemporario>();
		documentosMinutas = new ArrayList<>();
		for (DocumentoTemporarioWrapper documentoTemporarioWrapper : documentoTemporarioList) {
			if (documentoTemporarioWrapper.getCheck()) {
				if (documentoTemporarioWrapper.getDocumentoBin().getMinuta()) {
					documentosMinutas.add(documentoTemporarioWrapper);
				} else if (assinaturaDocumentoService.podeRenderizarApplet(Authenticator.getPapelAtual(),
						documentoTemporarioWrapper.getClassificacaoDocumento(),
						documentoTemporarioWrapper.getDocumentoBin(), Authenticator.getUsuarioLogado())) {
					documentosAssinaveis.add(documentoTemporarioWrapper);
				} else {
					documentosNaoAssinaveis.add(documentoTemporarioWrapper);
				}
			}
		}
	}

	public String getSignDocuments() {
		List<SignableDocument> documentsToSign = new ArrayList<SignableDocument>();
		if (documentosAssinaveis != null) {
			for (DocumentoTemporario documentoTemporario : documentosAssinaveis) {
				documentsToSign.add(new DefaultSignableDocumentImpl(documentoTemporario.getDocumentoBin()));
			}
		}
		SignDocuments multiSign = new SignDocuments(documentsToSign);
		return multiSign.getDocumentData();
	}

	public void viewDocumento(DocumentoTemporario documentoTemporario) {
		boolean isEditor = documentoTemporario.getDocumentoBin().getExtensao() == null;
		if (isEditor) {
			try { // Carrega documento do banco para descartar modificações
					// feitas e não salvas
				DocumentoTemporario documentoPersistido = documentoTemporarioManager
						.loadById(documentoTemporario.getId());
				documentoTemporario.setDocumentoBin(documentoPersistido.getDocumentoBin());
				documentoTemporario.setClassificacaoDocumento(documentoPersistido.getClassificacaoDocumento());
				documentoTemporario.setDescricao(documentoPersistido.getDescricao());
			} catch (Exception e) {
			}
			setDocumentoEditor(documentoTemporario);
			checkVinculoClassificacaoDocumento();
		}
	}

	public List<DocumentoTemporarioWrapper> getDocumentoTemporarioList() {
		if (documentoTemporarioList == null) {
			setDocumentoTemporarioList(loadDocumentoTemporarioList());
		}
		return documentoTemporarioList;
	}

	public void setDocumentoTemporarioList(List<DocumentoTemporarioWrapper> documentoTemporarioList) {
		this.documentoTemporarioList = documentoTemporarioList;
	}

	public DocumentoTemporario getDocumentoEditor() {
		if (documentoEditor == null) {
			newEditorInstance();
		}
		return documentoEditor;
	}

	public void setDocumentoEditor(DocumentoTemporario documentoEditor) {
		this.documentoEditor = documentoEditor;
	}

	public Processo getProcessoReal() {
		return processoReal;
	}

	public void setProcessoReal(Processo processoReal) {
		this.processoReal = processoReal;
	}

	public List<ModeloDocumento> getModeloDocumentoList() {
		return modeloDocumentoList;
	}

	public String getTokenAssinatura() {
		return tokenAssinatura;
	}

	public void setTokenAssinatura(String tokenAssinatura) {
		this.tokenAssinatura = tokenAssinatura;
	}

	public List<DocumentoTemporario> getDocumentosAssinaveis() {
		return documentosAssinaveis;
	}

	public void setDocumentosAssinaveis(List<DocumentoTemporario> documentosAssinaveis) {
		this.documentosAssinaveis = documentosAssinaveis;
	}

	public List<DocumentoTemporario> getDocumentosNaoAssinaveis() {
		return documentosNaoAssinaveis;
	}

	public void setDocumentosNaoAssinaveis(List<DocumentoTemporario> documentosNaoAssinaveis) {
		this.documentosNaoAssinaveis = documentosNaoAssinaveis;
	}

	public void setModeloDocumentoList(List<ModeloDocumento> modeloDocumentoList) {
		this.modeloDocumentoList = modeloDocumentoList;
	}

	public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}

	public void setModeloDocumento(ModeloDocumento modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}

	public boolean isShowModeloDocumentoCombo() {
		return showModeloDocumentoCombo;
	}

	public void setShowModeloDocumentoCombo(boolean showModeloDocumentoCombo) {
		this.showModeloDocumentoCombo = showModeloDocumentoCombo;
	}
	
	public List<Marcador> getMarcadoresEditor() {
        return marcadoresEditor;
    }

    public void setMarcadoresEditor(List<Marcador> marcadoresEditor) {
        this.marcadoresEditor = marcadoresEditor;
    }

    public List<String> getFaltaAssinar() {
		return faltaAssinar;
	}

	public void setFaltaAssinar(List<String> faltaAssinar) {
		this.faltaAssinar = faltaAssinar;
	}

	public DocumentoTemporario getDocTempMostrarAssinaturas() {
		return docTempMostrarAssinaturas;
	}

	public void setDocTempMostrarAssinaturas(DocumentoTemporario docTempMostrarAssinaturas) {
		this.docTempMostrarAssinaturas = docTempMostrarAssinaturas;
	}

	public List<DocumentoTemporarioWrapper> getDocumentosParaEnviar() {
		return documentosParaEnviar;
	}

	public void setDocumentosParaEnviar(List<DocumentoTemporarioWrapper> documentosParaEnviar) {
		this.documentosParaEnviar = documentosParaEnviar;
	}

	public String getOrderedColumn() {
		return orderedColumn;
	}

	public void setOrderedColumn(String orderedColumn) {
		if (orderedColumn.startsWith("classificacaoDocumento")) {
			orderedColumn = "cd." + orderedColumn.replace("classificacaoDocumento.", "").trim();
		} else {
			orderedColumn = "o." + orderedColumn.trim();
		}
		if (!orderedColumn.endsWith("asc") && !orderedColumn.endsWith("desc")) {
			orderedColumn = orderedColumn.concat(" asc");
		}
		setOrder(orderedColumn);
		this.orderedColumn = orderedColumn;
	}

	public Integer getResultCount() {
		return documentoTemporarioList == null ? 0 : documentoTemporarioList.size();
	}

	public boolean isPreviousExists() {
		return false;
	}

	public boolean isNextExists() {
		return false;
	}

	public String getOrder() {
		return order != null ? order : DEFAULT_ORDER;
	}

	public void setOrder(String order) {
		if (getOrder() != order) {
			this.order = order;
			setDocumentoTemporarioList(loadDocumentoTemporarioList());
		}
	}

	public List<DocumentoTemporario> getDocumentosMinutas() {
		return documentosMinutas;
	}

	public void setDocumentosMinutas(List<DocumentoTemporario> documentosMinutas) {
		this.documentosMinutas = documentosMinutas;
	}

	public boolean isShowUploaderButton() {
		return !dadosUploader.isEmpty();
	}

	public ClassificacaoDocumento getClassificacaoDocumentoUploader() {
		return classificacaoDocumentoUploader;
	}

	public void setClassificacaoDocumentoUploader(ClassificacaoDocumento classificacaoDocumentoUploader) {
		this.classificacaoDocumentoUploader = classificacaoDocumentoUploader;
	}
	
    public String getDescricaoUploader() {
		return descricaoUploader;
	}

	public void setDescricaoUploader(String descricaoUploader) {
		this.descricaoUploader = descricaoUploader;
	}

	public List<Marcador> getMarcadoresUpload() {
        return marcadoresUpload;
    }

    public void setMarcadoresUpload(List<Marcador> marcadoresUpload) {
        this.marcadoresUpload = marcadoresUpload;
    }

    private void createExpressionResolver() {
		if (processoReal != null) {
			VariableTypeResolver variableTypeResolver = ComponentUtil.getComponent(VariableTypeResolver.NAME);
			EntityManager entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);
			ProcessInstance processInstance = entityManager.find(ProcessInstance.class, processoReal.getIdJbpm());
			if (variableTypeResolver.getProcessInstance() == null) {
			    variableTypeResolver.setProcessInstance(processInstance);
			}
			ExecutionContext executionContext = new ExecutionContext(processInstance.getRootToken());
			expressionResolver = ExpressionResolverChainBuilder
					.defaultExpressionResolverChain(processoReal.getIdProcesso(), executionContext);
		}
	}

	public class DocumentoTemporarioWrapper extends DocumentoTemporario implements Serializable {
		private static final long serialVersionUID = 1L;

		private Boolean check = false;

		public DocumentoTemporarioWrapper(DocumentoTemporario documentoTemporario) {
			setId(documentoTemporario.getId());
			setClassificacaoDocumento(documentoTemporario.getClassificacaoDocumento());
			setDocumentoBin(documentoTemporario.getDocumentoBin());
			setProcesso(documentoTemporario.getProcesso());
			setDescricao(documentoTemporario.getDescricao());
			setAnexo(documentoTemporario.getAnexo());
			setIdJbpmTask(documentoTemporario.getIdJbpmTask());
			setPerfilTemplate(documentoTemporario.getPerfilTemplate());
			setDataInclusao(documentoTemporario.getDataInclusao());
			setUsuarioInclusao(documentoTemporario.getUsuarioInclusao());
			setDataAlteracao(documentoTemporario.getDataAlteracao());
			setUsuarioAlteracao(documentoTemporario.getUsuarioAlteracao());
			setPasta(documentoTemporario.getPasta());
			setLocalizacao(documentoTemporario.getLocalizacao());
		}

		public DocumentoTemporario getDocumentoTemporario() {
			DocumentoTemporario dt = new DocumentoTemporario();
			dt.setId(this.getId());
			dt.setClassificacaoDocumento(this.getClassificacaoDocumento());
			dt.setDocumentoBin(this.getDocumentoBin());
			dt.setProcesso(this.getProcesso());
			dt.setDescricao(this.getDescricao());
			dt.setAnexo(this.getAnexo());
			dt.setIdJbpmTask(this.getIdJbpmTask());
			dt.setPerfilTemplate(this.getPerfilTemplate());
			dt.setDataInclusao(this.getDataInclusao());
			dt.setUsuarioInclusao(this.getUsuarioInclusao());
			dt.setDataAlteracao(this.getDataAlteracao());
			dt.setUsuarioAlteracao(this.getUsuarioAlteracao());
			dt.setPasta(this.getPasta());
			dt.setLocalizacao(this.getLocalizacao());
			return dt;
		}

		public Boolean getCheck() {
			return check;
		}

		public void setCheck(Boolean check) {
			this.check = check;
		}
	}

	public Pasta getPastaUploader() {
		return pastaUploader;
	}

	public void setPastaUploader(Pasta pastaUploader) {
		this.pastaUploader = pastaUploader;
	}

}