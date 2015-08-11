package br.com.infox.epp.certificado;

import java.util.UUID;

import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

public class DefaultSignableDocumentImpl implements SignableDocument {

    private String md5;
    private UUID uuid;

    public DefaultSignableDocumentImpl(Documento documento) {
        DocumentoBin bin = documento.getDocumentoBin();
        this.md5 = bin.getMd5Documento();
        this.uuid = bin.getUuid();
    }

    public DefaultSignableDocumentImpl(DocumentoBin documentoBin) {
        this.md5 = documentoBin.getMd5Documento();
        this.uuid = documentoBin.getUuid();
    }

    @Override
    public String getMD5() {
        return this.md5;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }
}
