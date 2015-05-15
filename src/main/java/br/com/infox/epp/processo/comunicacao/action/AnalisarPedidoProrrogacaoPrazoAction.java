package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import com.google.common.base.Strings;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.documento.anexos.DocumentoDownloader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(AnalisarPedidoProrrogacaoPrazoAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@Transactional
public class AnalisarPedidoProrrogacaoPrazoAction implements Serializable {
	public static final String NAME = "analisarPedidoProrrogacaoPrazoAction";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(AnalisarPedidoProrrogacaoPrazoAction.class);
	
	@In
	private PrazoComunicacaoService prazoComunicacaoService;
	@In
	private ComunicacaoService comunicacaoService;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private DocumentoComunicacaoList documentoComunicacaoList;
	@In
	private DocumentoDownloader documentoDownloader;
	@In
	private DocumentoManager documentoManager;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	
	@In
	private ComunicacaoAction comunicacaoAction;
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	
	private Processo comunicacao;
	private Processo processoDocumento;
	private DestinatarioModeloComunicacao destinatarioComunicacao;
	private Date dataFimPrazoCumprimento;
	private Integer diasProrrogacao;
	private boolean prorrogacaoPrazo;
	
	private DestinatarioBean destinatario;
	private Date novoPrazoCumprimento;

	@Create
	public void init() {
		processoDocumento = JbpmUtil.getProcesso();
		comunicacao = processoDocumento.getProcessoPai();
		destinatarioComunicacao = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO).getValue();
		dataFimPrazoCumprimento = prazoComunicacaoService.contabilizarPrazoCumprimento(comunicacao);
		documentoComunicacaoList.setProcesso(comunicacao.getProcessoRoot());
		documentoComunicacaoList.setModeloComunicacao(destinatarioComunicacao.getModeloComunicacao());
	}
	
	public String getMeioExpedicao() {
		return destinatarioComunicacao.getMeioExpedicao().getLabel();
	}
	
	public Date getDataFimPrazoCumprimento() {
		return dataFimPrazoCumprimento;
	}
	
	public void downloadComunicacao() {
		try {
			byte[] pdf = comunicacaoService.gerarPdfCompleto(destinatarioComunicacao.getModeloComunicacao(), destinatarioComunicacao);
			FileDownloader.download(pdf, "application/pdf", "Comunicação.pdf");
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
	}
	
	public void downloadDocumento(Documento documento) {
		documentoDownloader.downloadDocumento(documento);
	}
	
	public void downloadSolicitacaoProrrogacaoPrazo() {
		Documento documentoPedidoProrrogacao = processoDocumento.getMetadado(EppMetadadoProvider.DOCUMENTO_EM_ANALISE).getValue();
		documentoDownloader.downloadDocumento(documentoPedidoProrrogacao);
	}

	public void endTask() {
		try {
//			TODO: ver onde colocar o metadado ComunicacaoMetadadoProvider.DATA_ANALISE_PRORROGACAO
			MetadadoProcesso metadadoPrazo = comunicacao.getMetadado(ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO);
			if (metadadoPrazo != null && diasProrrogacao != null) {
				int prazoExistente = Integer.valueOf(metadadoPrazo.getValor());
				metadadoPrazo.setValor(String.valueOf(prazoExistente + diasProrrogacao));
				metadadoProcessoManager.update(metadadoPrazo);
			}
			TaskInstanceHome taskInstanceHome = TaskInstanceHome.instance();
			taskInstanceHome.end(taskInstanceHome.getName());
		} catch (Exception e) {
			LOG.error("", e);
			if (e instanceof DAOException) {
				actionMessagesService.handleDAOException((DAOException) e);
			} else {
				actionMessagesService.handleException("Erro ao efetuar prorrogação de prazo", e);
			}
		}
	}
	
	public Integer getDiasProrrogacao() {
		return diasProrrogacao;
	}
	
	public void setDiasProrrogacao(Integer diasProrrogacao) {
		if (diasProrrogacao < 0) {
			FacesMessages.instance().add("O prazo deve ser maior ou igual a 0");
			return;
		}
		this.diasProrrogacao = diasProrrogacao;
	}
	
	public void setDestinatarioProrrogacaoPrazo(DestinatarioBean bean){
		this.destinatario = bean;
		setProrrogacaoPrazo(true);
	}
	
	public List<DestinatarioBean> getDestinatariosCienciaConfirmada() {
		List<DestinatarioBean> destinatarios = new ArrayList<>();
	    List<ModeloComunicacao> comunicacoesDoProcesso = modeloComunicacaoManager.listModelosComunicacaoPorProcesso(comunicacao.getProcessoRoot());
	    for (ModeloComunicacao modeloComunicacao : comunicacoesDoProcesso) {
	        List<DestinatarioBean> destinatariosPorModelo = comunicacaoAction.getDestinatarios(modeloComunicacao);
	        for (DestinatarioBean destinatarioBean : destinatariosPorModelo) {
	        	if (!destinatarioBean.getPrazoFinal().equals("-")){
	        		destinatarios.add(destinatarioBean);
	        	}
	        	
            }
        }
	    return destinatarios;
	}
	
	public String getStatusComunicacao(DestinatarioBean bean){
		Processo comunicacao  = bean.getComunicacao();
		if (comunicacao != null) {
            MetadadoProcesso mp = comunicacao.getMetadado(EppMetadadoProvider.STATUS_PROCESSO);
            if (mp != null){
            	return mp.toString();
            }
        }
		return "-";
	}
	
	public boolean isPedidoDentroDoPrazo(DestinatarioBean bean){
		Date dataLimiteCumprimento = getDataLimiteCumprimento(bean);
		if(dataLimiteCumprimento.after(new Date())){
			return true;
		}
		return false;
	}
	
	public Date getDataLimiteCumprimento(DestinatarioBean bean){
		MetadadoProcesso metadadoPrazo = bean.getComunicacao().getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO);
		Date dataLimiteCumprimento = new Date();
		if (metadadoPrazo != null && bean.getPrazoFinal() != null) {
			dataLimiteCumprimento = metadadoPrazo.getValue();
		}
		return dataLimiteCumprimento;
	}
	
	public Date getDataLimiteCumprimento(){
		if(destinatario != null){
			return getDataLimiteCumprimento(destinatario);
		}
		return new Date();
	}
	
	public void prorrogarPrazoDeCumprimento(){
		if (isPedidoDentroDoPrazo(destinatario)) {
				Date dataLimiteCumprimento = getDataLimiteCumprimento(destinatario);
				if(dataLimiteCumprimento.before(novoPrazoCumprimento)){
					MetadadoProcesso metadadoDataLimiteCumprimento = destinatario.getComunicacao().getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO);
					String dateFormatted = new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(novoPrazoCumprimento);
					metadadoDataLimiteCumprimento.setValor(dateFormatted);
					try {
						metadadoProcessoManager.update(metadadoDataLimiteCumprimento);
						FacesMessages.instance().add("Prazo prorrogado com sucesso");
					} catch (DAOException e) {
						LOG.error("", e);
						actionMessagesService.handleDAOException(e);
					}
				}
		}
	}
		
	public DestinatarioBean getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(DestinatarioBean destinatario) {
		this.destinatario = destinatario;
	}

	public boolean isProrrogacaoPrazo() {
		return prorrogacaoPrazo;
	}

	public void setProrrogacaoPrazo(boolean prorrogacaoPrazo) {
		this.prorrogacaoPrazo = prorrogacaoPrazo;
	}
	
	public Date getNovoPrazoCumprimento() {
		return novoPrazoCumprimento;
	}

	public void setNovoPrazoCumprimento(Date novoPrazoCumprimento) {
		this.novoPrazoCumprimento = DateUtil.getEndOfDay(novoPrazoCumprimento);
	}
	
		
}
