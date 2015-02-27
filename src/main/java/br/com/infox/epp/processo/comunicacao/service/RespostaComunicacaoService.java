package br.com.infox.epp.processo.comunicacao.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.dao.DocumentoRespostaComunicacaoDAO;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoListener;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.service.ProcessoAnaliseDocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.seam.exception.BusinessException;

@Name(RespostaComunicacaoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class RespostaComunicacaoService implements AssinaturaDocumentoListener {
	public static final String NAME = "respostaComunicacaoService";
	
	@In
	private DocumentoRespostaComunicacaoDAO documentoRespostaComunicacaoDAO;
	@In
	private ProcessoAnaliseDocumentoService processoAnaliseDocumentoService;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	
	public void enviarResposta(Documento resposta) throws DAOException {
		Processo comunicacao = documentoRespostaComunicacaoDAO.getComunicacaoVinculada(resposta);
		if (comunicacao == null) {
			return;
		}
		Processo processoResposta = processoAnaliseDocumentoService.criarProcessoAnaliseDocumentos(comunicacao, resposta);
		EppMetadadoProvider provider = new EppMetadadoProvider();
		MetadadoProcesso metadadoResposta = provider.gerarMetadado(EppMetadadoProvider.DOCUMENTO_EM_ANALISE, processoResposta, resposta.getId().toString());
		processoResposta.getMetadadoProcessoList().add(metadadoResposta);
		metadadoProcessoManager.persist(metadadoResposta);
		
		Map<String, Object> variaveisJbpm = new HashMap<>();
		variaveisJbpm.put("respostaTempestiva", getRespostaTempestiva(resposta, comunicacao));
		variaveisJbpm.put("respostaComunicacao", true);
		processoAnaliseDocumentoService.inicializarFluxoDocumento(processoResposta, variaveisJbpm);
	}

	private boolean getRespostaTempestiva(Documento resposta, Processo comunicacao) {
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
			
			Date dataInclusaoResposta = resposta.getDataInclusao();
			if (dataCiencia.equals(dataInclusaoResposta) || dataFimPrazoDestinatario.equals(dataInclusaoResposta) ||
					(dataCiencia.before(dataInclusaoResposta) && dataFimPrazoDestinatario.after(dataInclusaoResposta))) {
				respostaTempestiva = true;
			}
		}
		return respostaTempestiva;
	}

	@Override
	public void postSignDocument(Documento documento) {
		try {
			enviarResposta(documento);
		} catch (DAOException e) {
			throw new BusinessException("Erro ao enviar resposta da comunicação", e);
		}
	}
}
