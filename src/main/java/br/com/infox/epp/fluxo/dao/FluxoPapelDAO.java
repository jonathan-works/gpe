package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.FluxoPapelQuery.LIST_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.FluxoPapelQuery.PARAM_FLUXO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.FluxoPapel;

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
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_FLUXO, fluxo);
        return getNamedResultList(LIST_BY_FLUXO, parameters);
    }

}
