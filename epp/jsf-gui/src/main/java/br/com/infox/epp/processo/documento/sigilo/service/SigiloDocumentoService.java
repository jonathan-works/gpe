package br.com.infox.epp.processo.documento.sigilo.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoManager;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoPermissaoManager;

@Named
@ViewScoped
public class SigiloDocumentoService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SigiloDocumentoPermissaoManager sigiloDocumentoPermissaoManager;
    @Inject
    private SigiloDocumentoManager sigiloDocumentoManager;

    public boolean possuiPermissao(Documento documento,
            UsuarioLogin usuario) {
        SigiloDocumento sigiloDocumento = sigiloDocumentoManager.getSigiloDocumentoAtivo(documento);
        if (sigiloDocumento != null) {
            return sigiloDocumentoPermissaoManager.possuiPermissao(sigiloDocumento, usuario);
        }
        return true;
    }

    @Transactional
    public void gravarSigiloDocumento(SigiloDocumento sigiloDocumento) throws DAOException {
        SigiloDocumento sigiloDocumentoAtivo = sigiloDocumentoManager.getSigiloDocumentoAtivo(sigiloDocumento.getDocumento());
        if (sigiloDocumentoAtivo != null) {
            sigiloDocumentoPermissaoManager.inativarPermissoes(sigiloDocumentoAtivo);
        }
        sigiloDocumentoManager.persist(sigiloDocumento);
    }
}
