package br.com.infox.epp.assinador;

import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;

public interface ValidadorUsuarioCertificado {
    public void verificaCertificadoUsuarioLogado(String certChainBase64Encoded, PessoaFisica pessoaFisica) throws AssinaturaException;

}
