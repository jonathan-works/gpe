package br.com.infox.epp.documento.query;

public interface ExtensaoArquivoQuery {
    
    String LIMITE_EXTENSAO = "limiteDeExtensao";
    String CLASSIFICACAO_PARAM = "classificacao";
    String EXTENSAO_PARAM = "extensao";
    String LIMITE_EXTENSAO_QUERY = "select o.tamanho from ExtensaoArquivo o "
            + "where o.tipoProcessoDocumento = :" + CLASSIFICACAO_PARAM +" and o.extensao =:" + EXTENSAO_PARAM;

}
