package br.com.infox.epa.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epa.dao.NaturezaDAO;

@Name(NaturezaManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class NaturezaManager {

	public static final String NAME = "naturezaManager";

	@In
	private NaturezaDAO naturezaDAO;
	
}