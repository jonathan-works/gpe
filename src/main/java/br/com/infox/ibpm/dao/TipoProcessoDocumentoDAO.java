package br.com.infox.ibpm.dao;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;

@Name(TipoProcessoDocumentoDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class TipoProcessoDocumentoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoProcessoDocumentoDAO";
	
	//Retorna um TipoProcessoDocumento ~aleatório
	public TipoProcessoDocumento getTipoProcessoDocumentoFluxo(){
		String sql = "select o from TipoProcessoDocumento o ";
		Query q = entityManager.createQuery(sql);
		q.setMaxResults(1);
		return (TipoProcessoDocumento) q.getSingleResult();
	}
}
