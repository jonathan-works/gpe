package br.com.infox.ibpm.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.Papel;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;
import br.com.infox.ibpm.query.TipoProcessoDocumentoQuery;
import br.com.infox.ibpm.type.TipoDocumentoEnum;
import br.com.infox.util.constants.WarningConstants;

@Name(TipoProcessoDocumentoDAO.NAME)
@AutoCreate
public class TipoProcessoDocumentoDAO extends GenericDAO {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoProcessoDocumentoDAO";
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<TipoProcessoDocumento> getTipoProcessoDocumentoInterno(boolean isModelo){
		String restricaoDeTipo = "'";
		if (isModelo){
			restricaoDeTipo += (TipoDocumentoEnum.P).toString();
		} else{
			restricaoDeTipo += (TipoDocumentoEnum.D).toString();
		}
		restricaoDeTipo += "'";
		String hql = "select o from TipoProcessoDocumento o " +
				"where o.ativo = true and (o.visibilidade = 'I' OR o.visibilidade = 'A') and " +
				"(o.inTipoDocumento = " + restricaoDeTipo + " OR o.inTipoDocumento = 'T')";
		return entityManager.createQuery(hql).getResultList();
	}
	
	//Retorna um TipoProcessoDocumento ~aleatório
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public TipoProcessoDocumento getTipoProcessoDocumentoFluxo(){
		String sql = "select o from TipoProcessoDocumento o ";
		Query q = entityManager.createQuery(sql);
		q.setMaxResults(1);
		List<TipoProcessoDocumento> resultList = q.getResultList();
		TipoProcessoDocumento result = null;
		if (resultList.size()>0) {
		    result = resultList.get(0);
		}
		return result;	
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
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
