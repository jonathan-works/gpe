package br.com.infox.epp.processo.documento.dao;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.DocumentoBin;

@Name(DocumentoBinDAO.NAME)
@AutoCreate
public class DocumentoBinDAO extends DAO<DocumentoBin, Integer> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoBinDAO";
    
    @In 
    private transient EntityManager entityManagerBin;
    
    @Override
    protected EntityManager getEntityManager() {
        return entityManagerBin;
    }

    public byte[] getData(int idDocumentoBin) {
        return find(idDocumentoBin).getDocumentoBin();
    }
    
    /**
     * Grava o arquivo binário na base de arquivos Bin com o respctivo Id da
     * tabela ProcessoDocumentoBin.
     * 
     * @param idDocumentoBin Id da Tabela
     * @param file Arquivo do tipo byte[]
     * @throws DAOException 
     */
    public DocumentoBin gravarBinario(int idDocumentoBin, byte[] file) throws DAOException {
        return persist(createDocumentoBin(idDocumentoBin, file));
    }

    private DocumentoBin createDocumentoBin(int idDocumentoBin, byte[] file) {
        DocumentoBin documentoBin = new DocumentoBin();
        documentoBin.setIdDocumentoBin(idDocumentoBin);
        documentoBin.setDocumentoBin(file);
        return documentoBin;
    }

}
