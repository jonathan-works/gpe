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

@Name(ProcessoEpaManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoEpaManager extends GenericManager {

	public static final String NAME = "processoEpaManager";

	@In
	private ProcessoEpaDAO processoEpaDAO;

	public List<ProcessoEpa> listAllNotEnded() {
		return processoEpaDAO.listAllNotEnded();
	}
	
}