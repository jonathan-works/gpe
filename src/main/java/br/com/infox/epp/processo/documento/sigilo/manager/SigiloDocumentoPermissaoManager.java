package br.com.infox.epp.processo.documento.sigilo.manager;

import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.sigilo.dao.SigiloDocumentoPermissaoDAO;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumentoPermissao;
import br.com.infox.epp.processo.entity.ProcessoEpa;

@Name(SigiloDocumentoPermissaoManager.NAME)
@AutoCreate
@Scope(ScopeType.EVENT)
public class SigiloDocumentoPermissaoManager extends Manager<SigiloDocumentoPermissaoDAO, SigiloDocumentoPermissao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloDocumentoPermissaoManager";

    public boolean possuiPermissao(SigiloDocumento sigiloDocumento,
            UsuarioLogin usuario) {
        return getDao().possuiPermissao(sigiloDocumento, usuario);
    }

    public boolean possuiPermissao(Set<Integer> idsDocumentosSelecionados,
            UsuarioLogin usuario) {
        return getDao().possuiPermissao(idsDocumentosSelecionados, usuario);
    }

    public void inativarPermissoes(SigiloDocumento sigiloDocumento) {
        getDao().inativarPermissoes(sigiloDocumento);
    }

    public List<ProcessoDocumento> getDocumentosPermitidos(
            ProcessoEpa processo, UsuarioLogin usuario) {
        return getDao().getDocumentosPermitidos(processo, usuario);
    }
}
