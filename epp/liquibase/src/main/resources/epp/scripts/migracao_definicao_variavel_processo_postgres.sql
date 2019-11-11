DO $$
DECLARE fluxo record;
DECLARE variavel record;
DECLARE ordem int;
BEGIN
	FOR fluxo IN SELECT DISTINCT id_fluxo FROM tb_definicao_variavel_processo
	LOOP
		ordem := 0;
		FOR variavel IN 
		SELECT id_definicao_variavel_processo FROM tb_definicao_variavel_processo d WHERE d.id_fluxo = fluxo.id_fluxo AND
		EXISTS(SELECT 1 FROM tb_def_var_proc_recurso dv WHERE dv.id_definicao_variavel_processo = d.id_definicao_variavel_processo)
		ORDER BY nr_ordem
		LOOP
			UPDATE tb_def_var_proc_recurso d SET nr_ordem = ordem
			WHERE d.id_definicao_variavel_processo = variavel.id_definicao_variavel_processo;
			
			ordem := ordem + 1;
		END LOOP;
	END LOOP;
END
$$;
