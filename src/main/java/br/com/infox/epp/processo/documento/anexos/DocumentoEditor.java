package br.com.infox.epp.processo.documento.anexos;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;

@Name(DocumentoEditor.NAME)
@Scope(ScopeType.CONVERSATION)
public class DocumentoEditor extends DocumentoCreator {

    public static final String NAME = "documentoEditor";
    private static final LogProvider LOG = Logging.getLogProvider(DocumentoEditor.class);

    private List<ProcessoDocumento> processoDocumentosDaSessao;

    @In
    private ProcessoDocumentoManager processoDocumentoManager;

    public List<ProcessoDocumento> getProcessoDocumentosDaSessao() {
        return processoDocumentosDaSessao;
    }

    public void setProcessoDocumentosDaSessao(
            List<ProcessoDocumento> processoDocumentosDaSessao) {
        this.processoDocumentosDaSessao = processoDocumentosDaSessao;
    }
    
    public void persist() {
        try {
            processoDocumentosDaSessao.add(processoDocumentoManager.gravarDocumentoNoProcesso(getProcesso(), getProcessoDocumento()));
        } catch (DAOException e) {
            LOG.error("Não foi possível gravar o documento do processo " + getProcessoDocumento(), e);
        }
        newInstance();
    }

    @Override
    public void clear() {
        processoDocumentosDaSessao = new ArrayList<>();
        newInstance();
    }
    
}
