package br.com.infox.epp.fluxo.query;

public interface NaturezaQuery {

    String TABLE_NATUREZA = "tb_natureza";
    String SEQUENCE_NATUREZA = "sq_tb_natureza";
    String ID_NATUREZA = "id_natureza";
    String DESCRICAO_NATUREZA = "ds_natureza";
    String TIPO_PARTES = "tp_partes";
    String NUMERO_PARTES_FISICAS = "nr_partes_fisicas";
    String NUMERO_PARTES_JURIDICAS = "nr_partes_juridicas";
    String OBRIGATORIO_PARTES = "in_partes";
    String NATUREZA_ATTRIBUTE = "natureza";
    String LOCKED = "in_lock";
    String PRIMARIA = "in_primaria";
    
    String NATUREZA_FIND_BY_PRIMARIA = "natureza.findByPrimaria";
    String NATUREZA_FIND_BY_PRIMARIA_QUERY = "select n from Natureza n where n.primaria = true and n.ativo = true";

}
