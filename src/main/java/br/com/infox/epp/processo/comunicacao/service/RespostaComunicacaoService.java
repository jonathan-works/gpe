package br.com.infox.epp.processo.comunicacao.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.dao.DocumentoRespostaComunicacaoDAO;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.service.ProcessoAnaliseDocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;

@Name(RespostaComunicacaoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@Transactional
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
	
	public void enviarResposta(List<Documento> respostas) throws DAOException {
		Processo comunicacao = documentoRespostaComunicacaoDAO.getComunicacaoVinculada(respostas.get(0));
		if (comunicacao == null) {
			return;
		}
		Processo processoResposta = processoAnaliseDocumentoService.criarProcessoAnaliseDocumentos(comunicacao, respostas.toArray(new Documento[respostas.size()]));
				
		Map<String, Object> variaveisJbpm = new HashMap<>();
		setRespostaTempestiva(processoResposta.getDataInicio(), comunicacao);
		processoAnaliseDocumentoService.inicializarFluxoDocumento(processoResposta, variaveisJbpm);
		documentoRespostaComunicacaoDAO.updateDocumentoComoEnviado(respostas);
		
		MetadadoProcesso metadadoDestinatario = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO);
		TipoComunicacao tipoComunicacao = ((DestinatarioModeloComunicacao) metadadoDestinatario.getValue()).getModeloComunicacao().getTipoComunicacao();
		if(prazoComunicacaoService.containsClassificacaoProrrogacaoPrazo(respostas, tipoComunicacao)){
			createMetadadoDataPedidoProrrogacaoPrazo(comunicacao);
		}		
	}
	
	public void enviarProrrogacaoPrazo(Documento documento, Processo comunicacao) throws DAOException {
		documentoManager.gravarDocumentoNoProcesso(comunicacao.getProcessoRoot(), documento);
		Processo prorrogacao = processoAnaliseDocumentoService.criarProcessoAnaliseDocumentos(comunicacao, documento);
		processoAnaliseDocumentoService.inicializarFluxoDocumento(prorrogacao, null);
		createMetadadoDataPedidoProrrogacaoPrazo(comunicacao);
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
		boolean respostaTempestiva = false;
		MetadadoProcesso metadadoDataCiencia = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_CIENCIA);
		MetadadoProcesso metadadoPrazoDestinatario = comunicacao.getMetadado(ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO);
		if (metadadoDataCiencia != null && metadadoPrazoDestinatario != null) {
			Date dataCiencia = DateUtil.getBeginningOfDay((Date) metadadoDataCiencia.getValue());
			Integer prazoDestinatario = metadadoPrazoDestinatario.getValue();
			Calendar c = Calendar.getInstance();
			c.setTime(dataCiencia);
			c.add(Calendar.DAY_OF_MONTH, prazoDestinatario);
			Date dataFimPrazoDestinatario = DateUtil.getEndOfDay(c.getTime());
			
			if (dataCiencia.equals(DateUtil.getBeginningOfDay(dataResposta)) || dataFimPrazoDestinatario.equals(DateUtil.getEndOfDay(dataResposta)) ||
					(dataCiencia.before(dataResposta) && dataFimPrazoDestinatario.after(dataResposta))) {
				respostaTempestiva = true;
			}
		}
		contextInstance.setVariable("respostaTempestiva", respostaTempestiva);
	}
}
