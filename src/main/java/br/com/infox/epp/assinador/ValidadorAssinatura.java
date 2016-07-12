package br.com.infox.epp.assinador;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;

public interface ValidadorAssinatura {
	
	public void validarAssinatura(byte[] signedData, byte[] signature) throws AssinaturaException;

	public void validarAssinatura(byte[] signedData, byte[] signature, UsuarioLogin usuario) throws AssinaturaException;
}
