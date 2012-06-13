package br.com.infox.ibpm.manager;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.ProcessoEventoDAO;
import br.com.infox.ibpm.entity.ProcessoEvento;

/**
 * Classe que contem a regra de negocio da entidade
 * ProcessoEvento, assim como o acesso a sua classe DAO.
 * @author Daniel
 *
 */
@Name(ProcessoEventoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoEventoManager extends GenericManager implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "processoEventoManager";
	
	@In
	private ProcessoEventoDAO processoEventoDAO;
	
	public List<ProcessoEvento> listEventoNaoProcessado() {
		return processoEventoDAO.listEventoNaoProcessado();
	}
	
}