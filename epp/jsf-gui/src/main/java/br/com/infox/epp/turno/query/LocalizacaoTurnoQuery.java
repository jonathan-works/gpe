package br.com.infox.epp.turno.query;

public interface LocalizacaoTurnoQuery {

    String AND = " and ";
    String QUERY_PARAM_LOCALIZACAO = "localizacao";
    String QUERY_PARAM_HORA_INICIO = "horaInicio";
    String QUERY_PARAM_HORA_FIM = "horaFim";
    String QUERY_PARAM_DATA = "data";
    String QUERY_PARAM_MES = "mes";
    String QUERY_PARAM_ANO = "ano";

    String QUERY_PARAM_ID_TASK_INSTANCE = "idTaskInstance";
    String QUERY_PARAM_IDPROCESSO = "tarefa";
    String QUERY_PARAM_DIA_SEMANA = "diaSemana";

    String LIST_BY_LOCALIZACAO = "listLocalizacaoTurnoByLocalizcao";
    String LIST_BY_LOCALIZACAO_QUERY = "select o from LocalizacaoTurno o "
            + "where o.localizacao = :" + QUERY_PARAM_LOCALIZACAO;

    String LIST_BY_HORA_INICIO_FIM = "listByLocalizacaoTurnoByHoraInicioHoraFim";
    String LIST_BY_HORA_INICIO_FIM_QUERY = "select o from LocalizacaoTurno o where o.localizacao = :"
            + QUERY_PARAM_LOCALIZACAO
            + " and ((o.horaInicio >= :"
            + QUERY_PARAM_HORA_INICIO
            + AND
            + "o.horaInicio <= :"
            + QUERY_PARAM_HORA_FIM
            + ") or (o.horaFim >= :"
            + QUERY_PARAM_HORA_INICIO
            + " and o.horaFim < :"
            + QUERY_PARAM_HORA_FIM + "))";

    String COUNT_BY_HORA_INICIO_FIM = "countByLocalizacaoTurnoByHoraInicioHoraFim";
    String COUNT_BY_HORA_INICIO_FIM_QUERY = "select count(o) from LocalizacaoTurno o where o.localizacao = :"
            + QUERY_PARAM_LOCALIZACAO
            + " and ((o.horaInicio >= :"
            + QUERY_PARAM_HORA_INICIO
            + AND
            + "o.horaInicio <= :"
            + QUERY_PARAM_HORA_FIM
            + ") or (o.horaFim >= :"
            + QUERY_PARAM_HORA_INICIO
            + " and o.horaFim < :"
            + QUERY_PARAM_HORA_FIM + "))";

    String LOCALIZACAO_TURNO_BY_TAREFA_HORARIO = "localizacaoTurnoByTarefaHorario";
    String LOCALIZACAO_TURNO_BY_TAREFA_HORARIO_QUERY = "select lt from LocalizacaoTurno lt "
            + "where (:"
            + QUERY_PARAM_HORA_INICIO
            + " between lt.horaInicio and lt.horaFim or "
            + "       :"
            + QUERY_PARAM_HORA_FIM
            + " between lt.horaInicio and lt.horaFim) and"
            + "   lt.diaSemana = :"
            + QUERY_PARAM_DIA_SEMANA
            + AND
            + "   not exists(from CalendarioEventos cal "
            + "              where cal.localizacao = lt.localizacao "
            + "              and (:" + QUERY_PARAM_DATA + " between cal.dataInicio and cal.dataFim)) and "
            + "   exists (select 1 from ProcessoLocalizacaoIbpm pli where "
            + "                  pli.processo.idProcesso = :"
            + QUERY_PARAM_IDPROCESSO
            + AND
            + "                  pli.localizacao = lt.localizacao and "
            + "                  pli.contabilizar = true)";

    String LOCALIZACAO_TURNO_BY_TAREFA = "localizacaoTurnoByTarefa";
    String LOCALIZACAO_TURNO_BY_TAREFA_QUERY = "select lt from LocalizacaoTurno lt "
            + "where lt.diaSemana = :"
            + QUERY_PARAM_DIA_SEMANA
            + "  and not exists(from CalendarioEventos cal "
            + "              where cal.localizacao = lt.localizacao "
            + "                   and (:" + QUERY_PARAM_DATA + " between cal.dataInicio and cal.dataFim)) "
            + "  and exists (select 1 from UsuarioTaskInstance uti where "
            + "                  uti.idTaskInstance = :"
            + QUERY_PARAM_ID_TASK_INSTANCE
            + "                 and uti.localizacao = lt.localizacao)"
            + "order by lt.horaInicio";

    String COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA = "localizacaoTurnoByTarefaDia";
    String COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA_QUERY = "select count(lt) from LocalizacaoTurno lt "
            + "where lt.diaSemana = :" + QUERY_PARAM_DIA_SEMANA
            + "  and not exists(from CalendarioEventos cal "
            + "              where cal.localizacao = lt.localizacao "
            + "                   and (:" + QUERY_PARAM_DATA + " between cal.dataInicio and cal.dataFim))"
            + " and "
            + "   exists (select o from ProcessoLocalizacaoIbpm o where "
            + " 	 			   o.idTaskInstance = :"
            + QUERY_PARAM_ID_TASK_INSTANCE
            + AND
            + "				   o.localizacao = lt.localizacao and	"
            + "	 			   o.contabilizar = true)";

    String DELETE_TURNOS_ANTERIORES = "deleteTurnosAnteriores";
    String DELETE_TURNOS_ANTERIORES_QUERY = "delete from LocalizacaoTurno o where o.localizacao = :"
            + QUERY_PARAM_LOCALIZACAO;
}
