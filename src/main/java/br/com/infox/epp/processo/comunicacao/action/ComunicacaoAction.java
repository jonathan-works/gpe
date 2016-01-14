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

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.envio.action.EnvioComunicacaoController;
import br.com.infox.epp.processo.comunicacao.list.ModeloComunicacaoRascunhoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.documento.anexos.DocumentoDownloader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.service.ProcessoAnaliseDocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Named
@Stateful
@ViewScoped
public class ComunicacaoAction implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "comunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(ComunicacaoAction.class);
	
	
	private ProcessoAnaliseDocumentoService processoAnaliseDocumentoService = ComponentUtil.getComponent(ProcessoAnaliseDocumentoService.NAME);
	private DocumentoDownloader documentoDownloader = ComponentUtil.getComponent(DocumentoDownloader.NAME);
	private ComunicacaoService comunicacaoService = ComponentUtil.getComponent(ComunicacaoService.NAME); 
	
	@Inject
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@Inject	
	private ModeloComunicacaoRascunhoList modeloComunicacaoRascunhoList;
	@Inject
	protected InfoxMessages infoxMessages;
	@Inject
	private EntityManager entityManager;
	@Inject
	private EnvioComunicacaoController envioComunicacaoController;
	
	private List<ModeloComunicacao> comunicacoes;
	private Processo processo;
	private List<Documento> documentosDestinatario; // Cache dos documentos do destinatário selecionado
	private Map<Long, Boolean> dadosCiencia = new HashMap<>(); // Cache das confirmações de ciência dos destinatários
	private List<DestinatarioBean> destinatarios;
	
	private DestinatarioBean destinatario;
	private boolean documentos;
	private boolean documentoResposta;
	private List<Documento> documentosListResposta;
	
	protected static final Comparator<DestinatarioBean> comparatorDestinatarios = new Comparator<DestinatarioBean>() {
		@Override
		public int compare(DestinatarioBean o1, DestinatarioBean o2) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date d1 = dateFormat.parse(o1.getDataEnvio());
				Date d2 = dateFormat.parse(o2.getDataEnvio());
				return d2.compareTo(d1);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	};
	
	@PostConstruct
	public void init() {
		setProcesso(JbpmUtil.getProcesso());
	}
	
	public void reabrirComunicacao(ModeloComunicacao modeloComunicacao) {
		try {
			comunicacaoService.reabrirComunicacao(modeloComunicacao);
			envioComunicacaoController.init(); //para recarregar a página de tarefa
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.sucesso.reabertura"));
		} catch (DAOException | CloneNotSupportedException e) {
			LOG.error("Erro ao rebarir comunicação", e);
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.erro.reabertura"));
		}
	}
	
	public void excluirComunicacao(ModeloComunicacao modeloComunicacao) {
		try {
			comunicacaoService.excluirComunicacao(modeloComunicacao);
			envioComunicacaoController.init(); //para recarregar a página de tarefa
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.sucesso.exclusao"));
		} catch (DAOException e) {
			LOG.error("Erro ao excluir comunicação", e);
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.erro.exclusao"));
		}
	}
	
	public boolean podeReabrirComunicacao(ModeloComunicacao modeloComunicacao) {
		return modeloComunicacao.getFinalizada() && !modeloComunicacaoManager.isExpedida(modeloComunicacao);
	}
	
	public boolean podeExcluirModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		return !modeloComunicacao.getFinalizada() || (modeloComunicacao.getFinalizada() && !modeloComunicacaoManager.hasComunicacaoExpedida(modeloComunicacao));
	}
	
	public void setProcesso(Processo processo) {
	    this.processo = processo;
	    modeloComunicacaoRascunhoList.setProcesso(processo);
	    clear();
	}
	
	public List<ModeloComunicacao> getComunicacoesDoProcesso() {
		if (comunicacoes == null) {
			comunicacoes = modeloComunicacaoManager.listModelosComunicacaoPorProcessoRoot(processo.getNumeroProcessoRoot());
		}
		return comunicacoes;
	}
	
	public void clearCacheModelos() {
		for (ModeloComunicacao modeloComunicacao : modeloComunicacaoRascunhoList.getResultList()) {
			modeloComunicacaoManager.detach(modeloComunicacao);
		}
		modeloComunicacaoRascunhoList.refresh();
		this.comunicacoes = null;
		this.destinatario = null;
		this.destinatarios = null;
	}
	
	public List<DestinatarioBean> getDestinatarios() {
		if(destinatarios == null){
		    destinatarios = initDestinatarios();
		    Collections.sort(destinatarios, comparatorDestinatarios );
		}
	    return destinatarios;
	}

	protected List<DestinatarioBean> initDestinatarios() {
		List<DestinatarioBean> destinatarios = modeloComunicacaoManager.listDestinatarios(processo.getNumeroProcessoRoot());
		for (DestinatarioBean destinatario : destinatarios) {
			dadosCiencia.put(destinatario.getIdDestinatario(), !destinatario.getDataConfirmacao().equals("-"));
		}
		return destinatarios;
	}

	public DestinatarioBean getDestinatario() {
		return destinatario;
	}
	
	protected void setDestinatario(DestinatarioBean destinatarioBean) {
		this.destinatario = destinatarioBean;
	}
		
	public boolean isDocumentos() {
		return documentos;
	}
	
	public void setDestinatarioDocumentos(DestinatarioBean destinatario) {
		clear();
		this.destinatario = destinatario;
		documentos = true;
	}
	
	public Long getJbpmProcessId() {
		return JbpmUtil.getProcesso().getIdJbpm();
	}
	
	public List<Documento> getDocumentosDestinatario() {
		if (documentosDestinatario == null) {
			DestinatarioModeloComunicacao destinatarioModelo = getDestinatarioModeloComunicacao(destinatario);
			documentosDestinatario = new ArrayList<>();
			for (DocumentoModeloComunicacao documentoModelo : destinatarioModelo.getModeloComunicacao().getDocumentos()) {
				documentosDestinatario.add(documentoModelo.getDocumento());
				// FIXME: Só para não dar lazy na tela
				documentoModelo.getDocumento().getDocumentoBin().getSize();
			}
		}
		return documentosDestinatario;
	}

	protected DestinatarioModeloComunicacao getDestinatarioModeloComunicacao(DestinatarioBean bean) {
		return entityManager.find(DestinatarioModeloComunicacao.class, bean.getIdDestinatario());
	}
	
	public void downloadDocumento(Documento documento) {
		documentoDownloader.downloadDocumento(documento);
	}
	
	public void downloadComunicacao() {
		documentoDownloader.downloadDocumento(getDestinatarioModeloComunicacao(destinatario).getDocumentoComunicacao());
	}
	
	public boolean isCienciaConfirmada(DestinatarioBean bean) {
		return dadosCiencia.get(bean.getIdDestinatario());
	}
	
	public void clear() {
		clearCacheModelos();
		documentos = false;
		documentosDestinatario = null;
		destinatario = null;
		documentoResposta = false;
		documentosListResposta = null;
	}
	
	public List<Documento> getDocumentosRespostaList(){
		if (documentosListResposta == null) {
			if(destinatario != null){
				documentosListResposta = processoAnaliseDocumentoService.getDocumentosRespostaComunicacao(getDestinatarioModeloComunicacao(destinatario).getProcesso());
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
	
	public String getComunicacoesExpedidasTitle(){
		return infoxMessages.get("comunicacao.comunicacoes");
	}
	
	public Documento getComunicacaoDestinatario() {
		if (destinatario != null) {
			DestinatarioModeloComunicacao destinatarioModeloComunicacao = getDestinatarioModeloComunicacao(destinatario);
			return destinatarioModeloComunicacao.getDocumentoComunicacao();
		}
		return null;
	}
	
	public Map<Long, Boolean> getDadosCiencia() {
		return dadosCiencia;
	}

}
