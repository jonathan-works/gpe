package br.com.infox.epp.processo.documento.service;

import java.util.List;

import br.com.infox.epp.documento.entity.DocumentoBinario;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

public abstract class DocumentoBinWrapper {
    
    private DocumentoBin documentoBin;

    public abstract List<AssinaturaDocumento> carregarAssinaturas();
	
	public abstract DocumentoBinario carregarDocumentoBinario();

	public abstract Integer getSize();

	public abstract boolean existeBinario();

	public abstract String getHash();
	
	public DocumentoBinWrapper setDocumentoBin(DocumentoBin documentoBin) {
        this.documentoBin = documentoBin;
        return this;
    }
	
	public DocumentoBin getDocumentoBin() {
        return documentoBin;
    }
}
