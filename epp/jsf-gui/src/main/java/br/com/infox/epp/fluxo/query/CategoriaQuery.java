package br.com.infox.epp.fluxo.query;

public interface CategoriaQuery {

    String TABLE_CATEGORIA = "tb_categoria";
    String SEQUENCE_CATEGORIA = "sq_tb_categoria";
    String ID_CATEGORIA = "id_categoria";
    String DESCRICAO_CATEGORIA = "ds_categoria";
    String CATEGORIA_ATTRIBUTE = "categoria";

    String QUERY_PARAM_CATEGORIA = "categoria";

    String LIST_CATEGORIAS_BY_NATUREZA = "listCategoriaByNatureza";
    
    
    String QUERY_PARAM_NATUREZA = "natureza";

    String LIST_CATEGORIAS_BY_NATUREZA_QUERY = "select c from NaturezaCategoriaFluxo ncf inner join ncf.categoria c where ncf.natureza = :"+ QUERY_PARAM_NATUREZA;


    String LIST_PROCESSO_EPP_BY_CATEGORIA = "listProcessoEpaByCategoria";


    String LIST_PROCESSO_EPA_BY_CATEGORIA_QUERY = "select distinct(c), "
            + "(select count(p) from Processo p "
            + "inner join p.naturezaCategoriaFluxo ncf "
            + "inner join ncf.categoria cat where cat = c), "
            + "(select count(pTarefa) from ProcessoTarefa pTarefa "
            + "inner join pTarefa.processo pEpa "
            + "inner join pEpa.naturezaCategoriaFluxo ncf "
            + "inner join ncf.categoria cat "
            + "where cat = c and (pTarefa.tempoGasto / pTarefa.tempoPrevisto) <= 1 "
            + "and pTarefa.dataFim is null), "
            + "(select count(p) from Processo p "
            + "inner join p.naturezaCategoriaFluxo ncf "
            + "inner join ncf.fluxo f "
            + "where ncf.categoria = c and (p.tempoGasto / f.qtPrazo) <= 1), "
            + "(select count(pTarefa) from ProcessoTarefa pTarefa "
            + "inner join pTarefa.processo pEpa "
            + "inner join pEpa.naturezaCategoriaFluxo ncf "
            + "inner join ncf.categoria cat "
            + "where cat = c and (pTarefa.tempoGasto / pTarefa.tempoPrevisto) > 1) "
            + "from Categoria c "
            + "inner join c.naturezaCategoriaFluxoList ncfList "
            + "inner join ncfList.processoList procList ";

}
