package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.faces.FacesMessages;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.dao.DocumentoRespostaComunicacaoDAO;
import br.com.infox.epp.processo.comunicacao.list.ModeloComunicacaoRascunhoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.DestinatarioComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.DocumentoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.ProrrogacaoPrazoService;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.documento.anexos.DocumentoDownloader;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.service.ProcessoAnaliseDocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

import com.google.common.base.Strings;

@Name(ComunicacaoAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@Transactional
public class ComunicacaoAction implements Serializable {
	private static final String COMPROVANTE_DE_CIÊNCIA = "Comprovante de Ciência";
	private static final long serialVersionUID = 1L;
	public static final String NAME = "comunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(ComunicacaoAction.class);
	
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private ComunicacaoService comunicacaoService;
	@In
	private PrazoComunicacaoService prazoComunicacaoService;
	@In
	private DocumentoUploader documentoUploader;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
	@In
	private ClassificacaoDocumentoManager classificacaoDocumentoManager;
	@In
	private ModeloComunicacaoRascunhoList modeloComunicacaoRascunhoList;
	@In
	private GenericManager genericManager;
	@In
	private DocumentoDownloader documentoDownloader;
	@In
	private SituacaoProcessoDAO situacaoProcessoDAO;
	@In
	private ProcessoTarefaManager processoTarefaManager;
	@In
	private ProcessoAnaliseDocumentoService processoAnaliseDocumentoService;
	@In
	private DocumentoComunicacaoService documentoComunicacaoService;
	@In
	private ProrrogacaoPrazoService prorrogacaoPrazoService;
	@In
	private DocumentoManager documentoManager;
	@In
	private DocumentoBinManager documentoBinManager;
	@In
	private DestinatarioComunicacaoService destinatarioComunicacaoService;
	@In
	private DocumentoRespostaComunicacaoDAO documentoRespostaComunicacaoDAO;
	@In
	private ProcessoDAO processoDAO;
	
	private List<ModeloComunicacao> comunicacoes;
	private List<ClassificacaoDocumento> classificacoesDocumento;
	private List<ClassificacaoDocumento> classificacoesDocumentoProrrogacaoPrazo;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private Processo processo;
	private List<Documento> documentosDestinatario; // Cache dos documentos do destinatário selecionado
	private Map<Long, Boolean> dadosCiencia = new HashMap<>(); // Cache das confirmações de ciência dos destinatários
	private List<DestinatarioBean> destinatarios;
	
	private DestinatarioBean destinatario;
	private Date dataCiencia;
	private boolean ciencia;
	private String textoCiencia;
	private boolean editorCiencia; 
	private ClassificacaoDocumento classificacaoDocumentoCiencia;
	
	private ClassificacaoDocumento classificacaoDocumentoProrrogPrazo;
	private boolean prorrogacaoPrazo;
	private boolean documentos;
	
	private boolean documentoResposta;
	private List<Documento> documentosListResposta;
	
	
	@Create
	public void init() {
		clear();
		processo = JbpmUtil.getProcesso();
		modeloComunicacaoRascunhoList.setProcesso(processo);
	}
	
	public List<ModeloComunicacao> getComunicacoesDoProcesso() {
		if (comunicacoes == null) {
			comunicacoes = modeloComunicacaoManager.listModelosComunicacaoPorProcessoRoot(processo.getNumeroProcessoRoot());
		}
		return comunicacoes;
	}
	
	public void clearCacheModelos() {
		this.comunicacoes = null;
		this.destinatario = null;
		this.destinatarios = null;
	}
	
	public List<DestinatarioBean> getDestinatarios() {
		if(destinatarios == null){
			destinatarios = new ArrayList<>();
		    List<ModeloComunicacao> comunicacoesDoProcesso = getComunicacoesDoProcesso();
		    for (ModeloComunicacao modeloComunicacao : comunicacoesDoProcesso) {
		        List<DestinatarioBean> destinatariosPorModelo = getDestinatarios(modeloComunicacao);
		        for (DestinatarioBean destinatarioBean : destinatariosPorModelo) {
	                destinatarios.add(destinatarioBean);
	            }
	        }
		    Collections.sort(destinatarios, new Comparator<DestinatarioBean>() {
				@Override
				public int compare(DestinatarioBean o1, DestinatarioBean o2) {
					try {
						Date d1 = dateFormat.parse(o1.getDataEnvio());
						Date d2 = dateFormat.parse(o2.getDataEnvio());
						return d2.compareTo(d1);
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
	    return destinatarios;
	}
	
	private List<DestinatarioBean> getDestinatarios(ModeloComunicacao modeloComunicacao) {
		List<DestinatarioBean> destinatarios = destinatarioComunicacaoService.getDestinatarios(modeloComunicacao);
		for(DestinatarioBean destinatario : destinatarios){
			dadosCiencia.put(destinatario.getIdDestinatario(), destinatarioComunicacaoService.isCienciaConfirmada(destinatario.getComunicacao()));
		}
		return destinatarios;
	}
	
	public List<ClassificacaoDocumento> getClassificacoesDocumento() {
		if (isCiencia()) {
			boolean isModelo = isEditorCiencia();
			classificacoesDocumento = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(isModelo);
		}
		return classificacoesDocumento;
	}
	
	public List<ClassificacaoDocumento> getClassificacoesDocumentoProrrogacaoPrazo() {
		if (classificacoesDocumentoProrrogacaoPrazo == null) {
			if (isProrrogacaoPrazo()) {
				classificacoesDocumentoProrrogacaoPrazo = new ArrayList<>();
				classificacoesDocumentoProrrogacaoPrazo.add(prorrogacaoPrazoService.getClassificacaoProrrogacaoPrazo(destinatario.getDestinatario()));
			}
		}
		return classificacoesDocumentoProrrogacaoPrazo;
	}
	
	public ClassificacaoDocumento getClassificacaoDocumentoProrrogPrazo() {
		return classificacaoDocumentoProrrogPrazo;
	}

	public void setClassificacaoDocumentoProrrogPrazo(ClassificacaoDocumento classificacaoDocumentoProrrogPrazo) {
		this.classificacaoDocumentoProrrogPrazo = classificacaoDocumentoProrrogPrazo;
		documentoUploader.setClassificacaoDocumento(classificacaoDocumentoProrrogPrazo);
	}

	public DestinatarioBean getDestinatario() {
		return destinatario;
	}
	
	public void setDestinatarioCiencia(DestinatarioBean destinatario) {
		clear();
		this.destinatario = destinatario;
		ciencia = true;
	}
	
	public Date getDataCiencia() {
		return dataCiencia;
	}
	
	public void setDataCiencia(Date dataCiencia) {
		this.dataCiencia = dataCiencia;
	}
	
	public boolean isCiencia() {
		return ciencia;
	}
	
	public void darCiencia() {

		try {
			validarCiencia();
			
			Documento documento = gravarDocumentoCiencia();

			Processo comunicacao = destinatario.getComunicacao();
			MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(comunicacao);
			MetadadoProcesso documentoCiencia = metadadoProcessoProvider.gerarMetadado(
					ComunicacaoMetadadoProvider.DOCUMENTO_COMPROVACAO_CIENCIA, documento.getId().toString());
			metadadoProcessoManager.persist(documentoCiencia);
			prazoComunicacaoService.darCiencia(comunicacao, dataCiencia, Authenticator.getUsuarioLogado());
			movimentarProcessoJBPM(comunicacao);

			dadosCiencia.put(destinatario.getIdDestinatario(), true);
			destinatario = null;
			dataCiencia = null;
			ciencia = false;
			setEditorCiencia(false);
			setClassificacaoDocumentoCiencia(null);
			
			FacesMessages.instance().add("Ciência informada com sucesso");
			
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
	}
	    
	private Documento gravarDocumentoCiencia() {
		
		Documento documento = null;

		if (isEditorCiencia()) {	
			try {
				DocumentoBin bin = documentoBinManager.createProcessoDocumentoBin(COMPROVANTE_DE_CIÊNCIA, textoCiencia);
				documento = documentoManager.createDocumento(destinatario.getComunicacao().getProcessoRoot(), COMPROVANTE_DE_CIÊNCIA, bin, getClassificacaoDocumentoCiencia());
			} catch (DAOException e) {
				LOG.error("", e);
				actionMessagesService.handleDAOException(e);
			}
		} else {
			documento = documentoUploader.getDocumento();
			documento.setDescricao(documento.getDocumentoBin().getNomeArquivo());
			documentoUploader.setProcesso(destinatario.getComunicacao().getProcessoRoot());
			documentoUploader.persist();
			documentoUploader.setProcesso(null);
		}
		return documento;
	}

	private void validarCiencia() {
		StringBuilder msg = new StringBuilder();
		if (getClassificacaoDocumentoCiencia() == null) {
			msg.append("Informe a classificação do documento\n");
		}
		if (getDataCiencia() == null) {
			msg.append("Informe a data de ciência\n");
		}
		if (documentoUploader.getDocumento() == null && Strings.isNullOrEmpty(getTextoCiencia())) {
			msg.append("Informe o documento de comprovação de ciência");
		}
		
		if (msg.length() > 0) {
			FacesMessages.instance().add(msg.toString());
			return;
		}
	}
	
	private void movimentarProcessoJBPM(Processo processo) throws DAOException {
		Long processIdOriginal = BusinessProcess.instance().getProcessId(); // Para caso tenha sido expedido para apenas um destinatário
		Long taskIdOriginal = BusinessProcess.instance().getTaskId();
		BusinessProcess.instance().setProcessId(null);
		BusinessProcess.instance().setTaskId(null);
		Long idTaskInstance = situacaoProcessoDAO.getIdTaskInstanceByIdProcesso(processo.getIdProcesso());
		if (idTaskInstance == null) {
			LOG.warn("idTaskInstance para o processo " + processo.getNumeroProcesso() + " nulo");
			return;
		}
		BusinessProcess.instance().setProcessId(processo.getIdJbpm());
		BusinessProcess.instance().setTaskId(idTaskInstance);
		
		TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		taskInstance.end();
		atualizarProcessoTarefa(taskInstance);
		
		BusinessProcess.instance().setProcessId(processIdOriginal);
		BusinessProcess.instance().setTaskId(taskIdOriginal);
	}
	
	private void atualizarProcessoTarefa(TaskInstance taskInstance) throws DAOException {
		ProcessoTarefa processoTarefa = processoTarefaManager.getByTaskInstance(taskInstance.getId());
		processoTarefa.setDataFim(taskInstance.getEnd());
		processoTarefaManager.update(processoTarefa);
	}
	
	public boolean isProrrogacaoPrazo() {
		return prorrogacaoPrazo;
	}
	
	public void setDestinatarioProrrogacaoPrazo(DestinatarioBean destinatario) {
		clear();
		this.destinatario = destinatario;
		prorrogacaoPrazo = true;
		documentoUploader.setClassificacaoDocumento(null);
		classificacoesDocumentoProrrogacaoPrazo = null;
	}
	
	public boolean isDocumentos() {
		return documentos;
	}
	
	public void setDestinatarioDocumentos(DestinatarioBean destinatario) {
		clear();
		this.destinatario = destinatario;
		documentos = true;
	}
	
	public void pedirProrrogacaoPrazo() {
		try {
			Processo comunicacao = destinatario.getComunicacao();
			documentoUploader.setProcesso(comunicacao.getProcessoRoot());
			Documento documento = documentoUploader.getDocumento();
			documento.setDescricao(documentoUploader.getClassificacaoDocumento().getDescricao());
			documentoUploader.persist();
			documentoUploader.clear();
			documentoUploader.setProcesso(null);

			Processo prorrogacao = processoAnaliseDocumentoService.criarProcessoAnaliseDocumentos(comunicacao, documento);
			processoAnaliseDocumentoService.inicializarFluxoDocumento(prorrogacao, null);
			
			MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(comunicacao);
			MetadadoProcesso dataPedidoProrrogacao = metadadoProcessoProvider.gerarMetadado(
					ComunicacaoMetadadoProvider.DATA_PEDIDO_PRORROGACAO, new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(new Date()));
			metadadoProcessoManager.persist(dataPedidoProrrogacao);
			
			FacesMessages.instance().add("Pedido de prorrogação de prazo efetuado com sucesso");
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
	}
	
	public Long getJbpmProcessId() {
		return JbpmUtil.getProcesso().getIdJbpm();
	}
	
	public List<Documento> getDocumentosDestinatario() {
		if (documentosDestinatario == null) {
			DestinatarioModeloComunicacao destinatarioModelo = genericManager.find(DestinatarioModeloComunicacao.class, destinatario.getIdDestinatario());
			documentosDestinatario = new ArrayList<>();
			for (DocumentoModeloComunicacao documentoModelo : destinatarioModelo.getModeloComunicacao().getDocumentos()) {
				documentosDestinatario.add(documentoModelo.getDocumento());
			}
		}
		return documentosDestinatario;
	}
	
	public void downloadDocumento(Documento documento) {
		documentoDownloader.downloadDocumento(documento);
	}
	
	public void downloadComunicacao() {
		documentoDownloader.downloadDocumento(destinatario.getDocumentoComunicacao());
	}
	
	public boolean isCienciaConfirmada(DestinatarioBean bean) {
		return dadosCiencia.get(bean.getIdDestinatario());
	}
	
	public boolean podePedirProrrogacaoPrazo(DestinatarioBean bean) {
		return prorrogacaoPrazoService.canShowClassificacaoProrrogacaoPrazo(bean.getDestinatario()) && isCienciaConfirmada(bean) &&
				prorrogacaoPrazoService.getDataPedidoProrrogacao(bean.getComunicacao()) == null;
	}
	
	public void clear() {
		clearCacheModelos();
		ciencia = false;
		prorrogacaoPrazo = false;
		documentos = false;
		documentosDestinatario = null;
		destinatario = null;
		dataCiencia = null;
		editorCiencia = false;
		documentoUploader.clear();		
		documentoResposta = false;
		documentosListResposta = null;
		
	}
	
	public String getTextoCiencia() {
		return textoCiencia;
	}

	public void setTextoCiencia(String textoCiencia) {
		this.textoCiencia = textoCiencia;
	}

	public boolean isEditorCiencia() {
		return editorCiencia;
	}

	public void setEditorCiencia(boolean editorCiencia) {
		this.editorCiencia = editorCiencia;
		if (!isEditorCiencia()){
			setClassificacaoDocumentoCiencia(null);
		}
	}

	public ClassificacaoDocumento getClassificacaoDocumentoCiencia() {
		return classificacaoDocumentoCiencia;
	}

	public void setClassificacaoDocumentoCiencia(
			ClassificacaoDocumento classificacaoDocumentoCiencia) {
		this.classificacaoDocumentoCiencia = classificacaoDocumentoCiencia;
		if (!isEditorCiencia()){ 
			documentoUploader.setClassificacaoDocumento(classificacaoDocumentoCiencia);
		}
	}
	
	public List<Documento> getDocumentosRespostaList(){
		if (documentosListResposta == null) {
			if(destinatario != null){
				documentosListResposta = processoAnaliseDocumentoService.getDocumentosRespostaComunicacao(destinatario.getComunicacao());
			}
		}
		return documentosListResposta;
	}
	
	public void setDestinatarioResposta(DestinatarioBean destinatario){
		clear();
		this.destinatario = destinatario;
		this.documentoResposta = true;
		this.documentosListResposta = null;
	}

	public boolean isDocumentoResposta() {
		return documentoResposta;
	}

	public void setDocumentoResposta(boolean documentoResposta) {
		this.documentoResposta = documentoResposta;
	}

}
