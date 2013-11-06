package br.com.infox.ibpm.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;

@Name(TipoModeloDocumentoDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class TipoModeloDocumentoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoModeloDocumentoDAO";
	
	public boolean existeOutroTipoModeloDocumentoComMesmaAbreviacao(TipoModeloDocumento tipoModeloDocumento){
		String abreviacaoUniqueConstraint = "select count(o) from TipoModeloDocumento o where o.abreviacao = :abreviacao " +
				"and o.idTipoModeloDocumento != :id";
		return (((Long) entityManager.createQuery(abreviacaoUniqueConstraint)
			.setParameter("abreviacao", tipoModeloDocumento.getAbreviacao())
			.setParameter("id", tipoModeloDocumento.getIdTipoModeloDocumento())
			.getSingleResult()) > 0);
	}
	
	public boolean existeOutroTipoModeloDocumentoComMesmaDescricao(TipoModeloDocumento tipoModeloDocumento){
		String tipoModeloDocumentoUniqueConstraint = "select count(o) from TipoModeloDocumento o where o.tipoModeloDocumento = :tipoModeloDocumento " +
				"and o.idTipoModeloDocumento != :id";
		return (((Long) entityManager.createQuery(tipoModeloDocumentoUniqueConstraint)
			.setParameter("tipoModeloDocumento", tipoModeloDocumento.getTipoModeloDocumento())
			.setParameter("id", tipoModeloDocumento.getIdTipoModeloDocumento())
			.getSingleResult()) > 0);
	}

}
