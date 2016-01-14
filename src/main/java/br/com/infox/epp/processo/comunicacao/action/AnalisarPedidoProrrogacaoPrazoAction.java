package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.joda.time.DateTime;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Name(AnalisarPedidoProrrogacaoPrazoAction.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
@Transactional
@ContextDependency
public class AnalisarPedidoProrrogacaoPrazoAction implements Serializable {
	public static final String NAME = "analisarPedidoProrrogacaoPrazoAction";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(AnalisarPedidoProrrogacaoPrazoAction.class);
	
	@Inject
	private PrazoComunicacaoService prazoComunicacaoService;
	@Inject
	private ActionMessagesService actionMessagesService;
	@Inject
	private MetadadoProcessoManager metadadoProcessoManager;
	@Inject
	private CalendarioEventosManager calendarioEventosManager;
	
	private DocumentoComunicacaoList documentoComunicacaoList = ComponentUtil.getComponent(DocumentoComunicacaoList.NAME);
	
	private Processo processoDocumento;
	private List<Documento> documentosAnalise;
	private Processo comunicacao;
	private DestinatarioModeloComunicacao destinatarioComunicacao;
	private Date dataFimPrazoCumprimento;
	
	private Integer diasProrrogacao;
	private Date novoPrazoCumprimento;

	@Create
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
		novoPrazoCumprimento = DateUtil.getEndOfDay(calendarioEventosManager.getPrimeiroDiaUtil(getDataFimPrazoCumprimento(), getDiasProrrogacao()));
	}
	
	public void prorrogarPrazoDeCumprimento(){
		if (getNovoPrazoCumprimento() != null) {
			try {
				dataFimPrazoCumprimento = getNovoPrazoCumprimento();
				MetadadoProcesso metadadoDataFimCumprimento = comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO);
				metadadoDataFimCumprimento.setValor(new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(getDataFimPrazoCumprimento()));
				metadadoProcessoManager.update(metadadoDataFimCumprimento);
				criaMetadadoAnalisePedidoProrrogacao();
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
			prazoComunicacaoService.finalizarAnalisePedido(comunicacao);
			if (comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_ANALISE_PRORROGACAO) == null) {
				criaMetadadoAnalisePedidoProrrogacao();
			}
		} catch (Exception e) {
			LOG.error("", e);
			if (e instanceof DAOException) {
				actionMessagesService.handleDAOException((DAOException) e);
			} else {
				actionMessagesService.handleException("Erro ao efetuar prorrogação de prazo", e);
			}
		}
	}

	private void criaMetadadoAnalisePedidoProrrogacao() {
		MetadadoProcessoProvider metadadoProvider = new MetadadoProcessoProvider(comunicacao);
		MetadadoProcesso metadadoAnalise = metadadoProvider.gerarMetadado(ComunicacaoMetadadoProvider.DATA_ANALISE_PRORROGACAO, 
				new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(DateTime.now().toDate()));
		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoAnalise));
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
		return destinatarioComunicacao.getMeioExpedicao().getLabel();
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

	public Processo getProcessoDocumento(){
		return processoDocumento;
	}
	
	public void setProcessoDocumento(Processo processoDocumento) {
		this.processoDocumento = processoDocumento;
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
