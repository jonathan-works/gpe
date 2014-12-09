package br.com.infox.epp.julgamento.manager;

import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.julgamento.dao.SessaoJulgamentoDAO;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.entity.SessaoJulgamento;
import br.com.infox.epp.julgamento.type.Periodicidade;
import br.com.infox.seam.exception.BusinessException;

@AutoCreate
@Name(SessaoJulgamentoManager.NAME)
public class SessaoJulgamentoManager extends Manager<SessaoJulgamentoDAO, SessaoJulgamento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "sessaoJulgamentoManager";

	@In
	private SalaManager salaManager;

	private void validateBeforePersist(SessaoJulgamento sessaoJulgamento) throws BusinessException {
		salaManager.lock(sessaoJulgamento.getSala());
		Sala sala = sessaoJulgamento.getSala();
		Date dataInicio = sessaoJulgamento.getDataInicio();
		Date dataFim = sessaoJulgamento.getDataFim();
		if (!sessaoJulgamento.getSala().getForaExpediente()) {
			
		}
		boolean isSalaOcupada = getDao().existeSessaoJulgamentoComSalaEHorario(sala, dataInicio, dataFim);
		if (isSalaOcupada) {
			throw new BusinessException("Sala já está ocupada para o horário");
		}
	}

	public void beforeSave(SessaoJulgamento sessaoJulgamento, Periodicidade periodicidade, 
			Object periodicidadeValue) throws BusinessException {
		validateBeforePersist(sessaoJulgamento);
		if (periodicidadeValue instanceof Date) {
			
		} else if (periodicidadeValue instanceof Integer) {
			
		}
	}

}
