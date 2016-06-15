package br.com.infox.epp.assinador;

public class DadosAssinaturaLegada {
	
	private String certChain;
	private String signature;
	
	public DadosAssinaturaLegada(String certChain, String signature) {
		super();
		this.certChain = certChain;
		this.signature = signature;
	}
	
	public String getCertChain() {
		return certChain;
	}
	public String getSignature() {
		return signature;
	}
	
		
	
}
