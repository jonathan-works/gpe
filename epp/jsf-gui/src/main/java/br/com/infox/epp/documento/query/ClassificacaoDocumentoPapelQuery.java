package br.com.infox.epp.documento.query;

public interface ClassificacaoDocumentoPapelQuery {

	String PARAM_CLASSIFICACAO_DOCUMENTO = "classificacaoDocumento";
    String PARAM_PAPEL = "papel";
	
	String PAPEL_PODE_ASSINAR_CLASSIFICACAO = "AssinaturaDocumento.papelPodeAssinarClassificacao";
    String PAPEL_PODE_ASSINAR_CLASSIFICACAO_QUERY = "select 1 from ClassificacaoDocumentoPapel o "
    		+ "where o.tipoAssinatura <> 'P' and o.classificacaoDocumento = :" + PARAM_CLASSIFICACAO_DOCUMENTO
    		+ " and o.papel = :" + PARAM_PAPEL;
    
    String CLASSIFICACAO_EXIGE_ASSINATURA = "ClassificacaoDocumentoPapel.classificacaoExigeAssinatura";
    String CLASSIFICACAO_EXIGE_ASSINATURA_QUERY = "select 1 from ClassificacaoDocumentoPapel o where"
    		+ " o.classificacaoDocumento = :" + PARAM_CLASSIFICACAO_DOCUMENTO
    		+ " and o.tipoAssinatura not in ('F', 'P')";

    String GET_BY_PAPEL_AND_CLASSIFICACAO = "ClassificacaoDocumentoPapel.papelPodeRedigirClassificacao";
    String GET_BY_PAPEL_AND_CLASSIFICACAO_QUERY = "select o from ClassificacaoDocumentoPapel o "
            + "where o.classificacaoDocumento = :" + PARAM_CLASSIFICACAO_DOCUMENTO + " "
				+ "and o.papel = :" + PARAM_PAPEL;
}
