package br.com.infox.epp.processo.documento.component;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.documento.home.DocumentoBinHome;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.itx.component.FileHome;

@Name(ProcessoDocumentoDownloader.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ProcessoDocumentoDownloader {

    private static final String PAGINA_DOWNLOAD = "/download.xhtml";
    public static final String NAME = "processoDocumentoDownloader";
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoDocumentoDownloader.class);

    public String setDownloadInstance(ProcessoDocumento doc) {
        exportData(doc.getProcessoDocumentoBin());
        return PAGINA_DOWNLOAD;
    }
    
    public void exportData(ProcessoDocumentoBin doc) {
        FileHome file = FileHome.instance();
        file.setFileName(doc.getNomeArquivo());
        try {
            file.setData(DocumentoBinHome.instance().getData(doc.getIdProcessoDocumentoBin()));
        } catch (Exception e) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Erro ao descarregar o documento.");
            LOG.error(".exportData()", e);
        }
        Contexts.getConversationContext().set("fileHome", file);
    }

}
