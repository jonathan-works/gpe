package br.com.infox.epp.julgamento.query;

public interface SalaTurnoQuery {
    String DELETE_TURNOS_ANTERIORES = "salaDeleteTurnosAnteriores";
    String QUERY_PARAM_SALA = "sala";
    String DELETE_TURNOS_ANTERIORES_QUERY = "delete from SalaTurno o where o.sala = :" + QUERY_PARAM_SALA;
    

    String LIST_BY_SALA = "listSalaTurnoBySala";
    String LIST_BY_SALA_QUERY = "select o from SalaTurno o where o.sala = :" + QUERY_PARAM_SALA;

}
