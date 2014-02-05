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
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.entity.Processo;

@Name(ProcessoDocumentoEditor.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoDocumentoEditor {

    public static final String NAME = "processoDocumentoEditor";
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoDocumentoEditor.class);

    private Processo processo;
    private ProcessoDocumento processoDocumento;
    private List<ProcessoDocumento> processoDocumentosDaSessao;

    @In
    private ProcessoDocumentoManager processoDocumentoManager;

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public ProcessoDocumento getProcessoDocumento() {
        return processoDocumento;
    }

    public void setProcessoDocumento(ProcessoDocumento processoDocumento) {
        this.processoDocumento = processoDocumento;
    }

    public List<ProcessoDocumento> getProcessoDocumentosDaSessao() {
        return processoDocumentosDaSessao;
    }

    public void setProcessoDocumentosDaSessao(
            List<ProcessoDocumento> processoDocumentosDaSessao) {
        this.processoDocumentosDaSessao = processoDocumentosDaSessao;
    }

    public void onClickTabAnexar() {
        setProcessoDocumento(new ProcessoDocumento());
        getProcessoDocumento().setProcessoDocumentoBin(new ProcessoDocumentoBin());
        processoDocumentosDaSessao = new ArrayList<>();
    }

    public void persist() {
        try {
            processoDocumentosDaSessao.add(processoDocumentoManager.gravarDocumentoNoProcesso(processo, processoDocumento));
        } catch (DAOException e) {
            LOG.error("Não foi possível gravar o documento do processo " + processoDocumento, e);
        }
        processoDocumento = new ProcessoDocumento();
    }
    
}
