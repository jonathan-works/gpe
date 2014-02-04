package br.com.infox.epp.fluxo.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.fluxo.dao.FluxoPapelDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.FluxoPapel;

@Name(FluxoPapelManager.NAME)
// TODO: Ver o porque desse escopo
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class FluxoPapelManager extends Manager<FluxoPapelDAO, FluxoPapel> {
	private static final long serialVersionUID = 1L;

	public static final String NAME = "fluxoPapelManager";

	public List<FluxoPapel> listByFluxo(Fluxo fluxo) {
		return getDao().listByFluxo(fluxo);
	}
	
}