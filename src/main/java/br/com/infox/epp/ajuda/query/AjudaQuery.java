package br.com.infox.epp.ajuda.query;

public interface AjudaQuery {
    
    String TABLE_AJUDA = "tb_ajuda";
    String SEQUENCE_AJUDA = "public.sq_tb_ajuda";
    String ID_AJUDA = "id_ajuda";
    String DATA_REGISTRO = "dt_registro";
    String TEXTO = "ds_texto";
    String PAGINA = "id_pagina";
    String USUARIO = "id_usuario";
    
    String PARAM_URL = "url";
    String AJUDA_BY_URL = "ajudaByUrl";
    String AJUDA_BY_URL_QUERY = "select a from Ajuda a where a.pagina.url = :" + PARAM_URL 
                                    + " order by a.dataRegistro desc";

}
