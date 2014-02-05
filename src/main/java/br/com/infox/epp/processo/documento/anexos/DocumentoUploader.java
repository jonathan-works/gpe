package br.com.infox.epp.processo.documento.anexos;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.itx.util.Crypto;

@Name(DocumentoUploader.NAME)
@Scope(ScopeType.CONVERSATION)
public class DocumentoUploader extends DocumentoCreator implements FileUploadListener {

    public static final String NAME = "documentoUploader";
    
    private static final LogProvider LOG = Logging.getLogProvider(DocumentoUploader.class);
    
    @In private ProcessoDocumentoManager processoDocumentoManager;
    @In private DocumentoBinManager documentoBinManager;

    @Override
    public void processFileUpload(FileUploadEvent fileUploadEvent) {
        newInstance();
        final UploadedFile ui = fileUploadEvent.getUploadedFile();
        bin().setUsuario(Authenticator.getUsuarioLogado());
        bin().setNomeArquivo(ui.getName());
        bin().setExtensao(getFileType(ui.getName()));
        bin().setMd5Documento(getMD5(ui.getData()));
        bin().setSize(Long.valueOf(ui.getSize()).intValue());
        bin().setProcessoDocumento(ui.getData());
        bin().setModeloDocumento(null);
        FacesMessages.instance().add(Messages.instance().get("processoDocumento.doneLabel"));
    }
    
    private ProcessoDocumentoBin bin(){
        if (getProcessoDocumento().getProcessoDocumentoBin() == null) {
            getProcessoDocumento().setProcessoDocumentoBin(new ProcessoDocumentoBin());
        }
        return getProcessoDocumento().getProcessoDocumentoBin();
    }
    
    private String getFileType(String nomeArquivo) {
        String ret = "";
        if (nomeArquivo != null) {
            ret = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1);
        }
        return ret;
    }
    
    private String getMD5(byte[] data) {
        return Crypto.encodeMD5(data);
    }
    
    @Override
    protected LogProvider getLogger() {
        return LOG;
    }

    @Override
    protected ProcessoDocumento gravarDocumento() throws DAOException {
        ProcessoDocumento pd = processoDocumentoManager.gravarDocumentoNoProcesso(getProcesso(), getProcessoDocumento());
        documentoBinManager.salvarBinario(getProcessoDocumento().getIdProcessoDocumento(), bin().getProcessoDocumento());
        return pd;
    }
    

}
