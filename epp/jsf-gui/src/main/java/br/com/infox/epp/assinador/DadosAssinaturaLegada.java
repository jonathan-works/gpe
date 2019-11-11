package br.com.infox.epp.assinador;

import java.security.cert.X509Certificate;
import java.util.List;

public class DadosAssinaturaLegada {
	
	private List<X509Certificate> certChain; 
	private String certChainBase64;
	private String signature;
	
	public DadosAssinaturaLegada(List<X509Certificate> certChain, String certChainBase64,  String signature) {
		super();
		this.certChainBase64 = certChainBase64;
		this.certChain = certChain;
		this.signature = signature;
	}
	
	public String getCertChainBase64() {
		return certChainBase64;
	}
	public String getSignature() {
		return signature;
	}

	public List<X509Certificate> getCertChain() {
		return certChain;
	}
}
