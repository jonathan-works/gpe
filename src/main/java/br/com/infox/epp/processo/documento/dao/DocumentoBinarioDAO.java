package br.com.infox.epp.processo.documento.dao;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.DocumentoBinario;

@AutoCreate
@Name(DocumentoBinarioDAO.NAME)
public class DocumentoBinarioDAO extends DAO<DocumentoBinario> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoBinarioDAO";

    @In
    private transient EntityManager entityManagerBin;

    @Override
    protected EntityManager getEntityManager() {
        return entityManagerBin;
    }

    public byte[] getData(int idDocumentoBin) {
        return find(idDocumentoBin).getDocumentoBinario();
    }

    /**
     * Grava o arquivo binário na base de arquivos Bin com o respctivo Id da
     * tabela ProcessoDocumentoBin.
     * 
     * @param idDocumentoBinario Id da Tabela
     * @param file Arquivo do tipo byte[]
     * @throws DAOException
     */
    public DocumentoBinario gravarBinario(int idDocumentoBinario, byte[] file) throws DAOException {
        return persist(createDocumentoBinario(idDocumentoBinario, file));
    }

    private DocumentoBinario createDocumentoBinario(int idDocumentoBinario, byte[] file) {
        DocumentoBinario documentoBinario = new DocumentoBinario();
        documentoBinario.setId(idDocumentoBinario);
        documentoBinario.setDocumentoBinario(file);
        return documentoBinario;
    }

}
