package br.com.infox.epa.query;

/**
 * Interface com as queries da entidade de 
 * NaturezaLocalizacao
 * @author Daniel
 *
 */
public interface LocalizacaoTurnoQuery {

	String QUERY_PARAM_LOCALIZACAO = "localizacao";
	String QUERY_PARAM_HORA_INICIO = "horaInicio";
	String QUERY_PARAM_HORA_FIM = "horaFim";
	String QUERY_PARAM_ID_TASK_INSTANCE = "idTaskInstance";
	String QUERY_PARAM_DIA_SEMANA = "diaSemana";
	
	String LIST_BY_LOCALIZACAO = "listLocalizacaoTurnoByLocalizcao";
	String LIST_BY_LOCALIZACAO_QUERY = "select o from LocalizacaoTurno o " +
									"where o.localizacao = :"+QUERY_PARAM_LOCALIZACAO;

	String LIST_BY_HORA_INICIO_FIM = "listByLocalizacaoTurnoByHoraInicioHoraFim";
	String LIST_BY_HORA_INICIO_FIM_QUERY = 
		"select o from LocalizacaoTurno o where o.localizacao = :"+QUERY_PARAM_LOCALIZACAO
		+" and ((o.horaInicio >= :"+
		QUERY_PARAM_HORA_INICIO+" and " +
		"o.horaInicio <= :"+QUERY_PARAM_HORA_FIM+") or (o.horaFim >= :"+
		QUERY_PARAM_HORA_INICIO+" and o.horaFim < :"+QUERY_PARAM_HORA_FIM+"))";
	
	String COUNT_BY_HORA_INICIO_FIM = "countByLocalizacaoTurnoByHoraInicioHoraFim";
	String COUNT_BY_HORA_INICIO_FIM_QUERY = 
		"select count(o) from LocalizacaoTurno o where o.localizacao = :"+QUERY_PARAM_LOCALIZACAO
		+" and ((o.horaInicio >= :"+
		QUERY_PARAM_HORA_INICIO+" and " +
		"o.horaInicio <= :"+QUERY_PARAM_HORA_FIM+") or (o.horaFim >= :"+
		QUERY_PARAM_HORA_INICIO+" and o.horaFim < :"+QUERY_PARAM_HORA_FIM+"))";
	
	String LOCALIZACAO_TURNO_BY_TAREFA_HORARIO = "localizacaoTurnoByTarefaHorario";
	String LOCALIZACAO_TURNO_BY_TAREFA_HORARIO_QUERY = 
			"select lt from LocalizacaoTurno lt " +
			"where (:" +QUERY_PARAM_HORA_INICIO+ " between lt.horaInicio and lt.horaFim or " +
			"		:" +QUERY_PARAM_HORA_FIM+ " between lt.horaInicio and lt.horaFim) and" +
			"	lt.diaSemana = :" + QUERY_PARAM_DIA_SEMANA + " and " +
			"   lt.localizacao in (select o.localizacao from ProcessoLocalizacaoIbpm o where " +
			" 	 					o.idTaskInstance = :"+QUERY_PARAM_ID_TASK_INSTANCE+" and " +
			"	 					o.contabilizar = true)";
	
}