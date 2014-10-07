package br.com.infox.epp.processo.documento.sigilo.service;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoManager;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoPermissaoManager;

@Name(SigiloDocumentoService.NAME)
@AutoCreate
@Scope(ScopeType.CONVERSATION)
public class SigiloDocumentoService implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloDocumentoService";

    @In
    private SigiloDocumentoPermissaoManager sigiloDocumentoPermissaoManager;

    @In
    private SigiloDocumentoManager sigiloDocumentoManager;

    public boolean possuiPermissao(Documento documento,
            UsuarioLogin usuario) {
        SigiloDocumento sigiloDocumento = sigiloDocumentoManager.getSigiloDocumentoAtivo(documento);
        if (sigiloDocumento != null) {
            return sigiloDocumentoPermissaoManager.possuiPermissao(sigiloDocumento, usuario);
        }
        return true;
    }

    public void gravarSigiloDocumento(SigiloDocumento sigiloDocumento) throws DAOException {
        SigiloDocumento sigiloDocumentoAtivo = sigiloDocumentoManager.getSigiloDocumentoAtivo(sigiloDocumento.getDocumento());
        if (sigiloDocumentoAtivo != null) {
            sigiloDocumentoPermissaoManager.inativarPermissoes(sigiloDocumentoAtivo);
        }
        sigiloDocumentoManager.persist(sigiloDocumento);
    }
}
