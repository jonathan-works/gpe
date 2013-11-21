package br.com.infox.certificado;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("certificadosCaCheckManagerTeste")
@BypassInterceptors
@Scope(ScopeType.EVENT)
public class CeriticadosCaCheckManagerTeste {
	
	private String certChain;	
	private Certificado certificado;
	private DadosCertificado dadosCertificado;

	public void verificaCertificado() {
		try {
			certificado = new Certificado(certChain);
			dadosCertificado = DadosCertificado.parse(certificado);
			CertificadosCaCheckManager.instance().verificaCertificado(certChain);
			FacesMessages.instance().add(Severity.INFO, "Certificado válido: " + certificado.getCn());
		} catch (Exception e) {
			FacesMessages.instance().add(Severity.ERROR, "Erro ao validar: " + e.getMessage());
			e.printStackTrace();
		} 
	}

	public void setCertChain(String certChain) {
		this.certChain = certChain;
	}

	public String getCertChain() {
		return certChain;
	}

	public DadosCertificado getDadosCertificado() {
		return dadosCertificado;
	}	
	
	public Certificado getCertificado() {
		return certificado;
	}
	
}