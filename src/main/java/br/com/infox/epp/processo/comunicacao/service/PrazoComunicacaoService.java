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
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.joda.time.DateTime;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.system.Parametros;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;

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
	
	private ProcessoManager processoManager = ComponentUtil.getComponent(ProcessoManager.NAME);
	
	public Boolean isClassificacaoProrrogacaoPrazo(ClassificacaoDocumento classificacaoDocumento, TipoComunicacao tipoComunicacao) {
		return false;
	}
	
	public Boolean canShowClassificacaoProrrogacaoPrazo(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
		return false;
	}
	
	public Boolean containsClassificacaoProrrogacaoPrazo(List<Documento> documentos, TipoComunicacao tipoComunicacao) {
		return false;
	}
	
	public Boolean canRequestProrrogacaoPrazo(TipoComunicacao tipoComunicacao){
		return false;
	}
	
	public ClassificacaoDocumento getClassificacaoProrrogacaoPrazo(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
		throw new BusinessException("O tipo de comunicação " + destinatarioModeloComunicacao.getModeloComunicacao().getTipoComunicacao().getDescricao() + " não admite pedido de prorrogação de prazo");
	}
	
	public void finalizarAnalisePedido(Processo comunicacao) throws DAOException{
		TaskInstanceHome.instance().end(TaskInstanceHome.instance().getName());
	}
	
	public Date getDataPedidoProrrogacao(Processo comunicacao){
    	MetadadoProcesso metadadoProcesso = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_PEDIDO_PRORROGACAO);
    	if(metadadoProcesso != null) {
    		return metadadoProcesso.getValue();
    	}
    	return null;
    }
    
    public boolean hasPedidoProrrogacaoEmAberto(Processo comunicacao){
    	return getDataPedidoProrrogacao(comunicacao) != null && getDataAnaliseProrrogacao(comunicacao) == null;
    }
    
    public Date getDataAnaliseProrrogacao(Processo comunicacao){
    	MetadadoProcesso metadadoProcesso = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_ANALISE_PRORROGACAO);
    	if(metadadoProcesso != null) {
    		return metadadoProcesso.getValue();
    	}
    	return null;
    }
    
    public boolean isPrazoProrrogado(Processo comunicacao){
    	return  !getDataLimiteCumprimentoInicial(comunicacao).equals(
    					comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO).getValue());
    }
    
    public Date getDataLimiteCumprimentoInicial(Processo comunicacao){
    	return comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO_INICIAL).getValue();
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
	
	public Date contabilizarPrazoCiencia(Processo comunicacao) {
		DestinatarioModeloComunicacao destinatario = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO).getValue();
        Integer qtdDias = destinatario.getModeloComunicacao().getTipoComunicacao().getQuantidadeDiasCiencia();
        Date hoje = new Date();
        return calendarioEventosManager.getPrimeiroDiaUtil(hoje, qtdDias);
    }
    
	public Date contabilizarPrazoCumprimento(Processo comunicacao) {
		MetadadoProcesso metadadoCiencia = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_CIENCIA);
        MetadadoProcesso metadadoPrazoCumprimento = comunicacao.getMetadado(ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO);
        if (metadadoPrazoCumprimento == null || metadadoCiencia == null) {
        	return null;
        }
        Integer diasPrazoCumprimento = metadadoPrazoCumprimento.getValue();
        return calendarioEventosManager.getPrimeiroDiaUtil((Date) metadadoCiencia.getValue(), diasPrazoCumprimento);
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

	protected void adicionarPrazoDeCumprimento(Processo comunicacao, Date dataCiencia)
			throws DAOException {
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(comunicacao);
		MetadadoProcesso metadadoPrazoCumprimento = comunicacao.getMetadado(ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO);
		Integer diasPrazoCumprimento;
		if (metadadoPrazoCumprimento == null){
			diasPrazoCumprimento = -1;
		}else{
			diasPrazoCumprimento = metadadoPrazoCumprimento.getValue();
		}
		if (diasPrazoCumprimento >= 0) {
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
		Date dataLimite = comunicacao.getMetadado(metadadoPrazo).getValue();
		DateTime dataParaCumprimento = new DateTime(dataLimite.getTime());
		if (dataParaCumprimento.isBeforeNow()) {
			processoManager.movimentarProcessoJBPM(comunicacao);
		}
	}
}
