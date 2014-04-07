package br.com.infox.epp.fluxo.query;

public interface ItemQuery {

    String TABLE_ITEM = "tb_item";
    String SEQUENCE_ITEM = "sq_tb_item";
    String ID_ITEM = "id_item";
    String ID_ITEM_PAI = "id_item_pai";
    String CODIGO_ITEM = "cd_item";
    String DESCRICAO_ITEM = "ds_item";
    String CAMINHO_COMPLETO = "ds_caminho_completo";
    String ITEM_PAI_ATTRIBUTE = "itemPai";

    String GET_FOLHAS = "getFolhas";
    String GET_FOLHAS_QUERY = "select i from Item i "
            + "where i.caminhoCompleto like concat(:" + CAMINHO_COMPLETO
            + ",'%') and not i.idItem in ("
            + "select distinct it.itemPai.idItem from Item it "
            + "where not it.itemPai is null)";

}
