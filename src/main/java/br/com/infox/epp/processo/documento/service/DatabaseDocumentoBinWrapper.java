package br.com.infox.epp.processo.documento.service;

import java.util.List;

import javax.inject.Inject;

import br.com.infox.epp.documento.entity.DocumentoBinario;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.dao.DocumentoBinarioDAO;

public class DatabaseDocumentoBinWrapper extends DocumentoBinWrapper {
    
    @Inject
    protected DocumentoBinarioDAO documentoBinarioDAO;
    
    @Override
    public DocumentoBinario carregarDocumentoBinario() {
        return documentoBinarioDAO.find(getDocumentoBin().getId());
    }
    
    @Override
    public List<AssinaturaDocumento> carregarAssinaturas() {
        return getDocumentoBin().getAssinaturasAtributo();
    }

    @Override
    public Integer getSize() {
        return getDocumentoBin().getSizeAtributo();
    }

    @Override
    public boolean existeBinario() {
        return documentoBinarioDAO.existeBinario(getDocumentoBin().getId());
    }

    @Override
    public String getHash() {
        return getDocumentoBin().getMd5DocumentoAtributo();
    }

}
