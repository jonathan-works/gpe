package br.com.infox.epp.processo.comunicacao.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.dao.DocumentoRespostaComunicacaoDAO;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.service.ProcessoAnaliseDocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.seam.util.ComponentUtil;
import br.com.infox.util.time.DateRange;

@Name(RespostaComunicacaoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@Transactional
@Stateless
public class RespostaComunicacaoService {
	
	public static final String NAME = "respostaComunicacaoService";
	
	@In
	private DocumentoRespostaComunicacaoDAO documentoRespostaComunicacaoDAO;
	@In
	private ProcessoAnaliseDocumentoService processoAnaliseDocumentoService;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	@In
	private PrazoComunicacaoService prazoComunicacaoService;
	@In
	private DocumentoManager documentoManager;
	
	@Inject
	private CalendarioEventosManager calendarioEventosManager;
	
	private AssinaturaDocumentoService assinaturaDocumentoService = ComponentUtil.getComponent(AssinaturaDocumentoService.NAME);
	
	public void enviarResposta(List<Documento> respostas) throws DAOException {
		Processo comunicacao = documentoRespostaComunicacaoDAO.getComunicacaoVinculada(respostas.get(0));
		if (comunicacao == null) {
			return;
		}
		Processo processoResposta = processoAnaliseDocumentoService.criarProcessoAnaliseDocumentos(comunicacao, respostas.toArray(new Documento[respostas.size()]));
				
		Map<String, Object> variaveisJbpm = new HashMap<>();
		processoAnaliseDocumentoService.inicializarFluxoDocumento(processoResposta, variaveisJbpm);
		documentoRespostaComunicacaoDAO.updateDocumentoComoEnviado(respostas);
		
		MetadadoProcesso metadadoDestinatario = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO);
		TipoComunicacao tipoComunicacao = ((DestinatarioModeloComunicacao) metadadoDestinatario.getValue()).getModeloComunicacao().getTipoComunicacao();
		if(prazoComunicacaoService.containsClassificacaoProrrogacaoPrazo(respostas, tipoComunicacao)){
			createMetadadoDataPedidoProrrogacaoPrazo(comunicacao);
		} else {
			setRespostaTempestiva(processoResposta.getDataInicio(), comunicacao);
			adicionarDataResposta(comunicacao, new Date(), Authenticator.getUsuarioLogado());
		}
	}
	
	public void adicionarDataResposta (Processo comunicacao, Date dataResposta, UsuarioLogin usuarioResposta) {
		dataResposta = calendarioEventosManager.getPrimeiroDiaUtil(dataResposta);
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(comunicacao);
		String dateFormatted = new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(dataResposta);
		String idUsuarioCumprimento = usuarioResposta.getIdUsuarioLogin().toString();
		MetadadoProcesso metadadoDataResposta = 
				metadadoProcessoProvider.gerarMetadado(ComunicacaoMetadadoProvider.DATA_RESPOSTA, dateFormatted);
		MetadadoProcesso metadadoResponsavelResposta = 
				metadadoProcessoProvider.gerarMetadado(ComunicacaoMetadadoProvider.RESPONSAVEL_RESPOSTA, idUsuarioCumprimento);
		
		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoDataResposta));
		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoResponsavelResposta));
	}
	
	
	public void enviarProrrogacaoPrazo(Documento documento, Processo comunicacao) throws DAOException {
		documentoManager.gravarDocumentoNoProcesso(comunicacao.getProcessoRoot(), documento);
		enviarPedidoProrrogacaoDocumentoGravado(documento, comunicacao);
	}

	private void enviarPedidoProrrogacaoDocumentoGravado(Documento documento, Processo comunicacao) throws DAOException {
		Processo prorrogacao = processoAnaliseDocumentoService.criarProcessoAnaliseDocumentos(comunicacao, documento);
		processoAnaliseDocumentoService.inicializarFluxoDocumento(prorrogacao, null);
		createMetadadoDataPedidoProrrogacaoPrazo(comunicacao);
	}
	
	public void assinarEnviarProrrogacaoPrazo(Documento documento, Processo comunicacao, CertificateSignatureBean signatureBean, UsuarioPerfil usuarioPerfil) 
			throws DAOException, CertificadoException, AssinaturaException{
		documentoManager.gravarDocumentoNoProcesso(comunicacao.getProcessoRoot(), documento);
		assinaturaDocumentoService.assinarDocumento(documento.getDocumentoBin(), usuarioPerfil, signatureBean.getCertChain(), signatureBean.getSignature());
		enviarPedidoProrrogacaoDocumentoGravado(documento, comunicacao);
	}
	
	private void createMetadadoDataPedidoProrrogacaoPrazo(Processo comunicacao) throws DAOException {
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(comunicacao);
		MetadadoProcesso metadadoDataPedido = metadadoProcessoProvider.gerarMetadado(
				ComunicacaoMetadadoProvider.DATA_PEDIDO_PRORROGACAO, new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(new Date()));
		comunicacao.getMetadadoProcessoList().add(metadadoProcessoManager.persist(metadadoDataPedido));
	}
	
	private void setRespostaTempestiva(Date dataResposta, Processo comunicacao) {
		ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstanceForUpdate(comunicacao.getIdJbpm());
		ContextInstance contextInstance = processInstance.getContextInstance();
		if (contextInstance.getVariable("respostaTempestiva") != null) {
			return;
		}
		MetadadoProcesso metadadoDataCiencia = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_CIENCIA);
		Date dataLimiteCumprimento = prazoComunicacaoService.getPrazoLimiteParaResposta(comunicacao);
		boolean respostaTempestiva = false;
		if (metadadoDataCiencia != null && dataLimiteCumprimento != null) {
			Date dataCiencia = metadadoDataCiencia.getValue();
			respostaTempestiva = isRespostaTempestiva(dataCiencia, dataLimiteCumprimento, dataResposta);
		}
		contextInstance.setVariable("respostaTempestiva", respostaTempestiva);
	}

	boolean isRespostaTempestiva(Date dataCiencia, Date dataLimiteCumprimento, Date dataResposta) {
		return new DateRange(dataCiencia, dataLimiteCumprimento).contains(dataResposta);
	}
}
