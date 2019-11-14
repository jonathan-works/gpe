CREATE OR REPLACE FUNCTION DeleteJobQuartz(idQuatzTriggerParamName VARCHAR(100)) RETURNS void as $$
DECLARE triggerName VARCHAR(200);
DECLARE jobName VARCHAR(200);
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
END;
$$ LANGUAGE plpgsql;