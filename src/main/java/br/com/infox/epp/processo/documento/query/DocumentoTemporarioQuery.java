package br.com.infox.epp.processo.documento.query;

public interface DocumentoTemporarioQuery {

	String PARAM_PROCESSO = "processo";
	String PARAM_LOCALIZACAO = "localizacao";
    String PARAM_ORDER = "orderBy";
    String PARAM_ID_DOCUMENTO_TEMPORARIO = "idDocumentoTemporario";

    String LIST_BY_PROCESSO = "select distinct o from DocumentoTemporario o "
                + "inner join fetch o.classificacaoDocumento cd "
                + "inner join o.documentoBin bin "
                + "left join bin.assinaturas ass "
                + "left join ass.usuario u "
                + "left join u.pessoaFisica pf "
            + "where o.processo = :" + PARAM_PROCESSO + " "
        		+ "and o.localizacao = :" + PARAM_LOCALIZACAO + " "
    		+ "order by ";

    String LOAD_BY_ID = "loadById";
    String LOAD_BY_ID_QUERY = "select distinct o from DocumentoTemporario o "
            + "inner join o.classificacaoDocumento cd "
            + "inner join o.documentoBin bin "
            + "left join bin.assinaturas ass "
            + "left join ass.usuario u "
            + "left join u.pessoaFisica pf "
        + "where o.id = :" + PARAM_ID_DOCUMENTO_TEMPORARIO;
}
