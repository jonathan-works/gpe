create table tmp_migracao_constraint(table_name varchar(200), constraint_name_antes varchar(200), constraint_name_depois varchar(200));
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_calendario_eventos','tb_calendario_eventos_id_localizacao_fkey','fk_calendario_eventos_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_calendario_eventos','tb_calendario_eventos_localizacao_data_unique','uk_calendario_eventos_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_categoria','tb_categoria_ds_categoria_unique','uk_categoria_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_definicao_variavel_processo','tb_definicao_variavel_processo_id_fluxo_fkey','fk_def_variavel_processo_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_definicao_variavel_processo','tb_definicao_variavel_processo_nm_variavel_id_fluxo_key','fk_def_variavel_processo_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_definicao_variavel_processo','tb_definicao_variavel_processo_pkey','pk_definicao_variavel_processo');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_dominio_variavel_tarefa','tb_dominio_variavel_tarefa_pkey','pk_dominio_variavel_tarefa');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_hist_participante_processo','fk_tb_historico_parte_processo_tb_usuario_login','fk_hist_parte_processo_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_hist_participante_processo','fk_tb_historico_parte_processo_tb_parte_processo','fk_hist_parte_processo_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_grupo_modelo_documento','tb_grupo_modelo_documento_ds_grupo_modelo_documento_unique','uk_grupo_modelo_documento_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_historico_ajuda','tb_historico_ajuda_id_pagina_fk','fk_historico_ajuda_id_pagina');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_historico_ajuda','fk_tb_historico_ajuda_id_usuario','fk_historico_ajuda_id_usuario');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_fluxo','fk_tb_fluxo_id_usuario_publicacao','fk_fluxo_id_usuario_publicacao');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_item_tipo_documento','tb_item_tipo_documento_id_localizacao_fkey','fk_item_tipo_documento_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_item_tipo_documento','tb_item_tipo_documento_id_grupo_modelo_documento_fkey','fk_item_tipo_documento_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento_fisico','tb_documento_fisico_id_processo_fkey','fk_documento_fisico_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_item','tb_item_ds_caminho_completo_unique','uk_item_ds_caminho_completo');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_localizacao_turno','tb_localizacao_turno_id_localizacao_fk','fk_localizacao_turno_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_lista_email','tb_lista_email_id_localizacao_fkey','fk_lista_email_id_localizacao');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo_tarefa','tb_processo_tarefa_id_processo_fk','fk_processo_tarefa_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo_tarefa','tb_processo_tarefa_id_tarefa_fk','fk_processo_tarefa_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo_tarefa','tb_processo_epa_tarefa_id_task_instance_fk','fk_processo_tarefa_03');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_parametro','fk_tb_parametro_id_usuario_modificacao','fk_parametro_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_nat_cat_fluxo_localizacao','tb_nat_cat_fluxo_localizacao_id_nat_cat_fluxo_fk','fk_nat_cat_fluxo_loc_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_nat_cat_fluxo_localizacao','tb_nat_cat_fluxo_localizacao_id_localizacao_fk','fk_nat_cat_fluxo_loc_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_nat_cat_fluxo_localizacao','tb_nat_cat_fluxo_localizacao_unq','uk_nat_cat_fluxo_loc_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_nat_cat_fluxo_localizacao','tb_nat_cat_fluxo_localizacao_pk','pk_nat_cat_fluxo_localizacao');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento','tb_processo_documento_id_tipo_processo_documento_fkey','fk_documento_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento','tb_processo_documento_id_processo_documento_bin_fkey','fk_documento_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento','fk_tb_processo_documento_id_usuario_inclusao','fk_documento_03');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento','fk_tb_processo_documento_id_usuario_alteracao','fk_documento_04');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_pessoa_juridica','tb_pessoa_juridica_id_pessoa_juridica_fk','fk_pessoa_juridica_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_pessoa_juridica','tb_pessoa_juridica_nr_cnpj_unique','uk_pessoa_juridica_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo_conexao','tb_processo_conexao_id_processo_fkey','fk_processo_conexao_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo_conexao','tb_processo_conexao_id_processo_conexo_fkey','fk_processo_conexao_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_modelo_documento','tb_modelo_documento_id_tipo_modelo_documento_fkey','fk_modelo_documento_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_prioridade_processo','tb_priori_proce_ds_prioridade_processo_uq','uk_prioridade_processo');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_sigilo_processo','tb_sigilo_processo_id_usuario_login_fkey','fk_sigilo_processo_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_sigilo_processo_permissao','tb_sigilo_processo_permissao_id_usuario_login_fkey','fk_sigilo_processo_permi_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_sigilo_processo_permissao','tb_sigilo_processo_permissao_id_sigilo_processo_fkey','fk_sigilo_processo_permi_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_sigilo_processo_permissao','tb_sigilo_processo_permissao_pkey','pk_sigilo_processo_permissao');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_task_conteudo_index','tb_conteudo_taskinstance_indexer_pk','pk_task_conteudo_index');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_relacionamento_processo','tb_relacionamento_processo_id_processo_fkey','fk_relacionamento_processo_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_relacionamento_processo','tb_relacionamento_processo_id_relacionamento_nr_processo_unique','uk_relacionamento_processo_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_relacionamento_processo','tb_relacionamento_processo_id_relacionamento_processo_pk','pk_relacionamento_processo');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_tipo_relacionamento_processo','tb_tipo_relacionamento_processo_ds_tipo_relacionamento_processo','uk_tipo_relaciona_processo_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_tipo_relacionamento_processo','tb_tipo_relacionamento_processo_id_tipo_relacionamento_processo','uk_tipo_relaciona_processo_02');--*
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_tipo_modelo_documento','tb_tipo_modelo_documento_id_grupo_modelo_documento_fkey','fk_tipo_modelo_documento_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_tipo_modelo_documento','tb_tipo_modelo_documento_ds_tipo_modelo_documento_unique','uk_tipo_modelo_documento_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_tipo_modelo_documento','tb_tipo_modelo_documento_ds_abreviacao_unique','uk_tipo_modelo_documento_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_usuario_localizacao','tb_usuario_localizacao_id_papel_fkey','fk_usuario_localizacao_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_usuario_localizacao','tb_usuario_localizacao_id_localizacao_fkey','fk_usuario_localizacao_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_usuario_localizacao','tb_usuario_localizacao_id_estrutura_fkey','fk_usuario_localizacao_03');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_usuario_localizacao','fk_tb_usuario_localizacao_tb_usuario_login','fk_usuario_localizacao_04');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_usuario_localizacao','tb_usuario_localizacao_id_usuario_key','uk_usuario_localizacao_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_sigilo_documento','tb_sigilo_documento_id_usuario_login_fkey','fk_sigilo_documento_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_sigilo_documento','tb_sigilo_documento_id_processo_documento_fkey','fk_sigilo_documento_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_tarefa_jbpm','tb_tarefa_jbpm_id_jbpm_task_fkey','fk_tarefa_jbpm_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_tipo_modelo_documento_papel','tb_tipo_modelo_documento_papel_id_tipo_modelo_documento_fkey','fk_tipo_modelo_doc_papel_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_tipo_modelo_documento_papel','tb_tipo_modelo_documento_papel_id_papel_fkey','fk_tipo_modelo_doc_papel_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_tipo_modelo_documento_papel','tb_tipo_modelo_documento_papel_pkey','pk_tipo_modelo_documento_papel');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_classificacao_documento_papel','tb_tipo_processo_documento_papel_id_papel_fkey','fk_classificacao_doc_papel_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_classificacao_documento_papel','tb_tipo_processo_documento_pape_id_tipo_processo_documento_fkey','fk_classificacao_doc_papel_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_classificacao_documento_papel','tb_tipo_processo_documento_papel_unq','fk_classificacao_doc_papel_03');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_classificacao_documento_papel','tb_tipo_processo_documento_papel_pkey','pk_classificacao_doc_papel');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_variavel_fluxo','tb_variavel_fluxo_id_variavel_fkey','fk_variavel_fluxo_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_variavel_fluxo','tb_variavel_fluxo_id_fluxo_fkey','fk_variavel_fluxo_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_bloqueio_usuario','fk_tb_bloqueio_usuario_id_usuario','fk_bloqueio_usuario_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_variavel_tipo_modelo','tb_variavel_tipo_modelo_id_variavel_fkey','fk_variavel_tipo_modelo_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_variavel_tipo_modelo','tb_variavel_tipo_modelo_id_tipo_modelo_documento_fkey','fk_variavel_tipo_modelo_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_papel_grupo','tb_papel_grupo_membro_do_grupo_fk','fk_papel_grupo_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo_localizacao_ibpm','tb_processo_localizacao_ibpm_id_ti','fk_proc_localizacao_ibpm_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo_localizacao_ibpm','tb_processo_localizacao_ibpm_id_task_jbpm_fk','fk_proc_localizacao_ibpm_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo_localizacao_ibpm','tb_processo_localizacao_ibpm_id_processo_fk','fk_proc_localizacao_ibpm_03');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo_localizacao_ibpm','tb_processo_localizacao_ibpm_id_processinstance_jbpm_fk','fk_proc_localizacao_ibpm_04');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo_localizacao_ibpm','tb_processo_localizacao_ibpm_id_papel_fk','fk_proc_localizacao_ibpm_05');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo_localizacao_ibpm','tb_processo_localizacao_ibpm_id_localizacao_fk','fk_proc_localizacao_ibpm_06');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo_localizacao_ibpm','pk_tb_processo_localizacao_ibpm','pk_processo_localizacao_ibpm');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_relacionamento','tb_relacionamento_id_tipo_relacionamento_processo_fk','fk_relacionamento_01'); --*
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_relacionamento','tb_relacionamento_id_relacionamento_pq','pk_relacionamento');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_usuario_taskinstance','tb_usuario_taskinstance_id_papel_fkey','fk_usuario_taskinstance_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_usuario_taskinstance','tb_usuario_taskinstance_id_localizacao_fkey','fk_usuario_taskinstance_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_usuario_taskinstance','fk_tb_usuario_taskinstance_tb_usuario_login','fk_usuario_taskinstance_03');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_usuario_taskinstance','fk_jbpm_task_instance_id_taskinstance','fk_usuario_taskinstance_04');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_modelo_documento_historico','tb_modelo_documento_historico_id_modelo_documento_fkey','fk_modelo_doc_historico_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_modelo_documento_historico','fk_tb_modelo_documento_historico_id_usuario_alteracao','fk_modelo_doc_historico_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_modelo_documento_historico','tb_modelo_documento_historico_pkey','pk_modelo_documento_historico');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_categoria_item','tb_categoria_item_id_categoria_fk','fk_categoria_item_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_sigilo_documento_permissao','tb_sigilo_documento_permissao_id_usuario_login_fkey','fk_sigilo_doc_permissao_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_sigilo_documento_permissao','tb_sigilo_documento_permissao_id_sigilo_documento_fkey','fk_sigilo_doc_permissao_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_sigilo_documento_permissao','tb_sigilo_documento_permissao_pkey','pk_sigilo_documento_permissao');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_natureza_categoria_fluxo','tb_nat_cat_assun_id_natureza_fk','fk_nat_cat_fluxo_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_natureza_categoria_fluxo','tb_nat_cat_assun_id_categoria_fk','fk_nat_cat_fluxo_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_natureza_categoria_fluxo','tb_natureza_categoria_fluxo_unq','uk_nat_cat_fluxo_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_natureza_categoria_fluxo','tb_nat_cat_assun_id_natureza_categoria_fluxo_pk','pk_natureza_categoria_fluxo');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_localizacao','tb_localizacao_id_localizacao_pai_fkey','fk_localizacao_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_localizacao','tb_localizacao_ds_caminho_completo_completo','uk_localizacao_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_usuario_login','tb_usuario_login_id_pessoa_fisica_key','fk_usuario_login_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_pessoa_fisica','tb_assinatura_documento_termo_adesao_fk','fk_pessoa_fisica_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_pessoa_fisica','tb_pessoa_fisica_id_pessoa_fisica_fk','fk_pessoa_fisica_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_assinatura_documento','cc_assinatura_doc_tp_assinatura','cc_ass_doc_tp_assinatura');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_assinatura_documento','tb_assinatura_documento_id_processo_documento_bin_fk','fk_assinatura_documento_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_assinatura_documento','tb_assinatura_documento_id_usuario_login_fk','fk_assinatura_documento_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_variavel_classificacao_doc','tb_variavel_classificacao_doc_nm_variavel_id_fluxo_id_tipo__key','fk_variavel_class_doc_01'); --*
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_variavel_classificacao_doc','fk_tb_variavel_classificacao_doc_id_fluxo','fk_variavel_class_doc_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_variavel_classificacao_doc','fk_tb_variavel_classificacao_doc_id_tp_proc_doc','fk_variavel_class_doc_03');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_variavel_classificacao_doc','pk_tb_variavel_classificacao_doc','tb_variavel_classificacao_doc'); --*
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_tipo_parte','tb_tipo_parte_nm_tipo_parte_key','uk_tipo_parte_01'); --*
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('qrtz_simple_triggers','qrtz_simple_triggers_sched_name_fkey','fk_qrtz_simple_triggers_01'); --*
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('qrtz_cron_triggers','qrtz_cron_triggers_sched_name_fkey','fk_qrtz_cron_triggers_01'); --*
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('qrtz_simprop_triggers','qrtz_simprop_triggers_sched_name_fkey','fk_qrtz_simprop_triggers_01'); --*
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('qrtz_blob_triggers','qrtz_blob_triggers_sched_name_fkey','fk_qrtz_blob_triggers_01'); --*
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento_modelo_comunic','doc_modelo_comunic_documento_fk','fk_doc_modelo_comunic_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento_modelo_comunic','doc_modelo_comunic_modelo_comunic_fk','fk_doc_modelo_comunic_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_destinatario_modelo_comunic','dest_modelo_comunic_processo_fk','fk_dest_modelo_comunic_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_destinatario_modelo_comunic','cc_dest_modelo_comunic_meio_expedicao','cc_dest_modelo_comunic_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_destinatario_modelo_comunic','dest_modelo_comunic_modelo_comunic_fk','fk_dest_modelo_comunic_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_destinatario_modelo_comunic','dest_modelo_comunic_pessoa_fisica_fk','fk_dest_modelo_comunic_03');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_destinatario_modelo_comunic','dest_modelo_comunic_localizacao_fk','fk_dest_modelo_comunic_04');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_destinatario_modelo_comunic','pk_tb_destinatario_modelo_comunic','pk_destinatario_modelo_comunic'); --*
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_modelo_comunicacao','modelo_comunic_perfil_template_fk','fk_modelo_comunicacao_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_numeracao_doc_sequencial','tb_numeracao_doc_sequencial_id_processo_key','uk_numeracao_doc_sequencial'); --*
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo','fk_natureza_categoria_fluxo_001','fk_processo_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_processo','fk_tb_processo_id_usuario_cadastro_processo','fk_processo_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento_temporario','fk_tb_documento_temporario_id_perfil_template','fk_documento_temporario_01');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento_temporario','fk_tb_documento_temporario_id_pasta','fk_documento_temporario_02');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento_temporario','fk_tb_documento_temporario_id_documento_bin','fk_documento_temporario_03');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento_temporario','fk_tb_documento_temporario_id_usuario_alteracao','fk_documento_temporario_04');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento_temporario','fk_tb_documento_temporario_id_usuario_inclusao','fk_documento_temporario_05');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento_temporario','fk_tb_documento_temporario_id_processo','fk_documento_temporario_06');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento_temporario','fk_tb_documento_temporario_id_classificacao_documento','fk_documento_temporario_07');
insert into tmp_migracao_constraint(table_name, constraint_name_antes, constraint_name_depois)values('tb_documento_temporario','fk_tb_documento_temporario_id_localizacao','fk_documento_temporario_08');

/*
postgres
*/
SELECT
concat('alter table ', nl.nspname,'.',tbl.relname, ' RENAME CONSTRAINT ', m.constraint_name_antes , ' to ', m.constraint_name_depois, ';')
FROM
pg_constraint c
LEFT JOIN pg_class tbl ON (c.conrelid = tbl.oid)
LEFT JOIN pg_namespace nl ON (tbl.relnamespace = nl.oid)
inner join tmp_migracao_constraint m on (tbl.relname = m.table_name and c.conname = m.constraint_name_antes);


/*
duplicados
*/
drop index if exists tb_caixa_nm_caixa_id_tarefa_key1;
drop index if exists tb_caixa_nm_caixa_id_tarefa_key2;
drop index if exists tb_caixa_id_tarefa_id_node_anterior_key1;
drop index if exists tb_caixa_id_tarefa_id_node_anterior_key2;
drop index if exists idx_tb_caixa1;

drop index if exists idx_tb_part_processo_id_pessoa;

ALTER TABLE tb_raia_perfil DROP CONSTRAINT raia_perfil_template_fk;

/*
finalizando
*/
drop table if exists tmp_migracao_constraint;