package br.com.infox.epp.processo.documento.anexos;

import java.util.UUID;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;

@Name(DocumentoValidator.NAME)
@Scope(ScopeType.SESSION)
@AutoCreate
public class DocumentoValidator {
    public static final String NAME = "documentoValidator";
    
    private String uuid;
    
    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private DocumentoBinarioManager documentoBinarioManager;
    @In
    private DocumentoDownloader documentoDownloader;
    
    private int tentativas;
    private boolean acessoBloqueado;
    
    public String getUuid() {
        return uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public void download() {
        DocumentoBin pdBin;
        try {
            pdBin = documentoBinManager.getByUUID(UUID.fromString(uuid));
        } catch (IllegalArgumentException e) {
            FacesMessages.instance().add("Código inválido");
            return;
        }
        if (pdBin == null) {
            FacesMessages.instance().add("Código inválido");
            return;
        }
        if (documentoBinarioManager.existeBinario(pdBin.getId())) {
            if (pdBin.getDocumentoList() == null || pdBin.getDocumentoList().isEmpty()) {
                documentoDownloader.downloadDocumento(pdBin, true);
                //Baixa o arquivo original caso não seja possível gerar as margens
                if(documentoDownloader.isErroMargem()) {
                    documentoDownloader.downloadDocumento(pdBin, false);                	
                }
            } else {
                documentoDownloader.downloadDocumento(pdBin.getDocumentoList().get(0), true);
                //Baixa o arquivo original caso não seja possível gerar as margens
                if(documentoDownloader.isErroMargem()) {
                    documentoDownloader.downloadDocumento(pdBin.getDocumentoList().get(0), false);                	
                }
            }
        } else if (!pdBin.isBinario()) {
            FileDownloader.download(pdBin.getModeloDocumento().getBytes(), "text/html", pdBin.getNomeArquivo());
        }
        tentativas = 0;
    }
    
    public void incrementTries() {
        tentativas++;
        if (tentativas == 3) {
            acessoBloqueado = true;
        }
    }
 
    public boolean isAcessoBloqueado() {
        return acessoBloqueado;
    }
    
    public int getTentativas() {
        return tentativas;
    }
}
