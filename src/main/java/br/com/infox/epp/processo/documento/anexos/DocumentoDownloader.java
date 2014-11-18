package br.com.infox.epp.processo.documento.anexos;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(DocumentoDownloader.NAME)
public class DocumentoDownloader implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final float BYTES_IN_A_KILOBYTE = 1024f;

    @In
    private DocumentoBinarioManager documentoBinarioManager;
    @In
    private DocumentoManager documentoManager;

    public static final String NAME = "documentoDownloader";

    public void downloadDocumento(Documento documento) {
        DocumentoBin pdBin = documento.getDocumentoBin();
        byte[] data = documentoBinarioManager.getData(pdBin.getId());
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
    public String getFormattedKb(DocumentoBin binario) {
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
        downloadDocumento(documentoManager.find(Integer.valueOf(idDocumento)));
    }
}
