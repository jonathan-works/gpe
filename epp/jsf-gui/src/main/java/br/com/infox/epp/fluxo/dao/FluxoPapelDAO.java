package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.FluxoPapelQuery.LIST_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.FluxoPapelQuery.PARAM_FLUXO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.FluxoPapel;

@Stateless
@AutoCreate
@Name(FluxoPapelDAO.NAME)
public class FluxoPapelDAO extends DAO<FluxoPapel> {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "fluxoPapelDAO";

    public List<FluxoPapel> listByFluxo(Fluxo fluxo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_FLUXO, fluxo);
        return getNamedResultList(LIST_BY_FLUXO, parameters);
    }

}
