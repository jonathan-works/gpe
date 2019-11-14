package br.com.infox.epp.fluxo.definicao.modeler.configuracoes;

final class ConfiguracaoVariavelDocumento extends ConfiguracaoDocumento {
	boolean entrada;

	public ConfiguracaoVariavelDocumento(String nodeId, String label, String variavel, boolean entrada) {
	    super(nodeId, label, variavel);
	    this.entrada = entrada;
	}
	
    @Override
    protected String getPrefix() {
        return "Variable";
    }
}