package br.com.infox.epp.processo.comunicacao.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.joda.time.DateTime;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.system.Parametros;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.task.service.MovimentarTarefaService;
import br.com.infox.seam.exception.BusinessException;

@Name(PrazoComunicacaoService.NAME)
@AutoCreate
@Stateless
@Scope(ScopeType.STATELESS)
@Transactional
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@ContextDependency
public class PrazoComunicacaoService {
	public static final String NAME = "prazoComunicacaoService";
	
	@Inject
	private CalendarioEventosManager calendarioEventosManager;
	@Inject
	private MetadadoProcessoManager metadadoProcessoManager;
	@Inject
	private MovimentarTarefaService movimentarTarefaService;
	
	@In
	private DocumentoManager documentoManager;
	@In
	private ProcessoManager processoManager;

	public Date contabilizarPrazoCiencia(Processo comunicacao) {
		DestinatarioModeloComunicacao destinatario = getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.DESTINATARIO);
        Integer qtdDias = destinatario.getModeloComunicacao().getTipoComunicacao().getQuantidadeDiasCiencia();
        Date hoje = new Date();
        return calendarioEventosManager.getPrimeiroDiaUtil(hoje, qtdDias);
    }
    
	public Date contabilizarPrazoCumprimento(Processo comunicacao) {
		Date dataCiencia = getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.DATA_CIENCIA);
        Integer diasPrazoCumprimento = getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO);
        if (diasPrazoCumprimento == null || dataCiencia == null) {
        	return null;
        }
        return calendarioEventosManager.getPrimeiroDiaUtil(dataCiencia, diasPrazoCumprimento);
    }
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void darCiencia(Processo comunicacao, Date dataCiencia, UsuarioLogin usuarioCiencia) throws DAOException {
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(comunicacao);
		MetadadoProcesso metadadoDataCiencia = metadadoProcessoProvider.gerarMetadado(
				ComunicacaoMetadadoProvider.DATA_CIENCIA, new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(dataCiencia));
		MetadadoProcesso metadadoResponsavelCiencia = metadadoProcessoProvider.gerarMetadado(
				ComunicacaoMetadadoProvider.RESPONSAVEL_CIENCIA, usuarioCiencia.getIdUsuarioLogin().toString());
		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoDataCiencia));
		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoResponsavelCiencia));

		adicionarPrazoDeCumprimento(comunicacao, dataCiencia);
		adicionarVariavelCienciaAutomaticaAoProcesso(usuarioCiencia, comunicacao);
		adicionarVariavelPossuiPrazoAoProcesso(comunicacao);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void darCienciaManual(Processo comunicacao, Date dataCiencia, Documento documentoCiencia) throws DAOException {
		documentoManager.gravarDocumentoNoProcesso(comunicacao.getProcessoRoot(), documentoCiencia);
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(comunicacao);
		MetadadoProcesso metadadoCiencia = metadadoProcessoProvider.gerarMetadado(
				ComunicacaoMetadadoProvider.DOCUMENTO_COMPROVACAO_CIENCIA, documentoCiencia.getId().toString());
		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoCiencia));
		darCiencia(comunicacao, dataCiencia, Authenticator.getUsuarioLogado());
		movimentarTarefaService.finalizarTarefaEmAberto(comunicacao);
	}

	protected void adicionarPrazoDeCumprimento(Processo comunicacao, Date dataCiencia)
			throws DAOException {
		
		Integer diasPrazoCumprimento = getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO);
		if (diasPrazoCumprimento == null){
			diasPrazoCumprimento = -1;
		}
		if (diasPrazoCumprimento >= 0) {
			MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(comunicacao);
    		Date limiteDataCumprimento = contabilizarPrazoCumprimento(comunicacao);
    		String dataLimite = new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(limiteDataCumprimento);
    		MetadadoProcesso metadadoLimiteDataCumprimento = metadadoProcessoProvider.gerarMetadado(
    		        ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO, dataLimite);
    		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoLimiteDataCumprimento));
    		
    		metadadoLimiteDataCumprimento = metadadoProcessoProvider.gerarMetadado(
    				ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO_INICIAL, dataLimite);
    		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoLimiteDataCumprimento));
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void darCumprimento(Processo comunicacao, Date dataCumprimento, UsuarioLogin usuarioCumprimento) throws DAOException {
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(comunicacao);
		String dateFormatted = new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(dataCumprimento);
		String idUsuarioCumprimento = usuarioCumprimento.getIdUsuarioLogin().toString();
		MetadadoProcesso metadadoDataCumprimento = 
				metadadoProcessoProvider.gerarMetadado(ComunicacaoMetadadoProvider.DATA_CUMPRIMENTO, dateFormatted);
		MetadadoProcesso metadadoResponsavelCumprimento = 
				metadadoProcessoProvider.gerarMetadado(ComunicacaoMetadadoProvider.RESPONSAVEL_CUMPRIMENTO, idUsuarioCumprimento);
		
		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoDataCumprimento));
		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoResponsavelCumprimento));
	}
	
	private void adicionarVariavelCienciaAutomaticaAoProcesso(UsuarioLogin usuarioCiencia, Processo comunicacao) {
		Integer idUsuarioSistema = Integer.valueOf(Parametros.ID_USUARIO_SISTEMA.getValue());
		boolean isUsuarioSistema = idUsuarioSistema.equals(usuarioCiencia.getIdUsuarioLogin());
		org.jbpm.graph.exe.ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstanceForUpdate(comunicacao.getIdJbpm());
		ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("cienciaAutomatica", isUsuarioSistema);
	}
	
    private void adicionarVariavelPossuiPrazoAoProcesso(Processo comunicacao) {
    	MetadadoProcesso metadadoProcesso = comunicacao.getMetadado(ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO);
    	boolean possuiPrazoParaCumprimento = metadadoProcesso != null;
    	org.jbpm.graph.exe.ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstanceForUpdate(comunicacao.getIdJbpm());
		ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("possuiPrazoParaCumprimento", possuiPrazoParaCumprimento);
    }
    
    public void movimentarComunicacaoPrazoExpirado(Processo comunicacao, MetadadoProcessoDefinition metadadoPrazo) throws DAOException{
		Date dataLimite = getValueMetadado(comunicacao, metadadoPrazo);
		if (dataLimite != null) {
			DateTime dataParaCumprimento = new DateTime(dataLimite.getTime());
			if (dataParaCumprimento.isBeforeNow()) {
				processoManager.movimentarProcessoJBPM(comunicacao);
			}
		}
	}
    
    public Date getDataLimiteCumprimento(Processo comunicacao){
    	return getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO);
    }

	//Prorrogação de Prazo Service
	
	public Boolean isClassificacaoProrrogacaoPrazo(ClassificacaoDocumento classificacaoDocumento, TipoComunicacao tipoComunicacao) {
		return false;
	}
	
	public Boolean canRequestProrrogacaoPrazo(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
		return false;
	}
	
	public Boolean containsClassificacaoProrrogacaoPrazo(List<Documento> documentos, TipoComunicacao tipoComunicacao) {
		return false;
	}
	
	public Boolean canTipoComunicacaoRequestProrrogacaoPrazo(TipoComunicacao tipoComunicacao){
		return false;
	}
	
	public ClassificacaoDocumento getClassificacaoProrrogacaoPrazo(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
		throw new BusinessException("O tipo de comunicação " + destinatarioModeloComunicacao.getModeloComunicacao().getTipoComunicacao().getDescricao() + " não admite pedido de prorrogação de prazo");
	}
	
	public void finalizarAnalisePedido(Processo comunicacao) throws DAOException{
		TaskInstanceHome.instance().end(TaskInstanceHome.instance().getName());
	}
	
	public Date getDataPedidoProrrogacao(Processo comunicacao){
    	return getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.DATA_PEDIDO_PRORROGACAO);
    }
    
    public boolean hasPedidoProrrogacaoEmAberto(Processo comunicacao){
    	return getDataPedidoProrrogacao(comunicacao) != null && getDataAnaliseProrrogacao(comunicacao) == null;
    }
    
    public Date getDataAnaliseProrrogacao(Processo comunicacao){
    	return getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.DATA_ANALISE_PRORROGACAO);
    }
    
    public boolean isPrazoProrrogado(Processo comunicacao){
    	return  !getDataLimiteCumprimentoInicial(comunicacao).equals(getDataLimiteCumprimento(comunicacao));
    }
    
    public Date getDataLimiteCumprimentoInicial(Processo comunicacao){
    	return getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO);
    }
    
    public String getStatusProrrogacaoFormatado(Processo comunicacao){
    	if(comunicacao != null && getDataPedidoProrrogacao(comunicacao) != null){
    		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    		if (hasPedidoProrrogacaoEmAberto(comunicacao)){
    			return "Aguardando análise desde " + dateFormat.format(getDataPedidoProrrogacao(comunicacao));
    		}else{
    			String dataAnalise = dateFormat.format(getDataAnaliseProrrogacao(comunicacao)); 
				if(isPrazoProrrogado(comunicacao)){
					return "Prazo original: " + dateFormat.format(getDataLimiteCumprimentoInicial(comunicacao));
				}else{
					return "Prorrogação negada em " + dataAnalise;
				}
    		}
    	}
		return "";
    }
    
    protected <T> T getValueMetadado(Processo processo, MetadadoProcessoDefinition metaDefinition){
    	MetadadoProcesso metadadoProcesso = processo.getMetadado(metaDefinition);
    	if(metadadoProcesso != null){
    		return metadadoProcesso.getValue();
    	}
    	return null;
    }
}
