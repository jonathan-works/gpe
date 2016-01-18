package br.com.infox.epp.processo.documento.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.dao.AssinaturaDocumentoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(AssinaturaDocumentoManager.NAME)
@Stateless
public class AssinaturaDocumentoManager extends Manager<AssinaturaDocumentoDAO, AssinaturaDocumento> {

    public static final String NAME = "assinaturaDocumentoManager";
    private static final long serialVersionUID = 1L;

    public List<AssinaturaDocumento> listAssinaturaDocumentoByDocumento(Documento documento) {
        return getDao().listAssinaturaDocumentoByDocumento(documento);
    }

}
