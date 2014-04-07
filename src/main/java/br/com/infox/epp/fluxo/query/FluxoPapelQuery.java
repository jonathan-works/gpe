package br.com.infox.epp.fluxo.query;

public interface FluxoPapelQuery {

    String TABLE_FLUXO_PAPEL = "tb_fluxo_papel";
    String SEQUENCE_FLUXO_PAPEL = "sq_tb_fluxo_papel";
    String ID_FLUXO_PAPEL = "id_fluxo_papel";
    String ID_FLUXO = "id_fluxo";
    String ID_PAPEL = "id_papel";

    String PARAM_FLUXO = "fluxo";

    String LIST_BY_FLUXO = "listFluxoPapelByFluxo";
    String LIST_BY_FLUXO_QUERY = "select o from FluxoPapel o "
            + "where o.fluxo = :" + PARAM_FLUXO;

}
