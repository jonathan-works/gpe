package br.com.infox.epp.fluxo.definicao.modeler.configuracoes;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

abstract class ConfiguracaoDocumento {
    protected String nodeId;
    protected String label;
    protected String dataObjectId;
    protected String dataObjectReferenceId;
    
    public ConfiguracaoDocumento(String nodeId, String label, String baseId) {
        this.nodeId = nodeId;
        this.label = label;
        this.dataObjectId = generateDataObjectId(baseId);
        this.dataObjectReferenceId = generateDataObjectReferenceId(baseId);
    }
    
    private String generateDataObjectReferenceId(String baseId) {
        return getPrefix() + "_" + DigestUtils.sha1Hex(baseId);
    }
    
    private String generateDataObjectId(String baseId) {
        return getPrefix() + "_DataObject_" + DigestUtils.sha1Hex(baseId);
    }
    
    static <T extends ConfiguracaoDocumento> boolean contains(List<T> list, String dataObjectReferenceId) {
        for (ConfiguracaoDocumento config : list) {
            if (config.dataObjectReferenceId.equals(dataObjectReferenceId)) {
                return true;
            }
        }
        return false;
    }
    
    protected abstract String getPrefix();
}