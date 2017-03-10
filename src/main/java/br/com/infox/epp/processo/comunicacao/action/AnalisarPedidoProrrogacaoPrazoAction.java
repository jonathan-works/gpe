package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.service.ProrrogacaoComunicacaoService;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.variable.components.Taskpage;
import br.com.infox.ibpm.variable.components.TaskpageController;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Taskpage(id="analisarPedidoProrrogacaoPrazo", xhtmlPath="/WEB-INF/taskpages/analisarPedidoProrrogacaoPrazo.xhtml", name="Analisar pedido de prorrogação de Prazo")
@Named(AnalisarPedidoProrrogacaoPrazoAction.NAME)
@ViewScoped
public class AnalisarPedidoProrrogacaoPrazoAction implements Serializable, TaskpageController {
	public static final String NAME = "analisarPedidoProrrogacaoPrazoAction";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(AnalisarPedidoProrrogacaoPrazoAction.class);
	
	@Inject
	private ActionMessagesService actionMessagesService;
 	@Inject
	private CalendarioEventosManager calendarioEventosManager;
	@Inject
	private DocumentoComunicacaoList documentoComunicacaoList;
	@Inject
	private ProrrogacaoComunicacaoService prorrogacaoComunicacaoService;
	
	private Processo processoDocumento;
	private List<Documento> documentosAnalise;
	private Processo comunicacao;
	private DestinatarioModeloComunicacao destinatarioComunicacao;
	private Date dataFimPrazoCumprimento;
	
	private Integer diasProrrogacao;
	private Date novoPrazoCumprimento;

	@PostConstruct
	public void init() {
		processoDocumento = JbpmUtil.getProcesso();
		initDadosAnalise();
		comunicacao = processoDocumento.getProcessoPai();
		initDadosComunicacao();
		clear();
	}

	private void clear() {
		diasProrrogacao = null;
		novoPrazoCumprimento = null;
	}

	private void initDadosAnalise() {
		List<MetadadoProcesso> metadadosDocumentos =  processoDocumento.getMetadadoList(EppMetadadoProvider.DOCUMENTO_EM_ANALISE);
		setDocumentosAnalise(new ArrayList<Documento>());
		for (MetadadoProcesso metadadoProcesso : metadadosDocumentos) {
			getDocumentosAnalise().add((Documento) metadadoProcesso.getValue());
		}
	}

	private void initDadosComunicacao() {
		destinatarioComunicacao = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO).getValue();
		documentoComunicacaoList.setModeloComunicacao(destinatarioComunicacao.getModeloComunicacao());
		MetadadoProcesso metadadoDataLimite = comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO); 
		if (metadadoDataLimite != null) {
			dataFimPrazoCumprimento = metadadoDataLimite.getValue();
		}
	}
	
	public void atualizaNovoPrazo() {
		if (getDiasProrrogacao() != null) {
			novoPrazoCumprimento = DateUtil.getEndOfDay(calendarioEventosManager.getPrimeiroDiaUtil(getDataFimPrazoCumprimento(), getDiasProrrogacao()));
		} else {
			novoPrazoCumprimento = null;
		}
	}
	
	public void prorrogarPrazoDeCumprimento(){
		if (getNovoPrazoCumprimento() != null) {
			try {
				atualizaNovoPrazo();
				dataFimPrazoCumprimento = getNovoPrazoCumprimento();
				prorrogacaoComunicacaoService.prorrogarPrazo(destinatarioComunicacao, getDiasProrrogacao());
				clear();
				FacesMessages.instance().add("Prazo prorrogado com sucesso");
			} catch (DAOException e) {
				FacesMessages.instance().add("Erro ao prorrogar prazo");
			}
		} else {
			FacesMessages.instance().add("Não foi selecionada a quantidade de dias para prorrogação");
		}
	}
	
	public void endTask() {
		try {
			prorrogacaoComunicacaoService.finalizarAnaliseProrrogacao(comunicacao);
		} catch (Exception e) {
			LOG.error("", e);
			if (e instanceof DAOException) {
				actionMessagesService.handleDAOException((DAOException) e);
			} else {
				actionMessagesService.handleException("Erro ao efetuar prorrogação de prazo", e);
			}
		}
	}

	public Date getDataCiencia(){
		MetadadoProcesso metadadoCiencia = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_CIENCIA);
		Date dataCiencia = null;
		if(metadadoCiencia != null){
			dataCiencia = metadadoCiencia.getValue();
		}
		return dataCiencia;
	}
	
	public String getResponsavelCiencia(){
		MetadadoProcesso metadadoResponsavelCiencia = comunicacao.getMetadado(ComunicacaoMetadadoProvider.RESPONSAVEL_CIENCIA);
		if(metadadoResponsavelCiencia != null){
			return metadadoResponsavelCiencia.getValue().toString();
		}
		return "-";
	}
		
	public String getMeioExpedicao() {
		return destinatarioComunicacao.getMeioExpedicao().getDescricao();
	}
	
	public Date getDataFimPrazoCumprimento() {
		return dataFimPrazoCumprimento;
	}
	
	public List<Documento> getDocumentosAnalise() {
		return documentosAnalise;
	}

	public void setDocumentosAnalise(List<Documento> documentosAnalise) {
		this.documentosAnalise = documentosAnalise;
	}

	public Date getNovoPrazoCumprimento() {
		return novoPrazoCumprimento;
	}

	public void setNovoPrazoCumprimento(Date novoPrazoCumprimento) {
		this.novoPrazoCumprimento = DateUtil.getEndOfDay(novoPrazoCumprimento);
	}

	public DestinatarioModeloComunicacao getDestinatarioComunicacao(){
		return destinatarioComunicacao;
	}
	
	public void setDestinatarioComunicacao(DestinatarioModeloComunicacao destinatarioComunicacao) {
		this.destinatarioComunicacao = destinatarioComunicacao;
	}

	public Integer getDiasProrrogacao() {
		return diasProrrogacao;
	}

	public void setDiasProrrogacao(Integer diasProrrogacao) {
		this.diasProrrogacao = diasProrrogacao;
	}

}
