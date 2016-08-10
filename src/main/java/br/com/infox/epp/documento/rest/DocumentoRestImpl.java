package br.com.infox.epp.documento.rest;

import java.util.UUID;

import javax.inject.Inject;

public class DocumentoRestImpl implements DocumentoRest {

    @Inject
    private DocumentoResourceImpl documentoResourceImpl;
    
    @Override
    public DocumentoResource getBinaryData(UUID uuid) {
        documentoResourceImpl.setUuid(uuid);
        return documentoResourceImpl;
    }


}
