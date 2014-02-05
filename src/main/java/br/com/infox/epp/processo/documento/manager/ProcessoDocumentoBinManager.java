package br.com.infox.epp.processo.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.dao.ProcessoDocumentoBinDAO;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.itx.component.FileHome;
import br.com.itx.util.Crypto;

@Name(ProcessoDocumentoBinManager.NAME)
@AutoCreate
public class ProcessoDocumentoBinManager extends Manager<ProcessoDocumentoBinDAO, ProcessoDocumentoBin> {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_MAXIMO_ARQUIVO = 1572864;
    public static final String NAME = "processoDocumentoBinManager";
    
    public boolean isDocumentoBinValido(FileHome file) {
        if (file == null) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Nenhum documento selecionado.");
            return false;
        }
        if (!"PDF".equalsIgnoreCase(file.getFileType())) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "O documento deve ser do tipo PDF.");
            return false;
        }
        if (file.getSize() != null && file.getSize() > TAMANHO_MAXIMO_ARQUIVO) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "O documento deve ter o tamanho máximo de 1.5MB!");
            return false;
        }
        return true;
    }
    
    public ProcessoDocumentoBin createProcessoDocumentoBin(ProcessoDocumento processoDocumento) throws DAOException{
        ProcessoDocumentoBin bin = processoDocumento.getProcessoDocumentoBin();
        bin.setUsuario(Authenticator.getUsuarioLogado());
        if (bin.getMd5Documento() == null) {
            bin.setMd5Documento(Crypto.encodeMD5(processoDocumento.getProcessoDocumentoBin().getModeloDocumento()));
        }
        return persist(bin);
    }
}
