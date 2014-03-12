package br.com.infox.epp.fluxo.query;

/**
 * Interface com as queries da entidade Fluxo
 * 
 * @author tassio
 */
public interface FluxoQuery {

    String TABLE_FLUXO = "tb_fluxo";
    String SEQUENCE_FLUXO = "public.sq_tb_fluxo";
    String ID_FLUXO = "id_fluxo";
    String ID_USUARIO_PUBLICACAO = "id_usuario_publicacao";
    String CODIGO_FLUXO = "cd_fluxo";
    String DESCRICAO_FLUXO = "ds_fluxo";
    String XML_FLUXO = "ds_xml";
    String PRAZO = "qt_prazo";
    String PUBLICADO = "in_publicado";
    String DATA_INICIO_PUBLICACAO = "dt_inicio_publicacao";
    String DATA_FIM_PUBLICACAO = "dt_fim_publicacao";
    String FLUXO_ATTRIBUTE = "fluxo";

    String PARAM_FLUXO = "fluxo";
    String PARAM_DESCRICAO = "descricao";
    String PARAM_NOME = "nomeFluxo";
    String PARAM_CODIGO = "codFluxo";

    String FLUXO_BY_DESCRICACAO = "fluxoByDescricao";
    String FLUXO_BY_DESCRICAO_QUERY = "select o from Fluxo o where o.fluxo like :"
            + PARAM_DESCRICAO;

    String FLUXO_BY_NOME = "fluxoByNome";
    String FLUXO_BY_NOME_QUERY = "select o from Fluxo o where o.fluxo = :"
            + PARAM_NOME;

    String LIST_ATIVOS = "listFluxoAtivo";
    String LIST_ATIVOS_QUERY = "select o from Fluxo o "
            + "where o.ativo = true";

    String COUNT_PROCESSOS_BY_FLUXO = "countProcessosByFluxo";
    String COUNT_PROCESSOS_BY_FLUXO_QUERY = "select count(o) from Processo o where o.naturezaCategoriaFluxo.fluxo = :fluxo";

    String COUNT_PROCESSOS_ATRASADOS = "countProcessosAtrasadosByFluxo";
    String COUNT_PROCESSOS_ATRASADOS_QUERY = "select count(o) from ProcessoEpa o "
            + "where o.dataFim is null "
            + "  and o.situacaoPrazo != 'SAT'"
            + "  and o.naturezaCategoriaFluxo.fluxo = :" + PARAM_FLUXO;

    String COUNT_FLUXO_BY_DESCRICAO = "countFluxoByDescricao";
    String COUNT_FLUXO_BY_DESCRICAO_QUERY = "select count(o) from Fluxo o where o.fluxo like :"
            + PARAM_DESCRICAO;

    String COUNT_FLUXO_BY_CODIGO = "countFluxoByCodigo";
    String COUNT_FLUXO_BY_CODIGO_QUERY = "select count(o) from Fluxo o where o.codFluxo = :"
            + PARAM_CODIGO;
}
