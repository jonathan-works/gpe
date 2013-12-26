package br.com.infox.epp.fluxo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery;
import br.com.itx.util.EntityUtil;

/**
 * Classe DAO para a entidade NaturezaCategoriaFluxo
 * @author Daniel
 *
 */
@Name(NaturezaCategoriaFluxoDAO.NAME)
@AutoCreate
public class NaturezaCategoriaFluxoDAO extends GenericDAO {

	private static final long serialVersionUID = -1456893293816945596L;
	public static final String NAME = "naturezaCategoriaFluxoDAO";

	/**
	 * Lista todos os registros filtrando por uma natureza.
	 * @param natureza que se desejar filtrar a seleção.
	 * @return lista de todos os registros referente a <code>natureza</code>
	 * informada.
	 */
	public List<NaturezaCategoriaFluxo> listByNatureza(Natureza natureza) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(NaturezaCategoriaFluxoQuery.PARAM_NATUREZA, natureza);
		List<NaturezaCategoriaFluxo> resultList = getNamedResultList
								(NaturezaCategoriaFluxoQuery.LIST_BY_NATUREZA, 
								 parameters);
		return resultList;		
	}

    public NaturezaCategoriaFluxo getByRelationship(Natureza natureza, Categoria categoria, Fluxo fluxo) {
        Query query = EntityUtil.createQuery(NaturezaCategoriaFluxoQuery.BY_RELATIONSHIP_QUERY)
                .setParameter(NaturezaCategoriaFluxoQuery.PARAM_NATUREZA, natureza)
                .setParameter(NaturezaCategoriaFluxoQuery.PARAM_CATEGORIA, categoria)
                .setParameter(NaturezaCategoriaFluxoQuery.PARAM_FLUXO, fluxo);
        return EntityUtil.getSingleResult(query);
    }
    
    public List<NaturezaCategoriaFluxo> getActiveNaturezaCategoriaFluxoListByFluxo(Fluxo fluxo) {
        String hql = "select ncf from NaturezaCategoriaFluxo ncf " +
                "inner join ncf.natureza n " +
                "inner join ncf.categoria c " +
                "where n.ativo=true " +
                "and c.ativo=true " +
                "and ncf.fluxo=:fluxo";
        return EntityUtil.getEntityManager().createQuery(hql, NaturezaCategoriaFluxo.class)
                .setParameter("fluxo", fluxo)
                .getResultList();
    }
}