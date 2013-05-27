package br.com.infox.epp.query;

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
	String QUERY_PARAM_DIA = "dia";
	String QUERY_PARAM_MES = "mes";
	String QUERY_PARAM_ANO = "ano";
	
	String QUERY_PARAM_ID_TASK_INSTANCE = "idTaskInstance";
	String QUERY_PARAM_TAREFA = "tarefa";
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
			"   not exists(from CalendarioEventos cal " +
			"			   where cal.localizacao = lt.localizacao and " +
			"					 cal.dia = :" + QUERY_PARAM_DIA + " and " +
			"					 cal.mes = :" + QUERY_PARAM_MES + " and " +
			"					 (cal.ano is null or cal.ano = :" + QUERY_PARAM_ANO + ")) and " +
			"   exists (select 1 from ProcessoLocalizacaoIbpm pli where " +
			" 	 			   pli.idTaskJbpm in (select tj.idJbpmTask" +
			"                                   from TarefaJbpm tj" +
			"                                   where tj.tarefa = :"+QUERY_PARAM_TAREFA+") and " +
			"				   pli.localizacao = lt.localizacao and	" +
			"	 			   pli.contabilizar = true)";
	
	String COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA = "localizacaoTurnoByTarefaDia";
	String COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA_QUERY = 
			"select count(lt) from LocalizacaoTurno lt " +
			"where lt.diaSemana = :" + QUERY_PARAM_DIA_SEMANA + " and " +
			"   not exists(select o from CalendarioEventos o " +
			"			   where o.localizacao = lt.localizacao and " +
			"					 o.dia = :" + QUERY_PARAM_DIA + " and " +
			"					 o.mes = :" + QUERY_PARAM_MES + " and " +
			"					 (o.ano is null or o.ano = :" + QUERY_PARAM_ANO + ")) and " +
			"   exists (select o from ProcessoLocalizacaoIbpm o where " +
			" 	 			   o.idTaskInstance = :"+QUERY_PARAM_ID_TASK_INSTANCE+" and " +
			"				   o.localizacao = lt.localizacao and	" +
			"	 			   o.contabilizar = true)";
	
}