package br.com.infox.epp.processo.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.dao.ProcessoDocumentoBinDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;

@AutoCreate
@Name(ProcessoDocumentoBinManager.NAME)
public class ProcessoDocumentoBinManager extends Manager<ProcessoDocumentoBinDAO, ProcessoDocumentoBin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoDocumentoBinManager";
    
    public ProcessoDocumentoBin createProcessoDocumentoBin(
            Documento documento) throws DAOException {
        ProcessoDocumentoBin bin = documento.getProcessoDocumentoBin();
        if (bin.getMd5Documento() == null) {
            bin.setMd5Documento(MD5Encoder.encode(documento.getProcessoDocumentoBin().getModeloDocumento()));
        }
        return persist(bin);
    }
    
    public ProcessoDocumentoBin createProcessoDocumentoBin(final String tituloDocumento, final String conteudo) throws DAOException {
        ProcessoDocumentoBin bin = new ProcessoDocumentoBin();
        bin.setNomeArquivo(tituloDocumento);
        bin.setModeloDocumento(conteudo);
        bin.setMd5Documento(MD5Encoder.encode(conteudo));
        return persist(bin);
    }
}
