package br.com.infox.epa.dao;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epa.entity.LocalizacaoTurno;
import br.com.infox.epa.entity.ProcessoEpaTarefa;
import br.com.infox.epa.query.LocalizacaoTurnoQuery;
import br.com.infox.epa.type.DiaSemanaEnum;
import br.com.infox.ibpm.entity.Localizacao;

/**
 * Classe DAO para a entidade LocalizacaoTurno
 * @author Daniel
 *
 */
@Name(LocalizacaoTurnoDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class LocalizacaoTurnoDAO extends GenericDAO {

	public static final String NAME = "localizacaoTurnoDAO";
	
	/**
	 * Busca a LocalizacaoTurno da localização do processo em que o horário passado se
	 * encaixa 
	 * @param pt
	 * @param horario
	 * @return
	 */
	public LocalizacaoTurno getTurnoTarefa(ProcessoEpaTarefa pt, Date horario, DiaSemanaEnum diaSemana) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_ID_TASK_INSTANCE, pt.getTaskInstance());
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_HORA_INICIO, pt.getUltimoDisparo());
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_HORA_FIM, horario);
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_DIA_SEMANA, diaSemana);
		return getNamedSingleResult(LocalizacaoTurnoQuery.LOCALIZACAO_TURNO_BY_TAREFA_HORARIO, parameters);
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
	
	public int countByHoraInicioFim(Localizacao l, 
			Time horaInicio, Time horaFim) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_LOCALIZACAO, l);
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_HORA_INICIO, horaInicio);
		parameters.put(LocalizacaoTurnoQuery.QUERY_PARAM_HORA_FIM, horaFim);
		return getNamedSingleResult(
				LocalizacaoTurnoQuery.COUNT_BY_HORA_INICIO_FIM, parameters);
	}
	
}