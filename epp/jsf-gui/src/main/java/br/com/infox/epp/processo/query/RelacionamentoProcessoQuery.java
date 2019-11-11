package br.com.infox.epp.processo.query;

import static br.com.infox.epp.processo.query.ProcessoQuery.ID_PROCESSO;

public interface RelacionamentoProcessoQuery {
    String TABLE_NAME = "tb_relacionamento_processo";
    String ID_RELACIONAMENTO_PROCESSO = "id_relacionamento_processo";
    String SEQUENCE_NAME = "sq_tb_relacionamento_processo";

    String RELACIONAMENTO_BY_PROCESSO = "relacionamentoByProcesso";
    String RELACIONAMENTO_BY_PROCESSO_QUERY = "select r from RelacionamentoProcesso rp inner join rp.relacionamento r where rp.processo=:"
            + ID_PROCESSO;
    
    String NUMERO_PROCESSO = "nr_processo";
}
