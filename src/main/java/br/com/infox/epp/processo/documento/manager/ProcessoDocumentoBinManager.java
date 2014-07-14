package br.com.infox.epp.processo.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.dao.ProcessoDocumentoBinDAO;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;

@Name(ProcessoDocumentoBinManager.NAME)
@AutoCreate
public class ProcessoDocumentoBinManager extends Manager<ProcessoDocumentoBinDAO, ProcessoDocumentoBin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoDocumentoBinManager";

    public ProcessoDocumentoBin createProcessoDocumentoBin(
            ProcessoDocumento processoDocumento) throws DAOException {
        ProcessoDocumentoBin bin = processoDocumento.getProcessoDocumentoBin();
        if (bin.getMd5Documento() == null) {
            bin.setMd5Documento(MD5Encoder.encode(processoDocumento.getProcessoDocumentoBin().getModeloDocumento()));
        }
        return persist(bin);
    }
}
