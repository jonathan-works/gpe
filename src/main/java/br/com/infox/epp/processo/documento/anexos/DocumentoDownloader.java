package br.com.infox.epp.processo.documento.anexos;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.file.FileDownloader;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;

@Name(DocumentoDownloader.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class DocumentoDownloader {

    @In
    DocumentoBinManager documentoBinManager;

    public static final String NAME = "documentoDownloader";

    public void downloadDocumento(ProcessoDocumento documento){
        ProcessoDocumentoBin pdBin= documento.getProcessoDocumentoBin();
        byte [] data = documentoBinManager.getData(pdBin.getIdProcessoDocumentoBin());
        String fileName = pdBin.getNomeArquivo();
        String contentType = "application/" + pdBin.getExtensao();
        FileDownloader.download(data, contentType, fileName);
    }

}
