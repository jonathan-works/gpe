package br.com.infox.ibpm.manager;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.ibpm.dao.FluxoPapelDAO;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.entity.FluxoPapel;

@Name(FluxoPapelManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class FluxoPapelManager implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String NAME = "fluxoPapelManager";

	@In
	private FluxoPapelDAO fluxoPapelDAO;
	
	public List<FluxoPapel> listByFluxo(Fluxo fluxo) {
		return fluxoPapelDAO.listByFluxo(fluxo);
	}
	
}