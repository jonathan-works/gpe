CREATE OR REPLACE FUNCTION DataAdd(tipo VARCHAR(5), qtRepeticoes INT, dataAtual DATE) RETURNS DATE AS $$
DECLARE dataAgora DATE;
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
	  qtRepeticoes := qtRepeticoes - 1;
	END LOOP;

   RETURN dataAgora;
END;
$$ LANGUAGE plpgsql;