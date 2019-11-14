package br.com.infox.epp.processo.documento.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.dao.AssinaturaDocumentoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;

@Stateless
@AutoCreate
@Name(AssinaturaDocumentoManager.NAME)
public class AssinaturaDocumentoManager extends Manager<AssinaturaDocumentoDAO, AssinaturaDocumento> {

    public static final String NAME = "assinaturaDocumentoManager";
    private static final long serialVersionUID = 1L;

    public List<AssinaturaDocumento> listAssinaturaDocumentoByDocumento(Documento documento) {
        return getDao().listAssinaturaDocumentoByDocumento(documento);
    }

}
