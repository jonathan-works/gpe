CREATE OR REPLACE FUNCTION DocumentoSuficientementeAssinado(idDocumento bigint) RETURNS BOOLEAN AS $$
DECLARE suficientementeAssinado BOOLEAN := false;
DECLARE totalObrigatorias BIGINT;
BEGIN
	CREATE TEMP TABLE ClassificacaoDocumentoPapeis ON COMMIT DROP AS  
	SELECT CASE WHEN (SELECT DISTINCT 1 FROM tb_assinatura_documento a
	 					INNER JOIN tb_usuario_perfil up ON (up.id_usuario_perfil = a.id_usuario_perfil)
	 					INNER JOIN tb_perfil_template pt ON (pt.id_perfil_template = up.id_perfil_template)
	 					INNER JOIN tb_papel p ON (p.id_papel = pt.id_papel)
     					WHERE a.id_documento_bin = d.id_documento_bin AND p.id_papel = cdp.id_papel)
							IS NULL THEN false ELSE true END AS in_assinado,
					 cdp.tp_assinatura AS tp_assinatura
					 FROM tb_documento d
					 INNER JOIN tb_classificacao_documento_papel cdp ON (cdp.id_classificacao_documento = d.id_classificacao_documento)
					 WHERE d.id_documento = $1;

	IF (SELECT COUNT(*) FROM ClassificacaoDocumentoPapeis WHERE tp_assinatura = 'O' OR tp_assinatura = 'S') = 0 
	THEN
		suficientementeAssinado := true;
	ELSE
		BEGIN
			totalObrigatorias := (SELECT COUNT(*) FROM ClassificacaoDocumentoPapeis WHERE tp_assinatura = 'O');
			IF (totalObrigatorias = (SELECT COUNT(*) FROM ClassificacaoDocumentoPapeis WHERE tp_assinatura = 'O' AND in_assinado = true)) 
			THEN
				suficientementeAssinado := true;
			ELSE IF ((SELECT COUNT(*) FROM ClassificacaoDocumentoPapeis WHERE tp_assinatura = 'S' AND in_assinado = true) > 0 )
				THEN
		 		suficientementeAssinado := true;
			END IF;
			END IF;
		END;
	END IF;
	DROP TABLE ClassificacaoDocumentoPapeis;
	RETURN suficientementeAssinado;
END;
$$ LANGUAGE plpgsql; 