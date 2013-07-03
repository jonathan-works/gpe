package br.com.infox.epp.dao;

import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.Papel;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.itx.util.EntityUtil;

@Name(PapelDAO.NAME)
@AutoCreate
public class PapelDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "papelDAO";
	
	@SuppressWarnings("unchecked")
	public List<Papel> getPapeisNaoAssociadosATipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
		String hql = "select o from Papel o where identificador not like '/%' and o.idPapel not in ("
				+ "select p.papel.idPapel from TipoModeloDocumentoPapel p "
				+ "where p.tipoModeloDocumento = :tipoModeloDocumento)";
		return (List<Papel>) entityManager.createQuery(hql).setParameter("tipoModeloDocumento",
				tipoModeloDocumento).getResultList();
	}
	
	public Papel getPapelByIndentificador(String identificador){
		String hql = "select o from Papel o where o.identificador = :identificador";
		Query query = entityManager.createQuery(hql).setParameter("identificador", identificador);
		return EntityUtil.getSingleResult(query);
	}

}
