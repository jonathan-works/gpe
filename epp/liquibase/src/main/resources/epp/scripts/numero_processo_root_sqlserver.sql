CREATE FUNCTION NumeroProcessoRoot(@idProcesso bigint) RETURNS VARCHAR(30) AS
BEGIN
	DECLARE @numeroProcessoRoot VARCHAR(30);
	DECLARE @processoPai BIGINT;
	
	SET @numeroProcessoRoot = (select p.nr_processo from tb_processo p where p.id_processo = @idProcesso);
	SET @processoPai = (select p.id_processo_pai from tb_processo p where p.id_processo = @idProcesso);
	WHILE (@processoPai is not null)
	BEGIN
		SET @numeroProcessoRoot = (select p.nr_processo from tb_processo p where p.id_processo = @processoPai);
		SET @processoPai = (select p.id_processo_pai from tb_processo p where p.id_processo = @processoPai);
	END
	
	RETURN @numeroProcessoRoot;
END
