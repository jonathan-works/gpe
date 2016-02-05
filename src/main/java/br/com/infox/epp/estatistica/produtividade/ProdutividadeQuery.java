package br.com.infox.epp.estatistica.produtividade;

public interface ProdutividadeQuery {

    String PARAM_USUARIO = "usuario";
    String PARAM_FLUXO = "fluxo";
    String PARAM_DATA_INICIO = "dataInicio";
    String PARAM_DATA_FIM = "dataFim";
    String PARAM_START = "start";
    String PARAM_COUNT = "count";

    String BASE_QUERY = "SELECT t.nr_prazo, l.ds_localizacao, p.ds_nome, ul.nm_usuario, "
            + " t.ds_tarefa, AVG(pet.nr_tempo_gasto) AS media_tempo_gasto, MIN(pet.nr_tempo_gasto) AS min_tempo_gasto,"
            + " MAX(pet.nr_tempo_gasto) AS max_tempo_gasto, COUNT(pet.id_processo_tarefa) AS quantidade_tarefas,"
            + " t.tp_prazo"
            + " FROM tb_processo_tarefa pet"
            + " INNER JOIN tb_usuario_taskinstance ut ON (ut.id_taskinstance = pet.id_task_instance)"
            + " INNER JOIN tb_papel p ON (p.id_papel = ut.id_papel)"
            + " INNER JOIN tb_usuario_login ul ON (ul.id_usuario_login = ut.id_usuario_login)"
            + " INNER JOIN tb_localizacao l ON (l.id_localizacao = ut.id_localizacao)"
            + " INNER JOIN tb_tarefa t ON (t.id_tarefa = pet.id_tarefa)";

    String INNER_JOIN_FLUXO = " INNER JOIN tb_processo pro ON (pro.id_processo = pet.id_processo)"
            + " INNER JOIN tb_natureza_categoria_fluxo ncf ON (ncf.id_natureza_categoria_fluxo = pro.id_natureza_categoria_fluxo)";

    String GROUP_BY = " GROUP BY t.id_tarefa, t.nr_prazo, l.ds_localizacao, p.ds_nome, ul.nm_usuario, t.ds_tarefa, t.tp_prazo";

    String ORDER_BY = " ORDER BY ul.nm_usuario";

    String CONDICAO_FIXA = " WHERE 1=1";

    String CONDICAO_USUARIO = " AND ut.id_usuario_login = :" + PARAM_USUARIO;
    String CONDICAO_FLUXO = " AND ncf.id_fluxo = :" + PARAM_FLUXO;
    String CONDICAO_DATA_INICIO = " AND pet.dt_inicio >= :" + PARAM_DATA_INICIO;
    String CONDICAO_DATA_FIM = " AND pet.dt_fim <= :" + PARAM_DATA_FIM;

    int INDEX_TEMPO_PREVISTO = 0;
    int INDEX_LOCALIZACAO = 1;
    int INDEX_PAPEL = 2;
    int INDEX_USUARIO = 3;
    int INDEX_TAREFA = 4;
    int INDEX_MEDIA_TEMPO_GASTO = 5;
    int INDEX_MINIMO_TEMPO_GASTO = 6;
    int INDEX_MAXIMO_TEMPO_GASTO = 7;
    int INDEX_QUANTIDADE_TAREFAS = 8;
    int INDEX_TIPO_PRAZO_TAREFA = 9;
}
