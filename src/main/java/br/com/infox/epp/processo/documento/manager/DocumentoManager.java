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
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.processo.documento.dao.DocumentoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
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

    public String getModeloDocumentoByIdDocumento(Integer idDocumento) {
        return getDao().getModeloDocumentoByIdDocumento(idDocumento);
    }

    public String valorDocumento(Integer idDocumento) {
        return find(idDocumento).getDocumentoBin().getModeloDocumento();
    }
    
    public void exclusaoRestauracaoLogicaDocumento(Documento documento, String motivo, 
    		TipoAlteracaoDocumento tipoAlteracaoDocumento) throws DAOException{
    	historicoStatusDocumentoManager.gravarHistoricoDocumento(motivo, tipoAlteracaoDocumento, documento);
    	if (tipoAlteracaoDocumento == TipoAlteracaoDocumento.E) {
    		documento.setExcluido(true);
    	} else if (tipoAlteracaoDocumento == TipoAlteracaoDocumento.R) {
    		documento.setExcluido(false);
    	}
		update(documento);
    }
    
    public Documento gravarDocumentoNoProcesso(Processo processo,
            Documento documento) throws DAOException {
        documento.setProcesso(processo);
        documento.setNumeroDocumento(getNextNumeracao(documento));
        documento.setDocumentoBin(documentoBinManager.createProcessoDocumentoBin(documento));
        documento.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        if (TaskInstance.instance() != null) {
            long idJbpmTask = TaskInstance.instance().getId();
            documento.setIdJbpmTask(idJbpmTask);
        }
        persist(documento);
        return documento;
    }

    public Documento createDocumento(Processo processo,
            String label, DocumentoBin bin,
            ClassificacaoDocumento classificacaoDocumento) throws DAOException {
        Documento doc = new Documento();
        doc.setDocumentoBin(bin);
        doc.setDataInclusao(new Date());
        doc.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        doc.setProcesso(processo);
        doc.setDescricao(label);
        doc.setExcluido(false);
        doc.setClassificacaoDocumento(classificacaoDocumento);
        doc.setNumeroDocumento(getNextNumeracao(classificacaoDocumento, processo));
        return getDao().persist(doc);
    }
    
    public List<Documento> getDocumentoByTask(org.jbpm.taskmgmt.exe.TaskInstance task) {
        return getDao().getDocumentoListByTask(task);
    }

    public Integer getNextNumeracao(ClassificacaoDocumento tipoProcessoDoc,
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

    public Integer getNextNumeracao(Documento documento) {
        return getNextNumeracao(documento.getClassificacaoDocumento(), documento.getProcesso());
    }

    public List<Documento> getAnexosPublicos(long idJbpmTask) {
        return getDao().getAnexosPublicos(idJbpmTask);
    }
    
    public List<Documento> getListProcessoDocumento(Processo processo){
    	return getDao().getListProcessoDocumentoByProcesso(processo);
    }
}
