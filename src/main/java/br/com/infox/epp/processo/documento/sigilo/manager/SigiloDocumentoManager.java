package br.com.infox.epp.processo.documento.sigilo.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.sigilo.dao.SigiloDocumentoDAO;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;

@Name(SigiloDocumentoManager.NAME)
@AutoCreate
@Scope(ScopeType.EVENT)
public class SigiloDocumentoManager extends Manager<SigiloDocumentoDAO, SigiloDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloDocumentoManager";

    public SigiloDocumento getSigiloDocumentoAtivo(ProcessoDocumento documento) {
        return getDao().getSigiloDocumentoAtivo(documento);
    }

    public SigiloDocumento getSigiloDocumentoAtivo(Integer idDocumento) {
        return getDao().getSigiloDocumentoAtivo(idDocumento);
    }

    public boolean isSigiloso(Integer idDocumento) {
        return getDao().isSigiloso(idDocumento);
    }

    @Override
    public SigiloDocumento persist(SigiloDocumento o) throws DAOException {
        getDao().inativarSigilos(o.getDocumento());
        return super.persist(o);
    }
}
