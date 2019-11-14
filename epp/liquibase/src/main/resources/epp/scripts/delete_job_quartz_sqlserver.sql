CREATE PROCEDURE DeleteJobQuartz(@idQuatzTriggerParamName VARCHAR(100)) AS
BEGIN
	DECLARE @triggerName VARCHAR(200);
	DECLARE @jobName VARCHAR(200);

	SET @triggerName = (SELECT vl_variavel FROM tb_parametro 
		WHERE nm_variavel = @idQuatzTriggerParamName);
	
	SET @jobName = (SELECT job_name	FROM qrtz_triggers 
		WHERE trigger_name = @triggerName);
	
	DELETE FROM qrtz_cron_triggers
	WHERE trigger_name = @triggerName;

	DELETE FROM qrtz_triggers 
	WHERE trigger_name = @triggerName;

	DELETE FROM qrtz_job_details
	WHERE job_name = @jobName;
	
	DELETE FROM tb_parametro
	WHERE nm_variavel = @idQuatzTriggerParamName;
END