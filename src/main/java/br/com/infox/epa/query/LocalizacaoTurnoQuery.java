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
	
}