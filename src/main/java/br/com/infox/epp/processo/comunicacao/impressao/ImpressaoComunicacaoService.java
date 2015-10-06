package br.com.infox.epp.processo.comunicacao.impressao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.anexos.DocumentoDownloader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.seam.util.ComponentUtil;

@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(ImpressaoComunicacaoService.NAME)
@Transactional
public class ImpressaoComunicacaoService implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "impressaoComunicacaoService";
	
	@In
	private ComunicacaoService comunicacaoService;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	@In
	private DocumentoDownloader documentoDownloader;
	
	ModeloComunicacaoManager modeloComunicacaoManager = ComponentUtil.getComponent(ModeloComunicacaoManager.NAME);
	
	public MeioExpedicao getMeioExpedicao(Processo processo) {
		MetadadoProcesso metadadoProcesso = processo.getMetadado(ComunicacaoMetadadoProvider.MEIO_EXPEDICAO);
		if (metadadoProcesso != null) {
			return metadadoProcesso.getValue();
		}
		return null;
	}
	
	public Date getDataAssinatura(Processo processo) {
		List<AssinaturaDocumento> assinaturas = modeloComunicacaoManager.listAssinaturasComunicacao(processo);
		if (assinaturas != null && !assinaturas.isEmpty() && assinaturas.get(0) != null) {
			return assinaturas.get(0).getDataAssinatura();
		}
		return null;
	}
	
	public Boolean getImpresso(Processo processo) {
		MetadadoProcesso metadadoProcesso = processo.getMetadado(ComunicacaoMetadadoProvider.IMPRESSA);
		if (metadadoProcesso != null) {
			return metadadoProcesso.getValue();
		}
		return Boolean.FALSE;
	}
	
	public TipoComunicacao getTipoComunicacao(Processo processo) {
		MetadadoProcesso metadadoProcesso = processo.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO);
		if (metadadoProcesso != null) {
			DestinatarioModeloComunicacao destinatarioModeloComunicacao = metadadoProcesso.getValue();
			return destinatarioModeloComunicacao.getModeloComunicacao().getTipoComunicacao();
		}
		return null;
	}
	
	public void downloadComunicacao(Processo processo, boolean impressaoCompleta) throws DAOException {
		MetadadoProcesso metadadoProcesso = processo.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO);
		DestinatarioModeloComunicacao destinatarioModeloComunicacao = metadadoProcesso.getValue();
		ModeloComunicacao modeloComunicacao = destinatarioModeloComunicacao.getModeloComunicacao();
		byte[] pdf = null;
		if (impressaoCompleta) {
			pdf = comunicacaoService.gerarPdfCompleto(modeloComunicacao, destinatarioModeloComunicacao);
		} else {
			pdf = comunicacaoService.gerarPdfComunicacao(modeloComunicacao, destinatarioModeloComunicacao);
		}
		documentoDownloader.downloadPdf(destinatarioModeloComunicacao.getDocumentoComunicacao(), pdf, "Comunicação.pdf");
	}
	
	public void marcarComunicacaoComoImpressa(Processo processo) throws DAOException {
		MetadadoProcesso metadadoProcesso = processo.getMetadado(ComunicacaoMetadadoProvider.IMPRESSA);
		if (metadadoProcesso == null ) {
			MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processo);
			metadadoProcesso = metadadoProcessoProvider.gerarMetadado(ComunicacaoMetadadoProvider.IMPRESSA, Boolean.TRUE.toString());
			metadadoProcessoManager.persist(metadadoProcesso);
			processo.getMetadadoProcessoList().add(metadadoProcesso);
		} else {
			metadadoProcesso.setValor(Boolean.TRUE.toString());
			metadadoProcessoManager.update(metadadoProcesso);
		}
	}
	
	public void desmarcarComunicacaoComoImpressa(Processo processo) throws DAOException {
		MetadadoProcesso metadadoProcesso = processo.getMetadado(ComunicacaoMetadadoProvider.IMPRESSA);
		metadadoProcesso.setValor(Boolean.FALSE.toString());
		metadadoProcessoManager.update(metadadoProcesso);
	}

}
