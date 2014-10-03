package br.com.infox.epp.processo.documento.anexos;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;

@Scope(ScopeType.CONVERSATION)
@Name(DocumentoEditor.NAME)
public class DocumentoEditor extends DocumentoCreator implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoEditor";
    private static final LogProvider LOG = Logging.getLogProvider(DocumentoEditor.class);

    @In
    private DocumentoManager documentoManager;

    @Override
    protected LogProvider getLogger() {
        return LOG;
    }

    @Override
    protected Documento gravarDocumento() throws DAOException {
        return documentoManager.gravarDocumentoNoProcesso(getProcesso(), getDocumento());
    }

}
