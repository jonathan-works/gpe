CREATE OR REPLACE FUNCTION NumeroProcessoRoot(idProcessoParam BIGINT) RETURNS VARCHAR(30) AS $$
DECLARE numeroProcessoRoot VARCHAR(30);
DECLARE processoPai BIGINT;
BEGIN
	numeroProcessoRoot := (select p.nr_processo from tb_processo p where p.id_processo = $1);
	processoPai := (select p.id_processo_pai from tb_processo p where p.id_processo = $1);

	WHILE (processoPai is not null) LOOP
		numeroProcessoRoot := (select p.nr_processo from tb_processo p where p.id_processo = processoPai);
		processoPai := (select p.id_processo_pai from tb_processo p where p.id_processo = processoPai);
	END LOOP;

	RETURN numeroProcessoRoot;
END;
$$ LANGUAGE plpgsql;