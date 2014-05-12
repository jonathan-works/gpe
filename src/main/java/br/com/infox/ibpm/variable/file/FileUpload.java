package br.com.infox.ibpm.variable.file;

import java.io.IOException;
import java.io.InputStream;

import javax.faces.component.UIComponent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.file.reader.InfoxPdfReader;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.FileUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.manager.TipoProcessoDocumentoManager;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.infox.ibpm.task.home.TaskInstanceHome;

@Name(FileUpload.NAME)
@Scope(ScopeType.EVENT)
public class FileUpload implements FileUploadListener {

    public static final String NAME = "fileUpload";
    private static final LogProvider LOG = Logging.getLogProvider(FileUpload.class);
    
    @In
    private ProcessoDocumentoManager processoDocumentoManager;
    @In
    private GenericManager genericManager;
    @In
    private ProcessoDocumentoBinManager processoDocumentoBinManager;
    @In
    private TipoProcessoDocumentoManager tipoProcessoDocumentoManager;
    
    @Override
    public void processFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getUploadedFile();
        UIComponent uploadFile = event.getComponent();
        ProcessoDocumento processoDocumento = createDocumento(file);
        try {
            processoDocumentoManager.gravarDocumentoNoProcesso(ProcessoHome.instance().getInstance(), processoDocumento);
            TaskInstanceHome.instance().getInstance().put(uploadFile.getId(), processoDocumento.getIdProcessoDocumento());
        } catch (DAOException e) {
            LOG.error("Não foi possível gravar o documento " + file.getName() + "no processo " + ProcessoHome.instance().getInstance().getIdProcesso(), e);
        }
        TaskInstanceHome.instance().update();
    }
    
    private ProcessoDocumento createDocumento(final UploadedFile file) {
        ProcessoDocumento pd = new ProcessoDocumento();
        pd.setProcessoDocumento(file.getName());
        pd.setAnexo(true);
        pd.setProcessoDocumentoBin(createDocumentoBin(file));
        pd.setTipoProcessoDocumento(tipoProcessoDocumentoManager.getClassificaoParaAcessoDireto());
        return pd;
    }

    private ProcessoDocumentoBin createDocumentoBin(final UploadedFile file) {
        ProcessoDocumentoBin pdb = new ProcessoDocumentoBin();
        pdb.setUsuario(Authenticator.getUsuarioLogado());
        pdb.setNomeArquivo(file.getName());
        pdb.setExtensao(FileUtil.getFileType(file.getName()));
        pdb.setMd5Documento(MD5Encoder.encode(file.getData()));
        pdb.setSize(Long.valueOf(file.getSize()).intValue());
        pdb.setProcessoDocumento(file.getData());
        pdb.setModeloDocumento(getTextoIndexavel(file));
        return pdb;
    }
    
    private String getTextoIndexavel(final UploadedFile file) {
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
            return InfoxPdfReader.readPdfFromInputStream(inputStream);
        } catch (IOException exception) {
            LOG.error("Não foi possível recuperar o inputStream do arquivo carregado", exception);
        }
        return null;
    }
    
}
