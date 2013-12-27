package br.com.infox.epp.processo.documento.dao;

import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.*;
import static br.com.infox.core.constants.WarningConstants.*;

import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.itx.util.EntityUtil;

@Name(ProcessoDocumentoDAO.NAME)
@AutoCreate
public class ProcessoDocumentoDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoDocumentoDAO";
    
    @SuppressWarnings(UNCHECKED)
    public List<Integer> getNextSequencial(Processo processo) {
         final Query q = EntityUtil.createQuery(NEXT_SEQUENCIAL_QUERY)
                .setParameter(PARAM_PROCESSO, processo)
                .setParameter(PARAM_TIPO_PROCESSO, TipoNumeracaoEnum.S)
                .setMaxResults(1);
        
        return q.getResultList();
    }
    
    public Object getModeloDocumentoByIdProcessoDocumento(Integer idProcessoDocumento){
        ProcessoDocumento processoDocumento = EntityUtil.find(ProcessoDocumento.class, idProcessoDocumento);
        if (processoDocumento != null) {
            return processoDocumento.getProcessoDocumentoBin().getModeloDocumento();
        }
        return null;
    }
}
