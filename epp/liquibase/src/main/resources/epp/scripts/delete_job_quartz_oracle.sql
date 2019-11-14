CREATE OR REPLACE PROCEDURE DeleteJobQuartz(idQuatzTriggerParamName VARCHAR2) AS

	triggerName VARCHAR2(200);
	jobName VARCHAR2(200);

BEGIN
	SELECT vl_variavel INTO triggerName	
	FROM tb_parametro 
	WHERE nm_variavel = idQuatzTriggerParamName;
	
	SELECT job_name INTO jobName
	FROM qrtz_triggers 
	WHERE trigger_name = triggerName;
	
	DELETE FROM qrtz_cron_triggers
	WHERE trigger_name = triggerName;

	DELETE FROM qrtz_triggers 
	WHERE trigger_name = triggerName;

	DELETE FROM qrtz_job_details
	WHERE job_name = jobName;
	
	DELETE FROM tb_parametro
	WHERE nm_variavel = idQuatzTriggerParamName;

END DeleteJobQuartz;