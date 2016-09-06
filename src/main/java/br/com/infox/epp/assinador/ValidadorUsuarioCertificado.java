package br.com.infox.epp.assinador;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;

public interface ValidadorUsuarioCertificado {
    public void verificaCertificadoUsuarioLogado(String certChainBase64Encoded, UsuarioLogin usuarioLogado) throws AssinaturaException;

}
