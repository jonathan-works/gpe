package br.com.infox.epp.assinador.view;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.CertificateSignatureGroupService;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.certificado.enums.CertificateSignatureGroupStatus;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

@Named
@ViewScoped
public class AssinadorController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private AssinadorService assinadorService;
	@Inject
	private CertificateSignatureGroupService certificateSignatureGroupService;
	
	private String token;
	
	/*public String getDocumentosParaAssinar() {
		List<SignableDocument> documentsToSign = new ArrayList<>();
		if (documentosSelecionados != null) {
			for (DocumentoPC documento : documentosSelecionados) {
				documentsToSign.add(new DefaultSignableDocumentImpl(documento.getDocumentoBin()));
			}
		}
		SignDocuments multiSign = new SignDocuments(documentsToSign);
		String token = UUID.randomUUID().toString();
		Cache<String, String> documentosAssinatura = CertificadoDigitalMapSingleton.getCache(CertificadoDigitalJNLPServlet.DOCUMENTOS_ASSINATURA);
		documentosAssinatura.put(token, multiSign.getDocumentData());
		return token;
	}*/
	
	public String criarListaDocumentos(List<DocumentoBin> listaDocumentos) {
		CertificateSignatureGroup certificateSignatureGroup = assinadorService.criarListaDocumentos(listaDocumentos);
		this.token = certificateSignatureGroup.getToken();
		return certificateSignatureGroup.getToken();
	}
	
	public boolean isFinalizado() {
		CertificateSignatureGroupStatus status = certificateSignatureGroupService.getStatus(token);
		return status != CertificateSignatureGroupStatus.W;
	}
	
	public boolean isSucesso() {
		CertificateSignatureGroupStatus status = certificateSignatureGroupService.getStatus(token);
		return status == CertificateSignatureGroupStatus.S;
	}
	
}
