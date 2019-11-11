CREATE OR REPLACE FUNCTION DataAdd(tipo IN VARCHAR2, qtRepeticoes IN NUMBER, dataAtual IN DATE) RETURN DATE AS 

	dataAgora DATE;
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
      repeticoes := repeticoes - 1;
	END LOOP;

   RETURN dataAgora;
END DataAdd;