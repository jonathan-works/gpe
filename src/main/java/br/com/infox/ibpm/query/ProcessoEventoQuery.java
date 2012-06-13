package br.com.infox.ibpm.query;

public interface ProcessoEventoQuery {

	String LIST_EVENTO_NAO_PROCESSADO = "listEventoNaoProcessado";
	String LIST_EVENTO_NAO_PROCESSADO_QUERY = "select o from ProcessoEvento o where o.processado = false and " +
											  "o.verificadoProcessado = false";
	
}