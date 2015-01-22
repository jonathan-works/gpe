package br.com.infox.epp.processo.comunicacao.tipo.crud;

public interface TipoComunicacaoQuery {
	String LIST_TIPO_COMUNICACAO_ATIVOS = "TipoComunicacao.listAtivos";
	String LIST_TIPO_COMUNICACAO_ATIVOS_QUERY = "select o from TipoComunicacao o where o.ativo = true order by o.descricao";
}
