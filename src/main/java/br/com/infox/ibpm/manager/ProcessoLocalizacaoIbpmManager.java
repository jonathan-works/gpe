package br.com.infox.ibpm.manager;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.ibpm.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.ibpm.entity.Localizacao;

@Name(ProcessoLocalizacaoIbpmManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoLocalizacaoIbpmManager implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String NAME = "processoLocalizacaoIbpmManager";

	@In
	private ProcessoLocalizacaoIbpmDAO processoLocalizacaoIbpmDAO;
	
	public List<Localizacao> listByTaskInstance(Long idTaskInstance) {
		return processoLocalizacaoIbpmDAO.listByTaskInstance(idTaskInstance);
	}
	
}