package br.com.infox.epp.processo.situacao.query;

import static br.com.infox.epp.processo.metadado.type.MetadadoProcessoType.UNIDADE_DECISORA_COLEGIADA;
import static br.com.infox.epp.processo.metadado.type.MetadadoProcessoType.UNIDADE_DECISORA_MONOCRATICA;
import br.com.infox.epp.processo.metadado.type.MetadadoProcessoType;

public interface SituacaoProcessoQuery {

    String PARAM_ID_TASKINSTANCE = "idTaskInstance";
    String PARAM_COLEGIADA_LOGADA = "colegiadaLogada";
    String PARAM_MONOCRATICA_LOGADA = "monocraticaLogada";
    String PARAM_ID_LOCALIZACAO = "idLocalizacao";
    String PARAM_ID_PESSOA = "idPessoa";
    String PARAM_TIPO_PROCESSO = "tipoProcesso";
    
    String COUNT_TAREFAS_ATIVAS_BY_TASK_ID = "countTarefasAtivasByTaskId";
    String COUNT_TAREFAS_ATIVAS_BY_TASK_ID_QUERY = "select count(o.idTaskInstance) from SituacaoProcesso o "
            + "where o.idTaskInstance = :" + PARAM_ID_TASKINSTANCE;

    String TAREFAS_TREE_ROOTS = "tarefasTreeQueryRoots";
    String TAREFAS_TREE_QUERY_ROOTS_BASE = "select new map(s.nomeFluxo as nomeFluxo, max(s.idFluxo) as idFluxo, 'Fluxo' as type) "
            + "from SituacaoProcesso s where 1=1 ";
    
    String TAREFAS_TREE_QUERY_ROOTS_SUFIX = "group by s.nomeFluxo order by s.nomeFluxo";
    
    String TAREFAS_TREE_QUERY_ROOTS_BY_TIPO = "and exists (select 1 from MetadadoProcesso mp where mp.metadadoType = '" 
    		+ MetadadoProcessoType.TIPO_PROCESSO + "' and mp.valor = :" + PARAM_TIPO_PROCESSO + " and mp.processo.idProcesso = s.idProcesso) ";
    
    String FILTRO_LOCALIZACAO_DESTINO = "exists (select 1 from MetadadoProcesso mp where mp.metadadoType = '"
    		+ MetadadoProcessoType.LOCALIZACAO_DESTINO + "' and cast(mp.valor as integer) = :" + PARAM_ID_LOCALIZACAO
    		+ " and s.idProcesso = mp.processo.idProcesso) ";
    
    String FILTRO_PESSOA_DESTINATARIO = "exists (select 1 from MetadadoProcesso mp where mp.metadadoType = '"
    		+ MetadadoProcessoType.PESSOA_DESTINATARIO + "' and cast(mp.valor as integer) = :" + PARAM_ID_PESSOA
    		+ " and s.idProcesso = mp.processo.idProcesso )";

    String TAREFAS_TREE_CHILDREN = "tarefasTreeQueryChildren";
    
    String TAREFAS_TREE_QUERY_CHILDREN_SUFIX = " group by s.nomeTarefa order by s.nomeTarefa";
    
    String TAREFAS_TREE_QUERY_CHILDREN_BASE = "select new map(s.nomeTarefa as nomeTarefa, "
            + "max(s.idTask) as idTask, max(s.idTarefa) as idTarefa, count(s.nomeCaixa) as qtdEmCaixa, "
            + "count(s.idProcesso) as qtd, 'caixa' as tree, 'Task' as type) from SituacaoProcesso s "
            + "where s.idFluxo = :idFluxo ";
    
    String TAREFAS_TREE_FILTER_POOLEDACTOR = " and s.pooledActor = :idPerfilTemplate ";
    
    String TAREFAS_TREE_CAIXAS = "tarefasTreeQueryCaixas";
    
    String TAREFAS_TREE_QUERY_CAIXAS_BASE = "select new map(c.idCaixa as idCaixa, "
            + "c.tarefa.idTarefa as idTarefa, "
            + "c.nomeCaixa as nomeCaixa, "
            + "'Caixa' as type, "
            + "(select count(distinct s.idProcesso) from SituacaoProcesso s where s.idCaixa = c.idCaixa ";
    
    String TAREFAS_TREE_QUERY_CAIXAS_SUFIX = ") as qtd) from Caixa c where c.tarefa.idTarefa = :taskId order by c.nomeCaixa";

    String ID_TAREFA_PARAM = "idTarefa";

    String PROCESSOS_ABERTOS_BASE_QUERY = "select s.idProcesso from SituacaoProcesso s where s.idTarefa = :" + ID_TAREFA_PARAM;
    
    String COM_CAIXA_COND = " and s.idCaixa is not null";
    String SEM_CAIXA_COND = " and s.idCaixa is null";
    
    String GROUP_BY_PROCESSO_SUFIX = " group by s.idProcesso";
    
    String FILTRO_PREFIX = " and s.idProcesso IN (SELECT pe.idProcesso from Processo pe WHERE";
    String FILTRO_SUFIX = ") ";
    String AND = " and ";
    String OR = " or ";

    String COM_COLEGIADA = " '" + UNIDADE_DECISORA_COLEGIADA + "' = any "
            + "(select mpl.metadadoType from MetadadoProcesso mpl where mpl.processo = pe and mpl.valor = :" + PARAM_COLEGIADA_LOGADA +")";
    String COM_MONOCRATICA = " '" + UNIDADE_DECISORA_MONOCRATICA + "' = any "
            + "(select mpl.metadadoType from MetadadoProcesso mpl where mpl.processo = pe and mpl.valor = :" + PARAM_MONOCRATICA_LOGADA + ")";
    String SEM_COLEGIADA = " not ('" + UNIDADE_DECISORA_COLEGIADA + "' = all (select mpl.metadadoType from MetadadoProcesso mpl where mpl.processo = pe))";
    String SEM_MONOCRATICA = " not ('" + UNIDADE_DECISORA_MONOCRATICA + "' = all (select mpl.metadadoType from MetadadoProcesso mpl where mpl.processo = pe))";
    
    String PROCESSOS_COM_COLEGIADA_COND = FILTRO_PREFIX + COM_COLEGIADA + FILTRO_SUFIX;
    String PROCESSOS_COM_MONOCRATICA_COND = FILTRO_PREFIX + COM_MONOCRATICA + FILTRO_SUFIX;
    String PROCESSOS_COM_COLEGIADA_E_MONOCRATICA_COND = FILTRO_PREFIX + COM_COLEGIADA + AND + COM_MONOCRATICA + FILTRO_SUFIX;
    String PROCESSOS_SEM_COLEGIADA_NEM_MONOCRATICA_COND = FILTRO_PREFIX + SEM_COLEGIADA + AND + SEM_MONOCRATICA + FILTRO_SUFIX;

}
