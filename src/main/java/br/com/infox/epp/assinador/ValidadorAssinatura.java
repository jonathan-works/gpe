package br.com.infox.epp.assinador;

import br.com.infox.epp.assinador.assinavel.TipoSignedData;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;

public interface ValidadorAssinatura {
	
	public void validarAssinatura(byte[] signedData, TipoSignedData tipoSignedData, byte[] signature) throws AssinaturaException;

	public void validarAssinatura(byte[] signedData, TipoSignedData tipoSignedData, byte[] signature, PessoaFisica pessoaFisica) throws AssinaturaException;
}
