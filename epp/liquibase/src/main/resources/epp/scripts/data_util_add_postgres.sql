CREATE OR REPLACE FUNCTION DataUtilAdd(tipo VARCHAR(5), qtRepeticoes INT, dataAtual DATE) RETURNS DATE AS $$
DECLARE dataAgora DATE;
DECLARE qtCalendario INT;
DECLARE adding INT;
BEGIN
	adding := $2;
	dataAgora := $3;
	IF adding < 0 THEN
		qtRepeticoes := qtRepeticoes * -1;
	END IF;
	
	WHILE (qtRepeticoes > 0) LOOP
	  IF tipo = 'year' THEN
	    IF adding > 0 THEN
	      dataAgora := dataAgora + INTERVAL '1 year';	
	    ELSE
	      dataAgora := dataAgora - INTERVAL '1 year';
	    END IF;
	  ELSEIF tipo = 'month' THEN
	    IF adding > 0 THEN
	      dataAgora := dataAgora + INTERVAL '1 month';	
	    ELSE
	      dataAgora := dataAgora - INTERVAL '1 month';
	    END IF;
	  ELSE
	    IF adding > 0 THEN
	      dataAgora := dataAgora + INTERVAL '1 day';	
	    ELSE
	      dataAgora := dataAgora - INTERVAL '1 day';
	    END IF;
	  END IF;
	    
	  qtCalendario := (select count(*) from tb_calendario_eventos ce 
			  where (ce.dt_dia = date_part('day', dataAgora) and ce.dt_mes = date_part('month', dataAgora) and ce.dt_ano is null) 
			  or (ce.dt_dia = date_part('day', dataAgora) and ce.dt_mes = date_part('month', dataAgora) and ce.dt_ano = date_part('year', dataAgora)) );
	  IF  qtCalendario = 0 and (date_part('isodow', dataAgora) <> 6) and (date_part('isodow', dataAgora) <> 7) THEN
	     qtRepeticoes := qtRepeticoes - 1;
	  END IF;
	END LOOP;

   RETURN dataAgora;
END;
$$ LANGUAGE plpgsql;