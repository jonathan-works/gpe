CREATE FUNCTION DataUtilAdd(@type VARCHAR(5), @qtRepeticoes INT, @data DATE) RETURNS DATE AS
BEGIN
	DECLARE @dataAtual DATE;
	DECLARE @qtCalendario INT;
	DECLARE @adding INT;
	
	SET @adding = @qtRepeticoes;
	SET @dataAtual = @data;
	IF @adding < 0
	  SET @qtRepeticoes = @qtRepeticoes * -1;
	WHILE (@qtRepeticoes > 0)
	BEGIN
		IF @type = 'year'
			BEGIN
				IF @adding > 0
				  SET @dataAtual = DATEADD(year,  1, @dataAtual);	
				ELSE
				  SET @dataAtual = DATEADD(year, -1, @dataAtual);	
			END
		ELSE IF @type = 'month'
			BEGIN
				IF @adding > 0
				  SET @dataAtual = DATEADD(month,  1, @dataAtual);	
				ELSE
				  SET @dataAtual = DATEADD(month, -1, @dataAtual);
			END
		ELSE
			BEGIN
				IF @adding > 0
				  SET @dataAtual = DATEADD(day,  1, @dataAtual);	
				ELSE
				  SET @dataAtual = DATEADD(day, -1, @dataAtual);
			END 
		SET @qtCalendario = (select count(*) from tb_calendario_eventos ce 
						  where (ce.dt_dia = DATEPART(day, @dataAtual) and ce.dt_mes = DATEPART(month, @dataAtual) and ce.dt_ano is null) 
						  		or (ce.dt_dia = DATEPART(day, @dataAtual) and ce.dt_mes = DATEPART(month, @dataAtual) and ce.dt_ano = DATEPART(year, @dataAtual)) )
		IF @qtCalendario = 0 and (DATEPART(weekday, @dataAtual) <> 1) and (DATEPART(weekday, @dataAtual) <> 6)
		  SET @qtRepeticoes = @qtRepeticoes - 1;
	END
	
	RETURN @dataAtual;
END