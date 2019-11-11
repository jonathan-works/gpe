package br.com.infox.epp.documento.rest;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.core.rest.RestThreadPool;
import br.com.infox.epp.documento.DocumentoBinSearch;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;

@Stateless
public class DocumentoRestService {
    
    @Inject
    private DocumentoBinSearch documentoRestSearch;
    @Inject
    private DocumentoBinManager documentoBinManager;
    @Inject
    private DocumentoBinarioManager documentoBinarioManager;

    private DocumentoDownloadWrapper buildOtherResponse(String contentType,DocumentoBin documento){
        byte[] downloadData = documentoBinarioManager.getData(documento.getId());
        DocumentoDownloadWrapper wrapper = new DocumentoDownloadWrapper();
        wrapper.setContentType(contentType);
        wrapper.setData(downloadData);
        wrapper.setPdf(false);
        return wrapper;
    }
    
    private DocumentoDownloadWrapper buildPdfResponse(String contentType,DocumentoBin documento, String fileName){
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        documentoBinManager.writeMargemDocumento(documento, documentoBinarioManager.getData(documento.getId()), outStream);
        byte[] downloadData = outStream.toByteArray();
        DocumentoDownloadWrapper wrapper = new DocumentoDownloadWrapper();
        wrapper.setContentType(contentType);
        wrapper.setFileName(fileName);
        wrapper.setData(downloadData);
        wrapper.setPdf(true);
        return wrapper;
    }
    
    @RestThreadPool
    public DocumentoDownloadWrapper createDownloadDocumentWrapper(UUID uuid){
        DocumentoBin documento = documentoRestSearch.getDocumentoPublicoByUUID(uuid);
        String contentType = "application/" + documento.getExtensao().toLowerCase();
        if ("application/pdf".equals(contentType)) {
            return buildPdfResponse(contentType, documento, documento.getNomeArquivo());
        } else {
            return buildOtherResponse(contentType, documento);
        }
    }
    
    
}
