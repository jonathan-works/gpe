package br.com.infox.epp.processo.documento.component;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.TaskInstance;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.entity.Processo;

@Name(ProcessoDocumentoUploader.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoDocumentoUploader implements FileUploadListener {

    public static final String NAME = "processoDocumentoUploader";
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoDocumentoUploader.class);

    private Processo processo;
    private ProcessoDocumento processoDocumento;
    private List<ProcessoDocumento> processoDocumentosDaSessao;

    @In
    private ProcessoDocumentoManager processoDocumentoManager;
    @In
    private ProcessoDocumentoBinManager processoDocumentoBinManager;

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

    @Override
    public void processFileUpload(FileUploadEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void onClickTabAnexar() {
        setProcessoDocumento(new ProcessoDocumento());
        getProcessoDocumento().setProcessoDocumentoBin(new ProcessoDocumentoBin());
        processoDocumentosDaSessao = new ArrayList<>();
    }

    public void persist() {
        processoDocumento.setProcesso(processo);
        processoDocumento.setNumeroDocumento(processoDocumentoManager.getNextNumeracao(processoDocumento));
        try {
            processoDocumento.setProcessoDocumentoBin(
                    processoDocumentoBinManager.createProcessoDocumentoBin(processoDocumento));
        } catch (DAOException e) {
            LOG.error("Não foi possível gravar o binário do documento do processo " + processoDocumento, e);
        }
        processoDocumento.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        setJbpmTask();
        try {
            processoDocumentoManager.persist(processoDocumento);
        } catch (DAOException e) {
            LOG.error("Não foi possível gravar o documento do processo " + processoDocumento, e);
        }
        processoDocumentosDaSessao.add(processoDocumento);
        processoDocumento = new ProcessoDocumento();
    }
    
    private void setJbpmTask() {
        if (TaskInstance.instance() != null) {
            long idJbpmTask = TaskInstance.instance().getId();
            processoDocumento.setIdJbpmTask(idJbpmTask);
        }
    }
}
