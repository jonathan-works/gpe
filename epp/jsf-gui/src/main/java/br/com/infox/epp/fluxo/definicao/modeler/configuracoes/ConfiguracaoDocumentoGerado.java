package br.com.infox.epp.fluxo.definicao.modeler.configuracoes;

final class ConfiguracaoDocumentoGerado extends ConfiguracaoDocumento {
    private static final String PREFIX = "GeneratedDocument";
    
	public ConfiguracaoDocumentoGerado(String nodeId, String label, String codigoClassificacao) {
	    super(nodeId, label, nodeId + codigoClassificacao);
	}
	
	static boolean isGeneratedDocument(String id) {
	    return id.startsWith(PREFIX);
	}

    @Override
    protected String getPrefix() {
        return PREFIX;
    }
}