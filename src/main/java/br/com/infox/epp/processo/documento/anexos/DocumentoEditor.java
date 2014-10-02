package br.com.infox.epp.processo.documento.anexos;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;

@Name(DocumentoEditor.NAME)
@Scope(ScopeType.CONVERSATION)
public class DocumentoEditor extends DocumentoCreator {

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
        return documentoManager.gravarDocumentoNoProcesso(getProcesso(), getProcessoDocumento());
    }

}
