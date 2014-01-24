package br.com.infox.ibpm.variable.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaDAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;

@Name(DominioVariavelTarefaManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class DominioVariavelTarefaManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "dominioVariavelTarefaManager";
	
	public DominioVariavelTarefa getDominioVariavelTarefa(Integer id) {
		return getDao().getDominioVariavelTarefa(id);
	}
	
	@Override
	protected String getDaoName() {
		return DominioVariavelTarefaDAO.NAME;
	}
	
	@Override
	protected DominioVariavelTarefaDAO getDao() {
		return (DominioVariavelTarefaDAO) super.getDao();
	}
}
