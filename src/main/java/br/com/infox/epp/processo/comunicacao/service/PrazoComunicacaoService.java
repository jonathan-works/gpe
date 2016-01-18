package br.com.infox.epp.processo.comunicacao.service;

import static br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider.DATA_CIENCIA;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.context.exe.ContextInstance;

import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
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
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.system.Parametros;
import br.com.infox.ibpm.task.service.MovimentarTarefaService;
import br.com.infox.util.time.DateRange;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PrazoComunicacaoService {
	public static final String NAME = "prazoComunicacaoService";
	
	@Inject
	private CalendarioEventosManager calendarioEventosManager;
	@Inject
	private MetadadoProcessoManager metadadoProcessoManager;
	@Inject
	private MovimentarTarefaService movimentarTarefaService;
	@Inject
	private DocumentoManager documentoManager;
	@Inject
	private AssinaturaDocumentoService assinaturaDocumentoService;

	public Date contabilizarPrazoCiencia(Processo comunicacao) {
		DestinatarioModeloComunicacao destinatario = getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.DESTINATARIO);
        Integer qtdDias = destinatario.getModeloComunicacao().getTipoComunicacao().getQuantidadeDiasCiencia();
        //O início do prazo de ciência começa no dia do envio. 66741
        return calcularPrazoCiencia(new Date(), qtdDias);
    }
    
	public Date calcularPrazoCiencia(Date dataInicio, Integer qtdDias){
		return calendarioEventosManager.getPrimeiroDiaUtil(dataInicio, qtdDias);
	}
	
	public Date contabilizarPrazoCumprimento(Processo comunicacao) {
		Date dataCiencia = comunicacao.getMetadado(DATA_CIENCIA).getValue();
        Integer diasPrazoCumprimento = getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO);
        if (diasPrazoCumprimento == null){
            diasPrazoCumprimento = -1;
        }
        if (diasPrazoCumprimento>=0 && dataCiencia != null){
        	return calcularPrazoDeCumprimento(dataCiencia, diasPrazoCumprimento);
        }
        return null;
    }

	public Date calcularPrazoDeCumprimento(Date dataCiencia, Integer diasPrazoCumprimento) {
		br.com.infox.util.time.Date inicio = new br.com.infox.util.time.Date(dataCiencia).plusDays(1);
		DateRange periodo = new DateRange(inicio.toDate(), inicio.plusDays(diasPrazoCumprimento -1).toDate());
		periodo = calendarioEventosManager.calcularPrazoIniciandoEmDiaUtil(periodo);
		periodo = calendarioEventosManager.calcularPrazoEncerrandoEmDiaUtil(periodo);
		return periodo.getEnd().withTimeAtEndOfDay().toDate();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void darCiencia(Processo comunicacao, Date dataCiencia, UsuarioLogin usuarioCiencia) throws DAOException {
		//Se o usuário confirmar ciência em dia não útil, o sistema deverá considerar que a ciência foi confirmada no dia útil seguinte e começar a contar o prazo no dia útil 
    	//seguinte a essa confirmação. 64236
		dataCiencia = calendarioEventosManager.getNextWeekday(dataCiencia).toDate();
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
	
	private void adicionarVariavelCienciaAutomaticaAoProcesso(UsuarioLogin usuarioCiencia, Processo comunicacao) {
		Integer idUsuarioSistema = Integer.valueOf(Parametros.ID_USUARIO_SISTEMA.getValue());
		boolean isUsuarioSistema = idUsuarioSistema.equals(usuarioCiencia.getIdUsuarioLogin());
		org.jbpm.graph.exe.ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstanceForUpdate(comunicacao.getIdJbpm());
		ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable(VariaveisJbpmComunicacao.CIENCIA_AUTOMATICA, isUsuarioSistema);
	}
	
    private void adicionarVariavelPossuiPrazoAoProcesso(Processo comunicacao) {
    	MetadadoProcesso metadadoProcesso = comunicacao.getMetadado(ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO);
    	boolean possuiPrazoParaCumprimento = metadadoProcesso != null;
    	org.jbpm.graph.exe.ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstanceForUpdate(comunicacao.getIdJbpm());
		ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable(VariaveisJbpmComunicacao.POSSUI_PRAZO_CUMPRIMENTO, possuiPrazoParaCumprimento);
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
		if (comunicacao.getNaturezaCategoriaFluxo().getFluxo().getCodFluxo().equals(Parametros.CODIGO_FLUXO_COMUNICACAO_ELETRONICA.getValue())) {
			movimentarTarefaService.finalizarTarefasEmAberto(comunicacao);
		}
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

	private void adicionarPrazoDeCumprimento(Processo comunicacao, Date dataCiencia) throws DAOException {
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
	
    public void movimentarComunicacaoPrazoExpirado(Processo comunicacao, MetadadoProcessoDefinition metadadoPrazo) throws DAOException{
		Date dataLimite = getValueMetadado(comunicacao, metadadoPrazo);
		if (dataLimite != null) {
			if (dataLimite.compareTo(new Date())<=0) {
				movimentarTarefaService.finalizarTarefasEmAberto(comunicacao);
			}
		}
	}
    
    public Date getDataLimiteCumprimento(Processo comunicacao){
    	return getValueMetadado(comunicacao, ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO);
    }

	//Prorrogação de Prazo Service
	
    public Boolean isClassificacaoProrrogacaoPrazo(ClassificacaoDocumento classificacaoDocumento, TipoComunicacao tipoComunicacao) {
		return classificacaoDocumento != null && classificacaoDocumento.equals(tipoComunicacao.getClassificacaoDocumento());
	}
	
    public Boolean canRequestProrrogacaoPrazo(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
    	return canTipoComunicacaoRequestProrrogacaoPrazo(destinatarioModeloComunicacao.getModeloComunicacao().getTipoComunicacao())
				&& destinatarioModeloComunicacao.getProcesso().getMetadado(ComunicacaoMetadadoProvider.DATA_CUMPRIMENTO) == null;
	}

    public Boolean containsClassificacaoProrrogacaoPrazo(List<Documento> documentos, TipoComunicacao tipoComunicacao) {
		ClassificacaoDocumento classificacaoProrrogacao = tipoComunicacao.getClassificacaoProrrogacao();
		for (Documento documento : documentos) {
			if (documento.getClassificacaoDocumento().equals(classificacaoProrrogacao)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
    public Boolean canTipoComunicacaoRequestProrrogacaoPrazo(TipoComunicacao tipoComunicacao) {
		return tipoComunicacao.getClassificacaoProrrogacao() != null;
	}
	
    public ClassificacaoDocumento getClassificacaoProrrogacaoPrazo(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
		return destinatarioModeloComunicacao.getModeloComunicacao().getTipoComunicacao().getClassificacaoProrrogacao();
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
    
    private <T> T getValueMetadado(Processo processo, MetadadoProcessoDefinition metaDefinition){
    	MetadadoProcesso metadadoProcesso = processo.getMetadado(metaDefinition);
    	if(metadadoProcesso != null){
    		return metadadoProcesso.getValue();
    	}
    	return null;
    }

}
