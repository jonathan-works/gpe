package br.com.infox.epp.julgamento.manager;

import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.joda.time.DateTime;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;
import br.com.infox.epp.julgamento.dao.SessaoJulgamentoDAO;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.entity.SessaoJulgamento;
import br.com.infox.epp.julgamento.type.Periodicidade;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.util.time.DateRange;

@AutoCreate
@Name(SessaoJulgamentoManager.NAME)
public class SessaoJulgamentoManager extends Manager<SessaoJulgamentoDAO, SessaoJulgamento> {

    private static final LogProvider LOG = Logging.getLogProvider(SessaoJulgamentoManager.class);
    private static final long serialVersionUID = 1L;
	public static final String NAME = "sessaoJulgamentoManager";

	@In
	private SalaManager salaManager;
	@In
	private CalendarioEventosManager calendarioEventosManager;
	@In
	private SalaTurnoManager salaTurnoManager;
	@In
	private InfoxMessages infoxMessages;
	
	
	public List<SessaoJulgamento> getSessoesJulgamento(DateRange range){
		return getDao().getByDateRange(range);
	}
	
	public void persistIgnorandoErro(SessaoJulgamento sessaoJulgamento) throws DAOException {
		try {
			validate(sessaoJulgamento);
			persist(sessaoJulgamento);
		} catch (BusinessException e) {
		    LOG.info("SessaoJulgamentoManagerRF001", e);
		}
	}

	public void validate(SessaoJulgamento sessaoJulgamento) throws BusinessException {
		salaManager.lock(sessaoJulgamento.getSala());
		Date hoje = DateUtil.getBeginningOfDay(DateTime.now().toDate());
		if (!DateUtil.getEndOfDay(sessaoJulgamento.getData()).after(hoje)) {
			throw new BusinessException("#{infoxMessages['sessaoJulgamento.invalidDate.before']}");
		}
		if (sessaoJulgamento.getHoraFim().before(sessaoJulgamento.getHoraInicio())) {
			throw new BusinessException("#{infoxMessages['sessaoJulgamento.invalidTime.after']}");
		}
		if (!sessaoJulgamento.getSala().getForaExpediente() && !isDiaValido(sessaoJulgamento)) {
        	throw new BusinessException("#{infoxMessages['sessaoJulgamento.invalidTimespan']}");
        }
		boolean isSalaOcupada = getDao().existeSessaoJulgamentoComSalaEHorario(sessaoJulgamento);
		if (isSalaOcupada) {
			throw new BusinessException("#{infoxMessages['sessaoJulgamento.salaOcupada']}");
		}
	}

	public void afterSave(SessaoJulgamento sessaoJulgamento, Periodicidade periodicidade, 
			Object periodicidadeValue) throws CloneNotSupportedException, DAOException {
		if (periodicidadeValue instanceof Date) {
			gravarPeriodicidadeData(sessaoJulgamento, periodicidade, (Date) periodicidadeValue);
		} else if (periodicidadeValue instanceof Integer) {
			gravarPeriodicidadeRepeticoes(sessaoJulgamento, periodicidade, (Integer) periodicidadeValue);
		}
	}
	
	private void gravarPeriodicidadeData(SessaoJulgamento sessaoJulgamento, 
			Periodicidade periodicidade, Date dataAte) throws CloneNotSupportedException, DAOException {
		if (dataAte.before(sessaoJulgamento.getData())) {
			throw new BusinessException("#{infoxMessages['sessaoJulgamento.invalidToDate.before']}");
		}
		DateTime dataAtual = new DateTime(sessaoJulgamento.getData());
		while ( dataAte.after(dataAtual.toDate()) ) {
			dataAtual = plusPeriodicidade(dataAtual, periodicidade);
			if (calendarioEventosManager.isDiaUtil(dataAtual.toDate())) {
				SessaoJulgamento sessaoClone = sessaoJulgamento.clone();
				sessaoClone.setData(dataAtual.toDate());
				persistIgnorandoErro(sessaoClone);
			}
		}
	}
	
	private void gravarPeriodicidadeRepeticoes(SessaoJulgamento sessaoJulgamento, 
			Periodicidade periodicidade, Integer repeticoes) throws CloneNotSupportedException, DAOException {
		int qtRepeticoes = repeticoes;
		DateTime dataAtual = new DateTime(sessaoJulgamento.getData());
		while ( qtRepeticoes > 0 ) {
			dataAtual = plusPeriodicidade(dataAtual, periodicidade);
			if (calendarioEventosManager.isDiaUtil(dataAtual.toDate())) {
				SessaoJulgamento sessaoClone = sessaoJulgamento.clone();
				sessaoClone.setData(dataAtual.toDate());
				persistIgnorandoErro(sessaoClone);
				qtRepeticoes--;
			}
		}
	}
	
	private boolean isDiaValido(SessaoJulgamento sessaoJulgamento) {
		Date data = sessaoJulgamento.getData();
		Date horaInicio = sessaoJulgamento.getHoraInicio();
		Date horaFim = sessaoJulgamento.getHoraFim();
		Sala sala = sessaoJulgamento.getSala();
		return salaTurnoManager.isDiaHoraValido(data, horaInicio, horaFim, sala);
	}
	
	private DateTime plusPeriodicidade(DateTime dateTime, Periodicidade periodicidade) {
		switch (periodicidade) {
		case D:
			return dateTime.plusDays(1);
		case S:
			return dateTime.plusWeeks(1);
		case M:
			return dateTime.plusMonths(1);
		case A:
			return dateTime.plusYears(1);
		default:
			return null;
		}
	}
	
}
