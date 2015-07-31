package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.DestinatarioComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.documento.anexos.DocumentoDownloader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

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
	private GenericManager genericManager;
	@In
	private DestinatarioComunicacaoService destinatarioComunicacaoService;
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private ProcessoManager processoManager;
	
	private Processo comunicacao;
	private Processo processoDocumento;
	private DestinatarioModeloComunicacao destinatarioComunicacao;
	private Date dataFimPrazoCumprimento;
	private boolean prorrogacaoPrazo;
	private boolean documentos;
	private List<Documento> documentosDestinatario; // Cache dos documentos do destinatário selecionado
	private List<DestinatarioBean> destinatarioCienciaConfirmada;
	
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
		destinatarioCienciaConfirmada = null;
		clearDestinatarioBean();
	}
	
	public String getMeioExpedicao() {
		return destinatarioComunicacao.getMeioExpedicao().getLabel();
	}
	
	public Date getDataFimPrazoCumprimento() {
		return dataFimPrazoCumprimento;
	}
	
	public void endTask() {
		try {
			prazoComunicacaoService.finalizarAnalisePedido(comunicacao);		
		} catch (Exception e) {
			LOG.error("", e);
			if (e instanceof DAOException) {
				actionMessagesService.handleDAOException((DAOException) e);
			} else {
				actionMessagesService.handleException("Erro ao efetuar prorrogação de prazo", e);
			}
		}
	}
		
	public void clearDestinatarioBean(){
		setDestinatarioProrrogacaoPrazo(null);
		setProrrogacaoPrazo(false);
		setDocumentos(false);
		setNovoPrazoCumprimento(null);
	}
	
	public void setDestinatarioProrrogacaoPrazo(DestinatarioBean bean){
		this.destinatario = bean;
		setProrrogacaoPrazo(true);
		setDocumentos(false);
	}
	
	public void setDestinatarioDocumentos(DestinatarioBean bean){
		this.destinatario = bean;
		setProrrogacaoPrazo(false);
		setDocumentos(true);
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
	
	public List<DestinatarioBean> getDestinatarioCienciaConfirmada() {
		if(destinatarioCienciaConfirmada == null){
			destinatarioCienciaConfirmada = new ArrayList<>();
		    List<ModeloComunicacao> comunicacoesDoProcesso = modeloComunicacaoManager.listModelosComunicacaoPorProcessoRoot(comunicacao.getNumeroProcessoRoot());
		    for (ModeloComunicacao modeloComunicacao : comunicacoesDoProcesso) { 
		        List<DestinatarioBean> destinatariosPorModelo = destinatarioComunicacaoService.getDestinatarios(modeloComunicacao);
		        for (DestinatarioBean destinatarioBean : destinatariosPorModelo) {
		        	if (!destinatarioBean.getPrazoFinal().equals("-") && 
		        			prazoComunicacaoService.canTipoComunicacaoRequestProrrogacaoPrazo(destinatarioBean.getModeloComunicacao().getTipoComunicacao())){
		        		destinatarioCienciaConfirmada.add(destinatarioBean);
		        	}
	            }
	        }
		}
	    return destinatarioCienciaConfirmada;
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
	
	public Date getDataLimiteCumprimento(DestinatarioBean bean){
		return prazoComunicacaoService.getDataLimiteCumprimento(bean.getComunicacao());
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
	
	public boolean isPedidoDentroDoPrazo(DestinatarioBean bean){
		Date dataLimiteCumprimento = getDataLimiteCumprimento(bean);
		return dataLimiteCumprimento.after(new Date()) || prazoComunicacaoService.hasPedidoProrrogacaoEmAberto(bean.getComunicacao());
	}
	
	/**
	 * Seta o starDate do calendário 
	 */
	public Date getDataLimiteCumprimento(){
		if(destinatario != null){
			Calendar calendar =  Calendar.getInstance();
			calendar.setTime(getDataLimiteCumprimento(destinatario));
			calendar.add(Calendar.DATE, 1);
			return calendar.getTime();
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
						destinatarioCienciaConfirmada = null;
						clearDestinatarioBean();
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

	public boolean isDocumentos() {
		return documentos;
	}

	public void setDocumentos(boolean documentos) {
		this.documentos = documentos;
	}
	
	
	public Processo getProcessoDocumento(){
		return processoDocumento;
	}
	
	public void setProcessoDocumento(Processo processoDocumento) {
		this.processoDocumento = processoDocumento;
	}

	public Processo getComunicacao(){
		return comunicacao;
	}
	
	public void setComunicacao(Processo comunicacao) {
		this.comunicacao = comunicacao;
	}

	public DestinatarioModeloComunicacao getDestinatarioComunicacao(){
		return destinatarioComunicacao;
	}
	
	public void setDestinatarioComunicacao(DestinatarioModeloComunicacao destinatarioComunicacao) {
		this.destinatarioComunicacao = destinatarioComunicacao;
	}

}
