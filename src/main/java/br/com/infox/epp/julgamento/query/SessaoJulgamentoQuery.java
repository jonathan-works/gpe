package br.com.infox.epp.julgamento.query;

public interface SessaoJulgamentoQuery {
	
	String PARAM_DATA = "data";
	String PARAM_HORA_INICIO = "horaInicio";
	String PARAM_HORA_FIM = "horaFim";
	String PARAM_SALA = "sala";
	
	String EXISTE_SESSAO_COM_SALA_E_HORARIO = "existeSessaoComSalaEHorario";
	String EXISTE_SESSAO_COM_SALA_E_HORARIO_QUERY = "select count(o) from SessaoJulgamento o " +
			"where o.sala = :" + PARAM_SALA + " and o.ativo = true " + 
			" and cast(o.data as date) = cast( :" + PARAM_DATA + " as date) " + 
			" and not ( (o.horaInicio > :" + PARAM_HORA_INICIO + " and o.horaInicio > :" + PARAM_HORA_FIM + " ) " + 
					 " or " +
					 "  (o.horaFim < :" + PARAM_HORA_INICIO + " and o.horaFim < :" + PARAM_HORA_FIM + " ) ) ";
	
}
