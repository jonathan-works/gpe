CREATE OR REPLACE VIEW vs_situacao_processo 
AS SELECT taskinstan0_.token_ AS id_situacao_processo, pooledacto2_.actorid_ AS nm_pooled_actor, 
            fluxo.id_fluxo, processdef6_.name_ AS nm_fluxo, task4_.name_ AS nm_tarefa, tj.id_tarefa, 
            c.id_caixa, c.nm_caixa, proce.id_processo, taskinstan0_.procinst_ AS id_process_instance, 
            taskinstan0_.id_ AS id_task_instance, taskinstan0_.task_ AS id_task, 
            taskinstan0_.actorid_ AS nm_actorid
            FROM jbpm_taskinstance taskinstan0_
            LEFT JOIN tb_tarefa_jbpm tj ON tj.id_jbpm_task = taskinstan0_.task_
            LEFT JOIN tb_tarefa tbtarefa ON tbtarefa.id_tarefa = tj.id_tarefa
            LEFT JOIN tb_fluxo fluxo ON fluxo.id_fluxo = tbtarefa.id_fluxo
            LEFT JOIN jbpm_taskactorpool pooledacto1_ ON taskinstan0_.id_ = pooledacto1_.taskinstance_
            LEFT JOIN jbpm_pooledactor pooledacto2_ ON pooledacto1_.pooledactor_ = pooledacto2_.id_
            RIGHT JOIN tb_processo proce ON proce.id_processo = (( SELECT DISTINCT ( SELECT DISTINCT longinstan1_.longvalue_
                FROM jbpm_variableinstance longinstan1_
                WHERE longinstan1_.class_ = 'L' AND longinstan1_.name_ = 'processo' 
                AND longinstan1_.processinstance_ = longinstan3_.processinstance_) AS longvalue_
            FROM jbpm_variableinstance longinstan3_
            WHERE longinstan3_.PROCESSINSTANCE_ = taskinstan0_.PROCINST_))
            LEFT JOIN tb_caixa c ON proce.id_caixa = c.id_caixa
            LEFT JOIN jbpm_task task4_ ON taskinstan0_.task_ = task4_.id_
            LEFT JOIN jbpm_processinstance processins5_ ON taskinstan0_.procinst_ = processins5_.id_
            LEFT JOIN jbpm_processdefinition processdef6_ ON processins5_.processdefinition_ = processdef6_.id_
            WHERE (EXISTS ( SELECT 1
            FROM jbpm_taskinstance taskinstan7_
            LEFT JOIN jbpm_taskactorpool pooledacto8_ ON taskinstan7_.id_ = pooledacto8_.taskinstance_
            LEFT JOIN jbpm_pooledactor pooledacto9_ ON pooledacto8_.pooledactor_ = pooledacto9_.id_, jbpm_task task10_
            WHERE taskinstan7_.task_ = task10_.id_ AND taskinstan7_.issuspended_ = 0 
            AND taskinstan7_.isopen_ = 1 AND taskinstan0_.id_ = taskinstan7_.id_));
