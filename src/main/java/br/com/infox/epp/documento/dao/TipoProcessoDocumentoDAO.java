package br.com.infox.epp.documento.dao;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;
import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_INTERNO_ANEXO;
import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_INTERNO_TEXTO;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery;

@Name(TipoProcessoDocumentoDAO.NAME)
@AutoCreate
public class TipoProcessoDocumentoDAO extends GenericDAO {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoProcessoDocumentoDAO";
	
    public List<TipoProcessoDocumento> getTipoProcessoDocumentoInterno(boolean isModelo) {
        if (isModelo) {
            return getNamedResultList(TIPO_PROCESSO_DOCUMENTO_INTERNO_TEXTO);
        } else {
            return getNamedResultList(TIPO_PROCESSO_DOCUMENTO_INTERNO_ANEXO);
        }
    }
	
	//Retorna um TipoProcessoDocumento ~aleatório
	@SuppressWarnings(UNCHECKED)
	public TipoProcessoDocumento getTipoProcessoDocumentoFluxo(){
		String sql = "select o from TipoProcessoDocumento o ";
		Query q = getEntityManager().createQuery(sql);
		q.setMaxResults(1);
		List<TipoProcessoDocumento> resultList = q.getResultList();
		TipoProcessoDocumento result = null;
		if (resultList.size()>0) {
		    result = resultList.get(0);
		}
		return result;	
	}
	
	@SuppressWarnings(UNCHECKED)
    public boolean isAssinaturaObrigatoria(TipoProcessoDocumento tipoProcessoDocumento, Papel papel) {
	    HashMap<String,Object> params = new HashMap<String,Object>(0);
	    params.put(TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_PARAM, tipoProcessoDocumento);
	    params.put(TipoProcessoDocumentoQuery.PAPEL_PARAM, papel);
	    
        List<Boolean> list = getNamedQuery(TipoProcessoDocumentoQuery.ASSINATURA_OBRIGATORIA, params)
                .setMaxResults(1)
                .getResultList();
        Boolean result = false;
        if (list != null && list.size() > 0) {
            result = list.get(0);
        }
        return result;
	}

}
