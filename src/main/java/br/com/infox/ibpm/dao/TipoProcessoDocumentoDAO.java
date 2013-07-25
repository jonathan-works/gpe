package br.com.infox.ibpm.dao;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import javax.persistence.Query;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;
import br.com.infox.ibpm.type.TipoDocumentoEnum;

@Name(TipoProcessoDocumentoDAO.NAME)
@AutoCreate
public class TipoProcessoDocumentoDAO extends GenericDAO {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoProcessoDocumentoDAO";
	
	@SuppressWarnings("unchecked")
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
	public TipoProcessoDocumento getTipoProcessoDocumentoFluxo(){
		String sql = "select o from TipoProcessoDocumento o ";
		Query q = entityManager.createQuery(sql);
		q.setMaxResults(1);
		return (TipoProcessoDocumento) q.getSingleResult();	
	}

}
