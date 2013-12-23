package br.com.infox.epp.fluxo.query;

public interface DefinicaoVariavelProcessoQuery {
    
    String TABLE_DEFINICAO_VARIAVEL_PROCESSO = "tb_definicao_variavel_processo";
    String SEQUENCE_DEFINICAO_VARIAVEL_PROCESSO = "sq_tb_definicao_variavel_processo";
    String GENERATOR_DEFINICAO_VARIAVEL_PROCESSO = "DefinicaoVariavelProcessoGenerator";
    String ID_DEFINICAO_VARIAVEL_PROCESSO = "id_definicao_variavel_processo";
    String NOME_VARIAVEL = "nm_variavel";
    String LABEL = "ds_label";
    String ID_FLUXO = "id_fluxo";

    String PARAM_FLUXO = "fluxo";
    String PARAM_NOME = "nome";

    String LIST_BY_FLUXO = "listByFluxo";
    String LIST_BY_FLUXO_QUERY = "select o from DefinicaoVariavelProcesso o"
            + " where o.fluxo = :" + PARAM_FLUXO + " order by o.nome";

    String TOTAL_BY_FLUXO = "totalByFluxo";
    String TOTAL_BY_FLUXO_QUERY = "select count(o) from DefinicaoVariavelProcesso o where o.fluxo = :"
            + PARAM_FLUXO;

    String DEFINICAO_BY_FLUXO = "definicaoByFluxo";
    String DEFINICAO_BY_FLUXO_NOME_QUERY = "select o from DefinicaoVariavelProcesso o"
            + " where o.fluxo = :"
            + PARAM_FLUXO
            + " and o.nome = :"
            + PARAM_NOME;
}
