package br.com.infox.epp.processo.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.dao.ProcessoDocumentoBinDAO;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.itx.util.Crypto;

@Name(ProcessoDocumentoBinManager.NAME)
@AutoCreate
public class ProcessoDocumentoBinManager extends Manager<ProcessoDocumentoBinDAO, ProcessoDocumentoBin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoDocumentoBinManager";
    
    public ProcessoDocumentoBin createProcessoDocumentoBin(ProcessoDocumento processoDocumento) throws DAOException{
        ProcessoDocumentoBin bin = processoDocumento.getProcessoDocumentoBin();
        bin.setUsuario(Authenticator.getUsuarioLogado());
        if (bin.getMd5Documento() == null) {
            bin.setMd5Documento(Crypto.encodeMD5(processoDocumento.getProcessoDocumentoBin().getModeloDocumento()));
        }
        return persist(bin);
    }
}
