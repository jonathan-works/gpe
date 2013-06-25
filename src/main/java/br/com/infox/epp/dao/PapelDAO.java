package br.com.infox.epp.dao;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.Papel;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.TipoModeloDocumento;

@Name(PapelDAO.NAME)
@AutoCreate
public class PapelDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "papelDAO";
	
	@SuppressWarnings("unchecked")
	public List<Papel> getPapeisNaoAssociadosATipoModeloDocumento(
			TipoModeloDocumento tipoModeloDocumento) {
		String hql = "select o from Papel o where identificador not like '/%' and o.idPapel not in ("
				+ "select p.papel.idPapel from TipoModeloDocumentoPapel p "
				+ "where p.tipoModeloDocumento = :tipoModeloDocumento)";
		return (List<Papel>) entityManager.createQuery(hql).setParameter("tipoModeloDocumento",
				tipoModeloDocumento).getResultList();
	}

}
