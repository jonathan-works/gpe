package br.com.infox.epp.access.dao;

import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.Papel;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.util.constants.WarningConstants;
import br.com.itx.util.EntityUtil;

@Name(PapelDAO.NAME)
@AutoCreate
public class PapelDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "papelDAO";
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<Papel> getPapeisNaoAssociadosATipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
		String hql = "select o from Papel o where identificador not like '/%' and o.idPapel not in ("
				+ "select p.papel.idPapel from TipoModeloDocumentoPapel p "
				+ "where p.tipoModeloDocumento = :tipoModeloDocumento)";
		return (List<Papel>) entityManager.createQuery(hql).setParameter("tipoModeloDocumento",
				tipoModeloDocumento).getResultList();
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<Papel> getPapeisNaoAssociadosATipoProcessoDocumento(TipoProcessoDocumento tipoProcessoDocumento){
		String hql = "select o from Papel o where identificador not like '/%' and o not in " +
						"(select p.papel from TipoProcessoDocumentoPapel p " +
						"where p.tipoProcessoDocumento = :tipoProcessoDocumento)";
		return (List<Papel>) entityManager.createQuery(hql).setParameter("tipoProcessoDocumento", 
				tipoProcessoDocumento).getResultList();
	}
	
	public Papel getPapelByIndentificador(String identificador){
		String hql = "select o from Papel o where o.identificador = :identificador";
		Query query = entityManager.createQuery(hql).setParameter("identificador", identificador);
		return EntityUtil.getSingleResult(query);
	}
	
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<Papel> getPapeisByListaDeIdentificadores(List<String> identificadores){
		String hql = "select p from Papel p where identificador in (:list)";
		return (List<Papel>) entityManager.createQuery(hql).setParameter("list", identificadores).getResultList();
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<Papel> getPapeisDeUsuarioByLocalizacao(Localizacao localizacao){
		String hql = "select distinct l.papel from UsuarioLocalizacao l where l.localizacao = :loc ";
		return (List<Papel>) entityManager.createQuery(hql).setParameter("loc", localizacao).getResultList();
	}

}
