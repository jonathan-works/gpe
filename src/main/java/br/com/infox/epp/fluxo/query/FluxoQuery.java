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

    String FLUXO_PARAM = "fluxo";

    String LIST_ATIVOS = "listFluxoAtivo";
    String LIST_ATIVOS_QUERY = "select o from Fluxo o "
            + "where o.ativo = true";

    String COUNT_PROCESSOS_ATRASADOS = "countProcessosAtrasadosByFluxo";
    String COUNT_PROCESSOS_ATRASADOS_QUERY = "select count(o) from ProcessoEpa o "
            + "where o.dataFim is null "
            + "  and o.situacaoPrazo != 'SAT'"
            + "  and o.naturezaCategoriaFluxo.fluxo = :" + FLUXO_PARAM;

}
