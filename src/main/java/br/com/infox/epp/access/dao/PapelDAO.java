package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.PapelQuery.*;
import static br.com.infox.core.constants.WarningConstants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.itx.util.EntityUtil;

@Name(PapelDAO.NAME)
@AutoCreate
public class PapelDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "papelDAO";

    public List<Papel> getPapeisNaoAssociadosATipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_TIPO_MODELO_DOCUMENTO, tipoModeloDocumento);
        return getNamedResultList(PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO, parameters);
    }

	@SuppressWarnings(UNCHECKED)
	public List<Papel> getPapeisNaoAssociadosATipoProcessoDocumento(TipoProcessoDocumento tipoProcessoDocumento){
		String hql = "select o from Papel o where identificador not like '/%' and o not in " +
						"(select p.papel from TipoProcessoDocumentoPapel p " +
						"where p.tipoProcessoDocumento = :tipoProcessoDocumento)";
		return (List<Papel>) getEntityManager().createQuery(hql).setParameter("tipoProcessoDocumento", 
				tipoProcessoDocumento).getResultList();
	}
	
	public Papel getPapelByIndentificador(String identificador){
		String hql = "select o from Papel o where o.identificador = :identificador";
		Query query = getEntityManager().createQuery(hql).setParameter("identificador", identificador);
		return EntityUtil.getSingleResult(query);
	}
	
	
	@SuppressWarnings(UNCHECKED)
	public List<Papel> getPapeisByListaDeIdentificadores(List<String> identificadores){
		String hql = "select p from Papel p where identificador in (:list)";
		return (List<Papel>) getEntityManager().createQuery(hql).setParameter("list", identificadores).getResultList();
	}
	
	@SuppressWarnings(UNCHECKED)
	public List<Papel> getPapeisForaDaListaDeIdentificadores(List<String> identificadores){
        String hql = "select p from Papel p where identificador not in (:list)";
        return (List<Papel>) getEntityManager().createQuery(hql).setParameter("list", identificadores).getResultList();
    }
	
	@SuppressWarnings(UNCHECKED)
	public List<Papel> getPapeisDeUsuarioByLocalizacao(Localizacao localizacao){
		String hql = "select distinct l.papel from UsuarioLocalizacao l where l.localizacao = :loc ";
		return (List<Papel>) getEntityManager().createQuery(hql).setParameter("loc", localizacao).getResultList();
	}

}
