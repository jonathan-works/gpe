package br.com.infox.epp.processo.documento.anexos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;

@Name(DocumentoDownloader.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class DocumentoDownloader {

    private static final float BYTES_IN_A_KILOBYTE = 1024f;

    @In
    DocumentoBinManager documentoBinManager;

    @In
    private ProcessoDocumentoManager processoDocumentoManager;

    public static final String NAME = "documentoDownloader";

    public void downloadDocumento(ProcessoDocumento documento) {
        ProcessoDocumentoBin pdBin = documento.getProcessoDocumentoBin();
        byte[] data = documentoBinManager.getData(documento.getIdProcessoDocumento());
        String fileName = pdBin.getNomeArquivo();
        String contentType = "application/" + pdBin.getExtensao();
        FileDownloader.download(data, contentType, fileName);
    }

    /**
     * Recebe o número de bytes e retorna o número em Kb (kilobytes).
     * 
     * @param bytes número em bytes
     * @return número em kilobytes
     */
    public String getFormattedKb(ProcessoDocumentoBin binario) {
        Integer bytes = binario.getSize();
        if (bytes != null && bytes > 0) {
            NumberFormat formatter = DecimalFormat.getNumberInstance(new Locale("pt", "BR"));
            formatter.setMinimumIntegerDigits(1);
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
            float kbytes = bytes / BYTES_IN_A_KILOBYTE;
            return formatter.format(kbytes) + " Kb";
        } else {
            return null;
        }
    }

    public void downloadDocumento(String idDocumento) {
        downloadDocumento(processoDocumentoManager.find(Integer.valueOf(idDocumento)));
    }
}
