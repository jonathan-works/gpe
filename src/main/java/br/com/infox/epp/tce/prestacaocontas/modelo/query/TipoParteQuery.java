package br.com.infox.epp.tce.prestacaocontas.modelo.query;

public interface TipoParteQuery {
    String LIST_TIPOS_PARTE_PARA_MODELO_PRESTACAO_CONTAS = "TipoParte.listTiposParteParaModeloPrestacaoContas";
    String LIST_TIPOS_PARTE_PARA_MODELO_PRESTACAO_CONTAS_QUERY =
            "select o from TipoParte o where not exists"
            + "(select 1 from ResponsavelModeloPrestacaoContas r where r.tipoParte = o) "
            + "order by o.descricao";
}
