package br.com.infox.epp.documento.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.infox.epp.processo.documento.AssinaturaDto;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;

public class DocumentoResourceImpl implements DocumentoResource {

    @Inject
    private DocumentoRestService documentoRestService;
    @Inject
    private DocumentoBinManager documentoBinManager;

    private UUID uuid;

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

    @Override
    public byte[] getBinario() {
        DocumentoBin documentoBin = documentoBinManager.getByUUID(uuid);
        if (documentoBin == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return documentoBin.getDocumentoBinWrapper().carregarDocumentoBinario().getDocumentoBinario();
    }
    
    @Override
    public List<AssinaturaDto> getAssinaturas() {
        DocumentoBin documentoBin = documentoBinManager.getByUUID(uuid);
        if (documentoBin == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        List<AssinaturaDto> assinaturaDtos = new ArrayList<>();
        List<AssinaturaDocumento> assinaturas = documentoBin.getDocumentoBinWrapper().carregarAssinaturas();
        for (AssinaturaDocumento assinaturaDocumento : assinaturas) {
            assinaturaDtos.add(new AssinaturaDto(assinaturaDocumento));
        }
        return assinaturaDtos;
    }
   
}
