package br.com.infox.epp.turno.dao;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;
import br.com.infox.epp.turno.entity.LocalizacaoTurno;
import br.com.infox.epp.turno.query.LocalizacaoTurnoQuery;
import br.com.infox.epp.turno.type.DiaSemanaEnum;

/**
 * Classe DAO para a entidade LocalizacaoTurno
 * @author Daniel
 *
 */
@Name(LocalizacaoTurnoDAO.NAME)
@AutoCreate
public class LocalizacaoTurnoDAO extends GenericDAO {

	private static final long serialVersionUID = 4917008814431859631L;
	public static final String NAME = "localizacaoTurnoDAO";
	
	/**
	 * Busca a LocalizacaoTurno da localização do processo em que o horário passado se
	 * encaixa 
	 * @param pt
	 * @param horario
	 * @return
	 */
	public LocalizacaoTurno getTurnoTarefa(Integer idProcesso, Date dataAnterior, Date dataAtual, DiaSemanaEnum diaSemana) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_IDPROCESSO, idProcesso);
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_HORA_INICIO, new Time(dataAnterior.getTime()));
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_HORA_FIM, new Time(dataAtual.getTime()));
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_DIA_SEMANA, diaSemana);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataAtual);
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_DIA, calendar.get(Calendar.DAY_OF_MONTH));
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_MES, calendar.get(Calendar.MONTH));
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_ANO, calendar.get(Calendar.YEAR));
		return getNamedSingleResult(LocalizacaoTurnoQuery.LOCALIZACAO_TURNO_BY_TAREFA_HORARIO, parameters);
	}
	
	/**
	 * Busca a LocalizacaoTurno da localização do processo em que o dia passado se
	 * encaixa 
	 * @param pt
	 * @return
	 */
	public Long countTurnoTarefaDia(ProcessoEpaTarefa pt, Date data, DiaSemanaEnum diaSemana) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_ID_TASK_INSTANCE, pt.getTaskInstance());
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_DIA_SEMANA, diaSemana);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_DIA, calendar.get(Calendar.DAY_OF_MONTH));
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_MES, calendar.get(Calendar.MONTH));
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_ANO, calendar.get(Calendar.YEAR));
		return getNamedSingleResult(LocalizacaoTurnoQuery.COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA, parameters);
	}

	/**
	 * Lista todos os registros filtrando por uma natureza.
	 * @param natureza que se desejar filtrar a seleção.
	 * @return lista de todos os registros referente a <code>natureza</code>
	 * informada.
	 */
	public List<LocalizacaoTurno> listByLocalizacao(Localizacao localizacao) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_LOCALIZACAO, localizacao);
		List<LocalizacaoTurno> resultList = getNamedResultList
								(LocalizacaoTurnoQuery.LIST_BY_LOCALIZACAO, 
								 parameters);
		return resultList;		
	}
	
	public List<LocalizacaoTurno> listByHoraInicioFim(Localizacao l, 
			Time horaInicio, Time horaFim) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_LOCALIZACAO, l);
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_HORA_INICIO, horaInicio);
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_HORA_FIM, horaFim);
		List<LocalizacaoTurno> resultList = getNamedResultList(
				LocalizacaoTurnoQuery.LIST_BY_HORA_INICIO_FIM, parameters);
		return resultList;
	}
	
	public Integer countByHoraInicioFim(Localizacao l, 
			Time horaInicio, Time horaFim) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_LOCALIZACAO, l);
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_HORA_INICIO, horaInicio);
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_HORA_FIM, horaFim);
		return getNamedSingleResult(
				LocalizacaoTurnoQuery.COUNT_BY_HORA_INICIO_FIM, parameters);
	}
	
	public void removerTurnosAnteriores(Localizacao localizacao){
		String hql = "delete from LocalizacaoTurno o where o.localizacao = :localizacao";
		getEntityManager().createQuery(hql).setParameter("localizacao", localizacao).executeUpdate();
	}
	
}