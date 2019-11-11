package br.com.infox.epp.ajuda.query;

public interface PaginaQuery {

    String TABLE_PAGINA = "tb_pagina";
    String SEQUENCE_PAGINA = "sq_tb_pagina";
    String ID_PAGINA = "id_pagina";
    String DESCRICAO = "ds_descricao";
    String URL = "ds_url";

    String PARAM_URL = "url";
    String PAGINA_BY_URL = "paginaByUrl";
    String PAGINA_BY_URL_QUERY = "select o from Pagina o where o.url = :"
            + PARAM_URL;

}
