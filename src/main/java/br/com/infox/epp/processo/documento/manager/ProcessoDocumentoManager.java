package br.com.infox.epp.processo.documento.manager;

import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
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
public class ProcessoDocumentoManager extends GenericManager {

    public static final String NAME = "processoDocumentoManager";
    private static final long serialVersionUID = 1L;
    
    @In private ProcessoDocumentoDAO processoDocumentoDAO;
    
    public Object getModeloDocumentoByIdProcessoDocumento(Integer idProcessoDocumento){
        return processoDocumentoDAO.getModeloDocumentoByIdProcessoDocumento(idProcessoDocumento);
    }

    public ProcessoDocumento createProcessoDocumento(Processo processo, String label, ProcessoDocumentoBin bin, TipoProcessoDocumento tipoProcessoDocumento) throws DAOException {
        ProcessoDocumento doc = new ProcessoDocumento();
    	doc.setProcessoDocumentoBin(bin);
    	doc.setAtivo(Boolean.TRUE);
    	doc.setDataInclusao(new Date());
    	doc.setUsuarioInclusao(Authenticator.getUsuarioLogado());
    	doc.setProcesso(processo);
    	doc.setProcessoDocumento(label);
    	doc.setTipoProcessoDocumento(tipoProcessoDocumento);
    	doc.setNumeroDocumento(getNextNumeracao(tipoProcessoDocumento,processo));
    	return processoDocumentoDAO.persist(doc);
    }

    public Integer getNextNumeracao(TipoProcessoDocumento tipoProcessoDoc, Processo processo) {
        Integer result = null;
        if (tipoProcessoDoc.getNumera() 
                && tipoProcessoDoc.getTipoNumeracao().equals(TipoNumeracaoEnum.S)) {
            Integer next = processoDocumentoDAO.getNextSequencial(processo);
            if (next == null) {
                result = 1;
            } else {
                result = next +1;
            }
        }
        return result;
    }

}
