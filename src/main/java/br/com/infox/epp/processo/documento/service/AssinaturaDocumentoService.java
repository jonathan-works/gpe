package br.com.infox.epp.processo.documento.service;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.Strings;

import br.com.infox.certificado.Certificado;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.AssinaturaException;
import br.com.infox.epp.processo.documento.AssinaturaException.Motivo;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;

@Name(AssinaturaDocumentoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class AssinaturaDocumentoService implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "assinaturaDocumentoService";
    
    @In
    private ProcessoDocumentoManager processoDocumentoManager;

    public Boolean isDocumentoAssinado(ProcessoDocumento processoDocumento) {
        return !Strings.isEmpty(processoDocumento.getProcessoDocumentoBin().getCertChain())
                && !Strings.isEmpty(processoDocumento.getProcessoDocumentoBin().getSignature());
    }

    public Boolean isDocumentoAssinado(Integer idDoc) {
        ProcessoDocumento processoDocumento = processoDocumentoManager.find(idDoc);
        return processoDocumento != null && isDocumentoAssinado(processoDocumento);
    }

    public void verificaCertificadoUsuarioLogado(String certChainBase64Encoded,
            UsuarioLogin usuarioLogado) throws CertificadoException, AssinaturaException {
        if (Strings.isEmpty(certChainBase64Encoded)) {
            throw new AssinaturaException(Motivo.SEM_CERTIFICADO);
        }
        if (usuarioLogado.getPessoaFisica() == null) {
            throw new AssinaturaException(Motivo.USUARIO_SEM_PESSOA_FISICA);
        }
        if (Strings.isEmpty(usuarioLogado.getPessoaFisica().getCertChain())) {
            final Certificado certificado = new Certificado(certChainBase64Encoded);
            final String cpfCertificado = certificado.getCn().split(":")[1];
            if (cpfCertificado.equals(usuarioLogado.getPessoaFisica().getCpf().replace(".", "").replace("-", ""))) {
                usuarioLogado.getPessoaFisica().setCertChain(certChainBase64Encoded);
            } else {
                throw new AssinaturaException(Motivo.CADASTRO_USUARIO_NAO_ASSINADO);
            }
        }
        if (!usuarioLogado.getPessoaFisica().checkCertChain(certChainBase64Encoded)) {
            throw new AssinaturaException(Motivo.CERTIFICADO_USUARIO_DIFERENTE_CADASTRO);
        }
    }
}
