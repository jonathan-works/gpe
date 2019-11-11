package br.com.infox.epp.fluxo.query;

public interface CategoriaItemQuery {

    String TABLE_CATEGORIA_ITEM = "tb_categoria_item";
    String SEQUENCE_CATEGORIA_ITEM = "sq_tb_categoria_item";
    String ID_CATEGORIA_ITEM = "id_categoria_item";
    String ID_CATEGORIA = "id_categoria";
    String ID_ITEM = "id_item";

    String QUERY_PARAM_CATEGORIA = "categoria";
    String QUERY_PARAM_ITEM = "item";

    String LIST_BY_CATEGORIA = "listCategoriaItemByCategoria";
    String LIST_BY_CATEGORIA_QUERY = "select o from CategoriaItem o "
            + "where o.categoria = :" + QUERY_PARAM_CATEGORIA;

    String COUNT_BY_CATEGORIA_ITEM = "countCategoriaItemByCategoriaAndItem";
    String COUNT_BY_CATEGORIA_ITEM_QUERY = "select count(o) from CategoriaItem o where o.categoria=:"
            + QUERY_PARAM_CATEGORIA + " and o.item=:" + QUERY_PARAM_ITEM;

}
