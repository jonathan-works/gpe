package br.com.infox.epp.assinador.assinavel;

public interface AssinavelSource {

	/**
	 * Array de bytes que será utilizado como entrada para assinatura do assinador 
	 */
	public byte[] dataToSign(TipoSignedData tipoHash);
	
}
