package br.com.infox.epp.fluxo.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.dao.FluxoPapelDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.FluxoPapel;

@Name(FluxoPapelManager.NAME)
@AutoCreate
public class FluxoPapelManager extends Manager<FluxoPapelDAO, FluxoPapel> {
    private static final long serialVersionUID = 1L;

    public static final String NAME = "fluxoPapelManager";

    public List<FluxoPapel> listByFluxo(Fluxo fluxo) {
        return getDao().listByFluxo(fluxo);
    }
    
    @Override
    public FluxoPapel remove(FluxoPapel o) throws DAOException {
        return super.remove(find(o.getIdFluxoPapel()));
    }
    
}
