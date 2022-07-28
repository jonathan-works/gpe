package br.com.infox.epp.view.query;

import java.util.Arrays;

public interface ViewSituacaoProcessoQuery {

    String PARAM_PROCESS_DEFINITION = "processDefinitionId";
    String PARAM_ID_USUARIO_LOGIN = "idUsuarioLogin";
    String PARAM_TIPO_PROCESSO_METADADO = "tipoProcessoMetadado";
    String PARAM_TIPO_PROCESSO_VALOR = "tipoProcessoValor";
    String PARAM_ID_LOCALIZACAO = "idLocalizacao";
    String PARAM_LOCALIZACAO_DESTINO = "localizacaoDestino";
    String PARAM_PERFIL_DESTINO = "perfilDestino";
    String PARAM_ID_PERFIL_TEMPLATE = "idPerfilTemplate";
    String PARAM_ID_PESSOA_FISICA = "idPessoaFisica";
    String PARAM_CODIGO_PERFIL_TEMPLATE = "codigoPerfilTemplate";
    String PARAM_NOME_USUARIO_LOGIN = "nomeUsuarioLogin";
    String PARAM_TIPO_USER = "user";
    String PARAM_TIPO_GROUP = "group";
    String PARAM_TIPO_LOCAL = "local";
    String PARAM_CODIGO_LOCALIZACAO = "codigoLocalizacao";
    String PARAM_NUMERO_PROCESSO_ROOT = "nmrProcessoRoot";
    String PARAM_ACTOR_ID = "idActor";
    String PARAM_IDS_TASK_INSTANCE = "taskInstancesIDS";

    String TASK_INSTANCES = "ViewSituacaoProcesso.getTaskIntances";
    StringBuilder TASK_INSTANCES_QUERY = new StringBuilder("select Cast(taskins.ID_ as varchar) idti,")
            .append("task.NAME_ , ")
            .append("taskins.ASSIGNEE_, ")
            .append("Cast(processins.ID_ as varchar) as proid, ")
            .append("tasknode.KEY_, ")
            .append("proce.id_processo as idProcesso,")
            .append("caixa.nm_caixa ,")
            .append("caixa.id_caixa ,")
            .append("nat.ds_natureza as nomeNatureza,")
            .append("cat.ds_categoria as nomeCategoria,")
            .append("proce.nr_processo as numeroProcesso,")
            .append("procroot.id_processo ,")
            .append("procroot.nr_processo ,")
            .append("usulog.nm_usuario,")
            .append("prioproc.id_prioridade_processo, ")
            .append("prioproc.ds_prioridade_processo, ")
            .append("prioproc.nr_peso, ")
            .append("convert(varchar, proce.dt_inicio, 120), ")
            .append("natroot.ds_natureza ,")
            .append("catroot.ds_categoria ,")
            .append("vssp.in_documento_assinar ,")
            .append("fluxo.ds_fluxo ,")
            .append("fluxo.id_fluxo ,")
            .append("Cast((select 1 from JBPM_VARIABLEINSTANCE vi where vi.NAME_ = 'perfilVisualizarRecuperar'")
            .append(" and STRINGVALUE_ = concat(:")
            .append(PARAM_CODIGO_LOCALIZACAO).append(",'/',:").append(PARAM_CODIGO_PERFIL_TEMPLATE).append(")")
            .append(" and vi.TASKINSTANCE_ = vssp.id_taskinstance) as varchar) as podeVisualizar ")
            .append("from  vs_situacao_processo vssp ")
            .append("inner join JBPM_TASKINSTANCE taskins on vssp.id_taskinstance = taskins.ID_  ")
            .append("inner join JBPM_PROCESSINSTANCE processins on vssp.id_processinstace = processins.ID_  ")
            .append("inner join tb_processo proce on vssp.id_processo = proce.id_processo ")
            .append("inner join tb_processo procroot on vssp.id_processo_root = procroot.id_processo_root ")
            .append("inner join tb_usuario_login usulog on vssp.id_usuario_cadastro_processo = usulog.id_usuario_login  ")
            .append("left join tb_caixa caixa on vssp.id_caixa = caixa.id_caixa ")
            .append("left join tb_prioridade_processo prioproc on vssp.id_prioridade_processo = prioproc.id_prioridade_processo ")
            .append("inner join tb_natureza_categoria_fluxo natcat on vssp.id_natureza_categoria_fluxo = natcat.id_natureza_categoria_fluxo ")
            .append("inner join tb_natureza nat on natcat.id_natureza = nat.id_natureza ")
            .append("inner join tb_categoria cat on natcat.id_categoria  = cat.id_categoria ")
            .append("inner join tb_fluxo flux on natcat.id_fluxo = flux.id_fluxo ")
            .append("inner join tb_natureza_categoria_fluxo natcatroot on vssp.id_natureza_categoria_fluxo_root = natcatroot.id_natureza_categoria_fluxo ")
            .append("inner join tb_natureza natroot on natcatroot.id_natureza = natroot.id_natureza ")
            .append("inner join tb_categoria catroot on natcatroot.id_categoria  = catroot.id_categoria ")
            .append("inner join JBPM_TASK task on taskins.TASK_ = task.ID_  ")
            .append("inner join JBPM_NODE tasknode on task.TASKNODE_ = tasknode.ID_ ")
            .append("inner join dbo.JBPM_PROCESSDEFINITION processdefin on  processins.PROCESSDEFINITION_ = processdefin.ID_ " )
            .append("inner join dbo.tb_fluxo fluxo on processdefin.NAME_ = fluxo.ds_fluxo ")
            .append("where processins.END_ is null ")
            .append("and taskins.ISOPEN_ = 1 ")
            .append("and taskins.ISSUSPENDED_ = 0 ")
            .append("and Cast(flux.id_fluxo as varchar) = :")
            .append(PARAM_PROCESS_DEFINITION);

    StringBuilder TASK_INSTANCES_QUERY_WITH_VARIABLE_INSTANCE = new StringBuilder("select Cast(taskins.ID_ as varchar) idti,")
            .append("task.NAME_ , ")
            .append("taskins.ASSIGNEE_, ")
            .append("Cast(processins.ID_ as varchar) as proid, ")
            .append("tasknode.KEY_, ")
            .append("proce.id_processo as idProcesso,")
            .append("caixa.nm_caixa ,")
            .append("caixa.id_caixa ,")
            .append("nat.ds_natureza as nomeNatureza,")
            .append("cat.ds_categoria as nomeCategoria,")
            .append("proce.nr_processo as numeroProcesso,")
            .append("procroot.id_processo ,")
            .append("procroot.nr_processo ,")
            .append("usulog.nm_usuario,")
            .append("prioproc.id_prioridade_processo, ")
            .append("prioproc.ds_prioridade_processo, ")
            .append("prioproc.nr_peso, ")
            .append("convert(varchar, proce.dt_inicio, 120), ")
            .append("natroot.ds_natureza ,")
            .append("catroot.ds_categoria ,")
            .append("vssp.in_documento_assinar ,")
            .append("fluxo.ds_fluxo ,")
            .append("fluxo.id_fluxo ")
            .append("from  vs_situacao_processo vssp ")
            .append("inner join JBPM_TASKINSTANCE taskins on vssp.id_taskinstance = taskins.ID_  ")
            .append("inner join JBPM_PROCESSINSTANCE processins on vssp.id_processinstace = processins.ID_  ")
            .append("inner join tb_processo proce on vssp.id_processo = proce.id_processo ")
            .append("inner join tb_processo procroot on vssp.id_processo_root = procroot.id_processo_root ")
            .append("inner join tb_usuario_login usulog on vssp.id_usuario_cadastro_processo = usulog.id_usuario_login  ")
            .append("left join tb_caixa caixa on vssp.id_caixa = caixa.id_caixa ")
            .append("left join tb_prioridade_processo prioproc on vssp.id_prioridade_processo = prioproc.id_prioridade_processo ")
            .append("inner join tb_natureza_categoria_fluxo natcat on vssp.id_natureza_categoria_fluxo = natcat.id_natureza_categoria_fluxo ")
            .append("inner join tb_natureza nat on natcat.id_natureza = nat.id_natureza ")
            .append("inner join tb_categoria cat on natcat.id_categoria  = cat.id_categoria ")
            .append("inner join tb_fluxo flux on natcat.id_fluxo = flux.id_fluxo ")
            .append("inner join tb_natureza_categoria_fluxo natcatroot on vssp.id_natureza_categoria_fluxo_root = natcatroot.id_natureza_categoria_fluxo ")
            .append("inner join tb_natureza natroot on natcatroot.id_natureza = natroot.id_natureza ")
            .append("inner join tb_categoria catroot on natcatroot.id_categoria  = catroot.id_categoria ")
            .append("inner join JBPM_TASK task on taskins.TASK_ = task.ID_  ")
            .append("inner join JBPM_NODE tasknode on task.TASKNODE_ = tasknode.ID_ ")
            .append("inner join dbo.JBPM_PROCESSDEFINITION processdefin on  processins.PROCESSDEFINITION_ = processdefin.ID_ " )
            .append("inner join dbo.tb_fluxo fluxo on processdefin.NAME_ = fluxo.ds_fluxo ")
            .append("where processins.END_ is null ")
            .append("and taskins.ISOPEN_ = 1 ")
            .append("and taskins.ISSUSPENDED_ = 0 ")
            .append("and Cast(flux.id_fluxo as varchar) = :")
            .append(PARAM_PROCESS_DEFINITION)
            .append(" and taskins.ID_ in (:")
            .append(PARAM_IDS_TASK_INSTANCE)
            .append(")");

    StringBuilder subQuerySigilo = new StringBuilder(" and ( not exists (select 1 from tb_sigilo_processo sigproc where sigproc.id_processo = proce.id_processo and sigproc.in_ativo = 1 and sigproc.in_sigiloso = 1) ")
            .append("  or exists (SELECT 1 from tb_sigilo_processo_permissao sigprocper where sigprocper.id_sigilo_processo ")
            .append("     in (select sigproc2.id_sigilo_processo  from tb_sigilo_processo sigproc2 where sigproc2.in_ativo = 1) ")
            .append("        and sigprocper.in_ativo = 1 and sigprocper.id_usuario_login = :")
            .append(PARAM_ID_USUARIO_LOGIN)
            .append("))");

    StringBuilder subQueryTipoProcessoNull = new StringBuilder(" and (not exists (select 1 from tb_metadado_processo metaproc where metaproc.nm_metadado_processo = :")
            .append(PARAM_TIPO_PROCESSO_METADADO)
            .append(" and metaproc.id_processo = proce.id_processo ))");

    StringBuilder subQueryTipoProcesso = new StringBuilder(" and (exists (select 1 from tb_metadado_processo metaproc where metaproc.nm_metadado_processo = :")
            .append(PARAM_TIPO_PROCESSO_METADADO)
            .append(" and metaproc.id_processo = proce.id_processo ")
            .append(" and metaproc.vl_metadado_processo = :")
            .append(PARAM_TIPO_PROCESSO_VALOR)
            .append("))");

    StringBuilder queryLocExpedidor = new StringBuilder(" and proce.id_localizacao = :")
            .append(PARAM_ID_LOCALIZACAO);

    StringBuilder queryDestDestinatario = new StringBuilder(" and (( EXISTS (select 1 from tb_metadado_processo meta where meta.id_processo = proce.id_processo and meta.nm_metadado_processo = :")
            .append(PARAM_LOCALIZACAO_DESTINO)
            .append(" and meta.vl_metadado_processo = :")
            .append(PARAM_ID_LOCALIZACAO).append(") ")
            .append(" and (EXISTS (select 1 from tb_metadado_processo metaperfil where metaperfil.nm_metadado_processo = :")
            .append(PARAM_PERFIL_DESTINO)
            .append(" and ((metaperfil.id_processo = proce.id_processo and metaperfil.vl_metadado_processo = :")
            .append(PARAM_ID_PERFIL_TEMPLATE)
            .append(") ")
            .append(" or not EXISTS (select 1 from tb_metadado_processo metaperfil2 where metaperfil2.nm_metadado_processo = :")
            .append(PARAM_PERFIL_DESTINO)
            .append(" and metaperfil2.id_processo = proce.id_processo )))))")
            .append( "or (EXISTS (select 1 from tb_metadado_processo metadest where metadest.nm_metadado_processo = :")
            .append(PARAM_PERFIL_DESTINO)
            .append(" and metadest.id_processo = proce.id_processo ")
            .append(" and metadest.vl_metadado_processo = :")
            .append(PARAM_ID_PESSOA_FISICA)
            .append(")))");

    StringBuilder subQueryTipoProcessoFilter = new StringBuilder(" and exists (select 1 from JBPM_TASKACTORPOOL tap ")
            .append(" inner join JBPM_POOLEDACTOR pa on tap.POOLEDACTOR_ = pa.ID_ ")
            .append(" inner join JBPM_TASKINSTANCE ti on tap.TASKINSTANCE_ = ti.ID_ ")
            .append(" where ti.ID_ = taskins.ID_ ")
            .append(" and (((pa.TYPE_ is null ) and")
            .append(" ((pa.ACTORID_ = :")
            .append(PARAM_ID_PERFIL_TEMPLATE)
            .append(" )  or (pa.ACTORID_ = :")
            .append(PARAM_CODIGO_PERFIL_TEMPLATE)
            .append(")))")
            .append(" or  (pa.ACTORID_ = :")
            .append(PARAM_NOME_USUARIO_LOGIN)
            .append(" and pa.TYPE_ = :")
            .append(PARAM_TIPO_USER)
            .append(")")
            .append(" or (pa.ACTORID_ = concat(:")
            .append(PARAM_CODIGO_LOCALIZACAO)
            .append(",'&',:")
            .append(PARAM_CODIGO_PERFIL_TEMPLATE)
            .append(") and pa.TYPE_ = :")
            .append(PARAM_TIPO_GROUP)
            .append(")")
            .append(" or (pa.ACTORID_ = :")
            .append(PARAM_CODIGO_LOCALIZACAO)
            .append("  and pa.TYPE_ = :")
            .append(PARAM_TIPO_LOCAL)
            .append("  )) ");

    StringBuilder queryProcRoot = new StringBuilder(" and procroot.nr_processo like concat('%',:")
            .append(PARAM_NUMERO_PROCESSO_ROOT)
            .append(",'%')");

}
