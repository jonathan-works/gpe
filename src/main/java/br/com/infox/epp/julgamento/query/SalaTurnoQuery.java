package br.com.infox.epp.julgamento.query;

public interface SalaTurnoQuery {
    String DELETE_TURNOS_ANTERIORES = "salaDeleteTurnosAnteriores";
    String QUERY_PARAM_SALA = "sala";
    String QUERY_PARAM_HORA_INICIO = "horaInicio";
    String QUERY_PARAM_HORA_FIM = "horaFim";
    String QUERY_PARAM_DIA_SEMANA = "diaSemana";
    String DELETE_TURNOS_ANTERIORES_QUERY = "delete from SalaTurno o where o.sala = :" + QUERY_PARAM_SALA;
    

    String LIST_BY_SALA = "listSalaTurnoBySala";
    String LIST_BY_SALA_QUERY = "select o from SalaTurno o where o.sala = :" + QUERY_PARAM_SALA;
    
    String EXISTE_SALA_DISPONIVEL_HORARIO_DIA_SEMANA = "existeSalaDisponivelHorarioDia";
    String EXISTE_SALA_DISPONIVEL_HORARIO_DIA_SEMANA_QUERY = "select count(*) from SalaTurno o " +
    		"where o.horaInicio <= :" + QUERY_PARAM_HORA_INICIO + 
    		" and o.horaFim >= :" + QUERY_PARAM_HORA_FIM +
    		" and o.sala = :" + QUERY_PARAM_SALA + 
    		" and o.diaSemana = :" + QUERY_PARAM_DIA_SEMANA;

}
