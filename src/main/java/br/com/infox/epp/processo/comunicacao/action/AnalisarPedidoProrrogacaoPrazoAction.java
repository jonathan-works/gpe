package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.documento.anexos.DocumentoDownloader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(AnalisarPedidoProrrogacaoPrazoAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
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
	
	private Processo comunicacao;
	private Processo processoDocumento;
	private DestinatarioModeloComunicacao destinatarioComunicacao;
	private Date dataFimPrazoCumprimento;
	private Integer diasProrrogacao;
	
	@Create
	public void init() {
		processoDocumento = JbpmUtil.getProcesso();
		comunicacao = processoDocumento.getProcessoPai();
		destinatarioComunicacao = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO).getValue();
		dataFimPrazoCumprimento = prazoComunicacaoService.contabilizarPrazoCumprimento(comunicacao);
		documentoComunicacaoList.setProcessoComunicacao(comunicacao);
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
		documentoDownloader.downloadDocumento(processoDocumento.getDocumentoList().get(0));
	}

	public void endTask() {
		try {
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
}
