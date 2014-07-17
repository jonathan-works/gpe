package br.com.infox.epp.processo.documento.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.dao.AssinaturaDocumentoDAO;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;

@AutoCreate
@Name(AssinaturaDocumentoManager.NAME)
@Scope(ScopeType.EVENT)
public class AssinaturaDocumentoManager extends
        Manager<AssinaturaDocumentoDAO, AssinaturaDocumento> {

    public static final String NAME = "assinaturaDocumentoManager";
    private static final long serialVersionUID = 1L;

    public List<AssinaturaDocumento> listAssinaturaDocumentoByProcessoDocumento(
            ProcessoDocumento processoDocumento) {
        return getDao().listAssinaturaDocumentoByProcessoDocumento(
                processoDocumento);
    }

}
