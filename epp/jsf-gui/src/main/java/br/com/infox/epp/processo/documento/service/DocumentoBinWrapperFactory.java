package br.com.infox.epp.processo.documento.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;

@Singleton
public class DocumentoBinWrapperFactory implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Map<String, Class<? extends DocumentoBinWrapper>> supportedTypes = new HashMap<>();
    
    @Inject
    private DocumentoBinManager documentoBinManager;
    
    @PostConstruct
    private void init() {
        registerSupportedTypes();
    }
    
    protected void registerSupportedTypes() {
        registerSupportedType(null, DatabaseDocumentoBinWrapper.class);
    }

    protected final void registerSupportedType(String key, Class<? extends DocumentoBinWrapper> wrapperClass) {
        supportedTypes.put(key, wrapperClass);
    }
    
    public final DocumentoBinWrapper createWrapperInstance(Integer idDocumentoBin) {
        DocumentoBin documentoBin = documentoBinManager.find(idDocumentoBin);
        return createWrapperInstance(documentoBin);
    }

    public final DocumentoBinWrapper createWrapperInstance(DocumentoBin documentoBin) {
        DocumentoBinWrapper documentoBinWrapper = Beans.getReference(getWrapperClass(documentoBin));
        return documentoBinWrapper.setDocumentoBin(documentoBin);
    }
    
    public boolean isRegisteredType(String key) {
        return supportedTypes.containsKey(key);
    }
    
    private Class<? extends DocumentoBinWrapper> getWrapperClass(DocumentoBin documentoBin) {
        Class<? extends DocumentoBinWrapper> wrapperClass = supportedTypes.get(documentoBin.getTipoDocumentoExterno());
        if (wrapperClass == null) {
            throw new IllegalArgumentException("Tipo de documento não suportado " + documentoBin.getTipoDocumentoExterno());
        }
        return wrapperClass;
    }
    
    public static DocumentoBinWrapperFactory getInstance() {
        return Beans.getReference(DocumentoBinWrapperFactory.class);
    }
    
}
