package br.com.infox.ibpm.manager;

import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.ibpm.dao.ProcessoDocumentoDAO;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.ProcessoDocumentoBin;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;
import br.com.infox.ibpm.home.Authenticator;
import br.com.infox.ibpm.type.TipoNumeracaoEnum;

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
            final List<Integer> list = processoDocumentoDAO.getNextSequencial(processo);
            if (list == null || list.size() == 0 || list.get(0)==null) {
                result = 1;
            } else {
                result = list.get(0)+1;
            }
        }
        return result;
    }

}
