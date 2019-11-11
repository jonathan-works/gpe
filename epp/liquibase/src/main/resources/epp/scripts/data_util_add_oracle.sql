CREATE OR REPLACE FUNCTION DataUtilAdd(tipo IN VARCHAR2, qtRepeticoes IN NUMBER, dataAtual IN DATE) RETURN DATE AS 

	dataAgora DATE;
	qtCalendario NUMBER;
	adding NUMBER;
	repeticoes NUMBER;
BEGIN
	dataAgora := dataAtual;
	adding := qtRepeticoes;
	repeticoes := qtRepeticoes;
	IF adding < 0 THEN
		repeticoes := qtRepeticoes * -1;
	END IF;
	
	WHILE (repeticoes > 0) LOOP
	  IF tipo = 'year' THEN
	    IF adding > 0 THEN
	      dataAgora := dataAgora + INTERVAL '1' year;	
	    ELSE
	      dataAgora := dataAgora - INTERVAL '1' year;
	    END IF;
	  ELSIF tipo = 'month' THEN
	    IF adding > 0 THEN
	      dataAgora := dataAgora + INTERVAL '1' month;	
	    ELSE
	      dataAgora := dataAgora - INTERVAL '1' month;
	    END IF;
	  ELSE
	    IF adding > 0 THEN
	      dataAgora := dataAgora + INTERVAL '1' day;	
	    ELSE
	      dataAgora := dataAgora - INTERVAL '1' day;
	    END IF;
	  END IF;

	  select count(*) into qtCalendario from tb_calendario_eventos ce where ce.dt_inicio = dataAgora;
	    
	  IF  qtCalendario = 0 and (to_char(dataAgora, 'D') <> '1') and (to_char(dataAgora, 'D') <> '7') THEN
	     repeticoes := repeticoes - 1;
	  END IF;
	END LOOP;

   RETURN dataAgora;
END DataUtilAdd;