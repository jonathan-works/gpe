package br.com.infox.epp.fluxo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.FluxoPapel;
import br.com.infox.epp.fluxo.query.FluxoPapelQuery;

@Name(FluxoPapelDAO.NAME)
@AutoCreate
public class FluxoPapelDAO extends GenericDAO {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "fluxoPapelDAO";

    /**
     * Lista todos os papeis relacionados a um determinado fluxo.
     * 
     * @param fluxo que se deseja obter os papeis.
     * @return
     */
    public List<FluxoPapel> listByFluxo(Fluxo fluxo) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(FluxoPapelQuery.QUERY_PARAM_FLUXO, fluxo);
        List<FluxoPapel> resultList = getNamedResultList(FluxoPapelQuery.LIST_BY_FLUXO, map);
        return resultList;
    }

}
