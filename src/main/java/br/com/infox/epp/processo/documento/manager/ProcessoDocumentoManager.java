package br.com.infox.epp.processo.documento.manager;

import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.TaskInstance;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.processo.documento.dao.ProcessoDocumentoDAO;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.entity.Processo;

@Name(ProcessoDocumentoManager.NAME)
@AutoCreate
public class ProcessoDocumentoManager extends Manager<ProcessoDocumentoDAO, ProcessoDocumento> {

    public static final String NAME = "processoDocumentoManager";
    private static final long serialVersionUID = 1L;
    @In
    private ProcessoDocumentoBinManager processoDocumentoBinManager;

    public String getModeloDocumentoByIdProcessoDocumento(
            Integer idProcessoDocumento) {
        return getDao().getModeloDocumentoByIdProcessoDocumento(idProcessoDocumento);
    }

    public String valorProcessoDocumento(Integer idProcessoDocumento) {
        return find(idProcessoDocumento).getProcessoDocumentoBin().getModeloDocumento();
    }

    public ProcessoDocumento gravarDocumentoNoProcesso(Processo processo,
            ProcessoDocumento processoDocumento) throws DAOException {
        processoDocumento.setProcesso(processo);
        processoDocumento.setNumeroDocumento(getNextNumeracao(processoDocumento));
        processoDocumento.setProcessoDocumentoBin(processoDocumentoBinManager.createProcessoDocumentoBin(processoDocumento));
        processoDocumento.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        if (TaskInstance.instance() != null) {
            long idJbpmTask = TaskInstance.instance().getId();
            processoDocumento.setIdJbpmTask(idJbpmTask);
        }
        persist(processoDocumento);
        return processoDocumento;
    }

    public ProcessoDocumento createProcessoDocumento(Processo processo,
            String label, ProcessoDocumentoBin bin,
            TipoProcessoDocumento tipoProcessoDocumento) throws DAOException {
        ProcessoDocumento doc = new ProcessoDocumento();
        doc.setProcessoDocumentoBin(bin);
        doc.setAtivo(Boolean.TRUE);
        doc.setDataInclusao(new Date());
        doc.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        doc.setProcesso(processo);
        doc.setProcessoDocumento(label);
        doc.setTipoProcessoDocumento(tipoProcessoDocumento);
        doc.setNumeroDocumento(getNextNumeracao(tipoProcessoDocumento, processo));
        return getDao().persist(doc);
    }

    public Integer getNextNumeracao(TipoProcessoDocumento tipoProcessoDoc,
            Processo processo) {
        Integer result = null;
        if (tipoProcessoDoc.getTipoNumeracao().equals(TipoNumeracaoEnum.S)) {
            Integer next = getDao().getNextSequencial(processo);
            if (next == null) {
                result = 1;
            } else {
                result = next + 1;
            }
        }
        return result;
    }

    public Integer getNextNumeracao(ProcessoDocumento processoDocumento) {
        return getNextNumeracao(processoDocumento.getTipoProcessoDocumento(), processoDocumento.getProcesso());
    }

    public List<ProcessoDocumento> getAnexosPublicos(long idJbpmTask) {
        return getDao().getAnexosPublicos(idJbpmTask);
    }
}
