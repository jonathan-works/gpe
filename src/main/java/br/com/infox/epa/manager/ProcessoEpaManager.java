package br.com.infox.epa.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epa.dao.ProcessoEpaDAO;
import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.ibpm.entity.Fluxo;

@Name(ProcessoEpaManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoEpaManager extends GenericManager {

	private static final long serialVersionUID = 168832523707680478L;

	public static final String NAME = "processoEpaManager";

	@In
	private ProcessoEpaDAO processoEpaDAO;

	public List<ProcessoEpa> listAllNotEnded() {
		return processoEpaDAO.listAllNotEnded();
	}
	
	public List<ProcessoEpa> listNotEnded(Fluxo fluxo) {
		return processoEpaDAO.listNotEnded(fluxo);
	}
	
}