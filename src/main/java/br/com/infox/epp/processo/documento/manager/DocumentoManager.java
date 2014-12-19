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
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.processo.documento.dao.DocumentoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.type.TipoAlteracaoDocumento;
import br.com.infox.epp.processo.entity.Processo;

@AutoCreate
@Name(DocumentoManager.NAME)
public class DocumentoManager extends Manager<DocumentoDAO, Documento> {

    public static final String NAME = "documentoManager";
    private static final long serialVersionUID = 1L;

    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private HistoricoStatusDocumentoManager historicoStatusDocumentoManager;
    @In
    private PastaManager pastaManager;
    @In
    private ClassificacaoDocumentoManager classificacaoDocumentoManager;

    public String getModeloDocumentoByIdDocumento(final Integer idDocumento) {
        return getDao().getModeloDocumentoByIdDocumento(idDocumento);
    }

    public String valorDocumento(final Integer idDocumento) {
        return find(idDocumento).getDocumentoBin().getModeloDocumento();
    }

    public void exclusaoRestauracaoLogicaDocumento(final Documento documento,
            final String motivo,
            final TipoAlteracaoDocumento tipoAlteracaoDocumento)
            throws DAOException {
        this.historicoStatusDocumentoManager.gravarHistoricoDocumento(motivo,
                tipoAlteracaoDocumento, documento);
        if (tipoAlteracaoDocumento == TipoAlteracaoDocumento.E) {
            documento.setExcluido(true);
        } else if (tipoAlteracaoDocumento == TipoAlteracaoDocumento.R) {
            documento.setExcluido(false);
        }
        update(documento);
    }

    public Documento gravarDocumentoNoProcesso(final Processo processo,
            final Documento documento) throws DAOException {
        documento.setProcesso(processo);
        documento.setNumeroDocumento(getNextNumeracao(documento));
        documento.setDocumentoBin(this.documentoBinManager
                .createProcessoDocumentoBin(documento));
        documento.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        if (TaskInstance.instance() != null) {
            final long idJbpmTask = TaskInstance.instance().getId();
            documento.setIdJbpmTask(idJbpmTask);
        }
        final Pasta padrao = this.pastaManager.getDefaultFolder(processo);
        documento.setPasta(padrao);

        persist(documento);
        return documento;
    }

    public Documento createDocumento(final Processo processo,
            final String label, final DocumentoBin bin,
            final ClassificacaoDocumento classificacaoDocumento)
            throws DAOException {
        final Documento doc = new Documento();
        doc.setDocumentoBin(bin);
        doc.setDataInclusao(new Date());
        doc.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        doc.setProcesso(processo);
        doc.setDescricao(label);
        doc.setExcluido(Boolean.FALSE);
        // TODO adicionar a classificação de documento na lista de classes
        // gerenciadas antes de entrar aqui
        this.classificacaoDocumentoManager.refresh(classificacaoDocumento);
        doc.setClassificacaoDocumento(classificacaoDocumento);
        doc.setNumeroDocumento(getNextNumeracao(classificacaoDocumento,
                processo));
        return getDao().persist(doc);
    }

    public List<Documento> getDocumentoByTask(
            final org.jbpm.taskmgmt.exe.TaskInstance task) {
        return getDao().getDocumentoListByTask(task);
    }

    public Integer getNextNumeracao(
            final ClassificacaoDocumento tipoProcessoDoc,
            final Processo processo) {
        Integer result = null;
        if (TipoNumeracaoEnum.S.equals(tipoProcessoDoc.getTipoNumeracao())) {
            final Integer next = getDao().getNextSequencial(processo);
            if (next == null) {
                result = 1;
            } else {
                result = next + 1;
            }
        }
        return result;
    }

    public Integer getNextNumeracao(final Documento documento) {
        return getNextNumeracao(documento.getClassificacaoDocumento(),
                documento.getProcesso());
    }

    public List<Documento> getAnexosPublicos(final long idJbpmTask) {
        return getDao().getAnexosPublicos(idJbpmTask);
    }

    public List<Documento> getListDocumentoByProcesso(final Processo processo) {
        return getDao().getListDocumentoByProcesso(processo);
    }
    
    public int getTotalDocumentosProcesso(Processo processo) {
    	return getDao().getTotalDocumentosProcesso(processo);
    }
}
