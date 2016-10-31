package br.com.infox.epp.documento.rest;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class DocumentoResourceImpl implements DocumentoResource {

    private UUID uuid;
    @Inject
    private DocumentoRestService documentoRestService;

    private Response buildOtherResponse(DocumentoDownloadWrapper documentWrapper) {
        return Response.status(Status.OK).type(documentWrapper.getContentType()).entity(documentWrapper.getData())
                .build();
    }

    private Response buildPdfResponse(DocumentoDownloadWrapper documentWrapper) {
        return Response.status(Status.OK).type(documentWrapper.getContentType())
                .header("Content-disposition",
                        String.format("attachment; filename=\"%s\"", documentWrapper.getFileName()))
                .entity(documentWrapper.getData()).build();
    }

    @Override
    public Response getBinaryData() {
        DocumentoDownloadWrapper documentWrapper = documentoRestService.createDownloadDocumentWrapper(getUuid());
        if (documentWrapper.isPdf()) {
            return buildPdfResponse(documentWrapper);
        } else {
            return buildOtherResponse(documentWrapper);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

}
