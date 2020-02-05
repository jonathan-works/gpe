package br.com.infox.epp.assinador.assinavel;

import br.com.infox.epp.documento.type.TipoMeioAssinaturaEnum;

public interface AssinavelSource {
    public TipoMeioAssinaturaEnum getTipoMeioAssinatura();
	/**
	 * Array de bytes que será utilizado como entrada para assinatura do assinador
	 */
	public byte[] dataToSign(TipoSignedData tipoHash);

}
