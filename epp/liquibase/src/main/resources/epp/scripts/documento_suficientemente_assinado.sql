CREATE FUNCTION DocumentoSuficientementeAssinado(@idDocumento bigint) RETURNS BIT AS
BEGIN
	DECLARE @suficientementeAssinado BIT;
	SET @suficientementeAssinado = 0;
	DECLARE @ClassificacaoDocumentoPapeis TABLE(in_assinado bit, tp_assinatura char(1));
	
	INSERT INTO @ClassificacaoDocumentoPapeis
	SELECT CASE WHEN (SELECT DISTINCT 1 FROM tb_assinatura_documento a
				 INNER JOIN tb_usuario_perfil up ON (up.id_usuario_perfil = a.id_usuario_perfil)
				 INNER JOIN tb_perfil_template pt ON (pt.id_perfil_template = up.id_perfil_template)
				 INNER JOIN tb_papel p ON (p.id_papel = pt.id_papel)
			     WHERE a.id_documento_bin = d.id_documento_bin AND p.id_papel = cdp.id_papel)
			IS NULL THEN 0 ELSE 1 END,
	cdp.tp_assinatura
	FROM tb_documento d
	INNER JOIN tb_classificacao_documento_papel cdp ON (cdp.id_classificacao_documento = d.id_classificacao_documento)
	WHERE d.id_documento = @idDocumento;

	IF (SELECT COUNT(*) FROM @ClassificacaoDocumentoPapeis WHERE tp_assinatura = 'O' OR tp_assinatura = 'S') = 0
		SET @suficientementeAssinado = 1;
	ELSE
	BEGIN
		DECLARE @totalObrigatorias INT;
		SET @totalObrigatorias = (SELECT COUNT(*) FROM @ClassificacaoDocumentoPapeis WHERE tp_assinatura = 'O');
		IF @totalObrigatorias = (SELECT COUNT(*) FROM @ClassificacaoDocumentoPapeis WHERE tp_assinatura = 'O' AND in_assinado = 1)
			SET @suficientementeAssinado = 1;
		ELSE IF (SELECT COUNT(*) FROM @ClassificacaoDocumentoPapeis WHERE tp_assinatura = 'S' AND in_assinado = 1) > 0
			SET @suficientementeAssinado = 1;
	END

	RETURN @suficientementeAssinado;
END
