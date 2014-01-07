package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.ATIVOS_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.LIST_BY_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.LIST_BY_RELATIONSHIP;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.PARAM_CATEGORIA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.PARAM_FLUXO;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.PARAM_NATUREZA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

/**
 * Classe DAO para a entidade NaturezaCategoriaFluxo
 * 
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
     * 
     * @param natureza que se desejar filtrar a seleção.
     * @return lista de todos os registros referente a <code>natureza</code>
     *         informada.
     */
    public List<NaturezaCategoriaFluxo> listByNatureza(Natureza natureza) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_NATUREZA, natureza);
        return getNamedResultList(LIST_BY_NATUREZA, parameters);
    }

    public NaturezaCategoriaFluxo getByRelationship(Natureza natureza, Categoria categoria, Fluxo fluxo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_NATUREZA, natureza);
        parameters.put(PARAM_CATEGORIA, categoria);
        parameters.put(PARAM_FLUXO, fluxo);
        return getNamedSingleResult(LIST_BY_RELATIONSHIP, parameters);
    }

    public List<NaturezaCategoriaFluxo> getActiveNaturezaCategoriaFluxoListByFluxo(Fluxo fluxo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_FLUXO, fluxo);
        return getNamedResultList(ATIVOS_BY_FLUXO, parameters);
    }
}
