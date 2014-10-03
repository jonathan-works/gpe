package br.com.infox.epp.processo.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.dao.DocumentoBinDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

@AutoCreate
@Name(DocumentoBinManager.NAME)
public class DocumentoBinManager extends Manager<DocumentoBinDAO, DocumentoBin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoBinManager";
    
    public DocumentoBin createProcessoDocumentoBin(
            Documento documento) throws DAOException {
        DocumentoBin bin = documento.getDocumentoBin();
        if (bin.getMd5Documento() == null) {
            bin.setMd5Documento(MD5Encoder.encode(documento.getDocumentoBin().getModeloDocumento()));
        }
        return persist(bin);
    }
    
    public DocumentoBin createProcessoDocumentoBin(final String tituloDocumento, final String conteudo) throws DAOException {
        DocumentoBin bin = new DocumentoBin();
        bin.setNomeArquivo(tituloDocumento);
        bin.setModeloDocumento(conteudo);
        bin.setMd5Documento(MD5Encoder.encode(conteudo));
        return persist(bin);
    }
}
