package br.com.infox.epp.julgamento.manager;

import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.julgamento.dao.SessaoJulgamentoDAO;
import br.com.infox.epp.julgamento.entity.SessaoJulgamento;
import br.com.infox.epp.julgamento.type.Periodicidade;

@AutoCreate
@Name(SessaoJulgamentoManager.NAME)
public class SessaoJulgamentoManager extends Manager<SessaoJulgamentoDAO, SessaoJulgamento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "sessaoJulgamentoManager";

	@In
	private SalaManager salaManager;

	public boolean isInstanceValid(SessaoJulgamento sessaoJulgamento) {
		salaManager.lock(sessaoJulgamento.getSala());
		
		return false;
	}

	public void beforeSave(SessaoJulgamento sessaoJulgamento, Periodicidade periodicidade, 
			Object periodicidadeValue) {
		if (periodicidadeValue instanceof Date) {
			
		} else if (periodicidadeValue instanceof Integer) {
			
		}
	}

}
