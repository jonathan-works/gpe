-- Jbpm
delete from jbpm_variableinstance;
delete from jbpm_job;
update jbpm_processinstance set roottoken_ = null, superprocesstoken_ = null;
update jbpm_taskinstance set token_ = null;
update jbpm_tokenvariablemap set token_ = null;
delete from jbpm_token;
delete from tb_processo_localizacao_ibpm;
delete from tb_processo_epa_tarefa;
delete from jbpm_taskinstance;
delete from jbpm_swimlaneinstance;
delete from jbpm_tokenvariablemap;
delete from jbpm_moduleinstance;
delete from jbpm_processinstance;

--epa
delete from tb_processo_documento;
delete from tb_processo_documento_bin;
delete from tb_processo_epa;
delete from tb_processo;

SELECT setval('sq_tb_processo', 1, true);