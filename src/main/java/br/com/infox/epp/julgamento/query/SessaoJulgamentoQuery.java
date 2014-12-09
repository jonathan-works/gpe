package br.com.infox.epp.julgamento.query;

public interface SessaoJulgamentoQuery {
	
	String PARAM_DATA_FIM = "dataFim";
	String PARAM_DATA_INICIO = "dataInicio";
	String PARAM_SALA = "sala";
	
	String EXISTE_SESSAO_COM_SALA_E_HORARIO = "existeSessaoComSalaEHorario";
	String EXISTE_SESSAO_COM_SALA_E_HORARIO_QUERY = "select count(o) from SessaoJulgamento o " +
			"where o.sala = :" + PARAM_SALA + " and o.ativo = true " + 
			" and not ( (o.dataInicio > :" + PARAM_DATA_INICIO + " and o.dataInicio > :" + PARAM_DATA_FIM + " ) " + 
					 " or " +
					 "  (o.dataFim < :" + PARAM_DATA_INICIO + " and o.dataFim < :" + PARAM_DATA_FIM + " ) ) ";
	
}
