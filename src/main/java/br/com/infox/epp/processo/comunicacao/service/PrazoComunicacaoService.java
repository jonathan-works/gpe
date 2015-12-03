package br.com.infox.epp.processo.comunicacao.service;

import static br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.calendario.TipoEvento;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.cliente.entity.CalendarioEventos;
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
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
import br.com.infox.seam.util.ComponentUtil;
import br.com.infox.util.time.DateRange;

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
	
	private AssinaturaDocumentoService assinaturaDocumentoService = ComponentUtil.getComponent(AssinaturaDocumentoService.NAME);

	public Date contabilizarPrazoCiencia(Processo comunicacao) {
		DestinatarioModeloComunicacao destinatario = getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.DESTINATARIO);
        Integer qtdDias = destinatario.getModeloComunicacao().getTipoComunicacao().getQuantidadeDiasCiencia();
        Date hoje = new Date();
        //O início do prazo de ciência começa no dia do envio. 66741
        return calendarioEventosManager.getPrimeiroDiaUtil(hoje, qtdDias);
    }
    
	public Date contabilizarPrazoCumprimento(Processo comunicacao) {
		return calcularPrazoDeCumprimento(comunicacao);
    }
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void darCiencia(Processo comunicacao, Date dataCiencia, UsuarioLogin usuarioCiencia) throws DAOException {
		//Se o usuário confirmar ciência em dia não útil, o sistema deverá considerar que a ciência foi confirmada no dia útil seguinte e começar a contar o prazo no dia útil 
    	//seguinte a essa confirmação. 64236
		dataCiencia = calendarioEventosManager.getPrimeiroDiaUtil(dataCiencia);
		if (comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_CIENCIA) != null) {
    		return;
    	}
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
		if (comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_CIENCIA) != null) {
    		return;
    	}
		gravarDocumentoCiencia(comunicacao, documentoCiencia);
		darCienciaDocumentoGravado(comunicacao, dataCiencia, Authenticator.getUsuarioLogado());
	}

	private void gravarDocumentoCiencia(Processo comunicacao, Documento documentoCiencia) throws DAOException {
		documentoManager.gravarDocumentoNoProcesso(comunicacao.getProcessoRoot(), documentoCiencia);
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(comunicacao);
		MetadadoProcesso metadadoCiencia = metadadoProcessoProvider.gerarMetadado(
				ComunicacaoMetadadoProvider.DOCUMENTO_COMPROVACAO_CIENCIA, documentoCiencia.getId().toString());
		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoCiencia));
	}
	
	private void darCienciaDocumentoGravado(Processo comunicacao, Date dataCiencia, UsuarioLogin usuarioLogin) throws DAOException {
		darCiencia(comunicacao, dataCiencia, usuarioLogin);
		movimentarTarefaService.finalizarTarefasEmAberto(comunicacao);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void darCienciaManualAssinar(Processo comunicacao, Date dataCiencia, Documento documentoCiencia, CertificateSignatureBean signatureBean, UsuarioPerfil usuarioPerfil) 
			throws DAOException, CertificadoException, AssinaturaException{
		if (comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_CIENCIA) != null) {
    		return;
    	}
		gravarDocumentoCiencia(comunicacao, documentoCiencia);
		assinaturaDocumentoService.assinarDocumento(documentoCiencia.getDocumentoBin(), usuarioPerfil, signatureBean.getCertChain(), signatureBean.getSignature());
		darCienciaDocumentoGravado(comunicacao, dataCiencia, usuarioPerfil.getUsuarioLogin());
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
	public void darCumprimento(Processo comunicacao, Date dataCumprimento) throws DAOException {
		dataCumprimento = calendarioEventosManager.getPrimeiroDiaUtil(dataCumprimento);
		if (comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_CUMPRIMENTO) != null) {
    		return;
    	}
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(comunicacao);
		String dateFormatted = new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(dataCumprimento);
		MetadadoProcesso metadadoDataCumprimento = 
				metadadoProcessoProvider.gerarMetadado(ComunicacaoMetadadoProvider.DATA_CUMPRIMENTO, dateFormatted);
		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoDataCumprimento));
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
				movimentarTarefaService.finalizarTarefasEmAberto(comunicacao);
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

    private LocalDate getNextWeekday(LocalDate date){
        LocalDate result=date;
        while(DateTimeConstants.SATURDAY==result.getDayOfWeek() || DateTimeConstants.SUNDAY==result.getDayOfWeek()){
            result = result.plusDays(1);
        }
        return result;
    }
    
    
    public List<DateRange> getSuspensoesPrazo(DateRange periodo){
        List<DateRange> resultList = new ArrayList<>();
        for (CalendarioEventos calendarioEventos : calendarioEventosManager.getByDate(periodo)) {
            if (TipoEvento.S.equals(calendarioEventos.getTipoEvento())){
                resultList.add(new DateRange(calendarioEventos.getDataInicio(), calendarioEventos.getDataFim()));
            }
        }
        return resultList;
    }
    
    //O inicio de prazo de resposta sempre se dá em dia útil e sempre no dia útil seguinte ao da ciência (tenha esta sido manual ou pelo sistema).
    //O fim de prazo de resposta sempre se dá em dia útil. Se o fim de prazo ocorrer em dia não útil, deverá ser contabilizado como fim de prazo o dia útil seguinte. 64136
    public DateRange calcularPeriodoComSuspensaoDePrazo(Date date, int prazo){
        LocalDate inicioPeriodo = getNextWeekday(LocalDate.fromDateFields(date).plusDays(1));
        LocalDate fimPeriodo = getNextWeekday(inicioPeriodo.plusDays(prazo));
        DateRange periodo = new DateRange(inicioPeriodo, fimPeriodo);
        Set<DateRange> connections = new HashSet<>();
        boolean changed = false;
        do {
            changed = false;
            for (DateRange suspensaoPrazo : getSuspensoesPrazo(periodo)) {
                if (!connections.contains(suspensaoPrazo)) {
                    DateRange connection = periodo.connection(suspensaoPrazo);
                    if (periodo.intersects(connection)) {
                        periodo = periodo.incrementStartByDuration(connection);
                        periodo.setEnd(periodo.getEnd().nextWeekday().toEndOfDay());
                        changed = true;
                        connections.add(suspensaoPrazo);
                    } else if (periodo.abuts(connection)) {
                        periodo = periodo.union(connection);
                        periodo.setEnd(periodo.getEnd().nextWeekday().toEndOfDay());
                        changed = true;
                        connections.add(suspensaoPrazo);
                    }
                }
            }
        } while (changed);
        return periodo;
    }
    
    private Date calcularPrazoDeCumprimento(Processo comunicacao){
        Date dataCiencia = comunicacao.getMetadado(DATA_CIENCIA).<Date>getValue();
        Integer diasPrazoCumprimento = getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO);
        if (diasPrazoCumprimento == null){
            diasPrazoCumprimento = -1;
        }
        if (diasPrazoCumprimento>=0 && dataCiencia != null){
            return calcularPeriodoComSuspensaoDePrazo(dataCiencia, diasPrazoCumprimento).getEnd().toEndOfDay();
        }
        return null;
    }

    protected void atualizarMetadado(MetadadoProcesso metadado, String valor){
        if (!Objects.equals(metadado.getValor(), valor)){
            metadado.setValor(valor);
            metadadoProcessoManager.update(metadado);
        }
    }

    public void atualizarPrazoCiencia(Processo processo) {
        MetadadoProcesso metadado = processo.getMetadado(LIMITE_DATA_CIENCIA);
        String novoLimiteDataCiencia = new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(contabilizarPrazoCiencia(processo));
        atualizarMetadado(metadado, novoLimiteDataCiencia);
    }

    public void atualizarPrazoDeCumprimento(Processo processo) {
        MetadadoProcesso metaLimiteDtCumprimento = processo.getMetadado(LIMITE_DATA_CUMPRIMENTO);
        String novaDataCumprimento = new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(calcularPrazoDeCumprimento(processo));
        atualizarMetadado(metaLimiteDtCumprimento, novaDataCumprimento);
    }
}
