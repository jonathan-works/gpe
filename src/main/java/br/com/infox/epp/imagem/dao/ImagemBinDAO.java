package br.com.infox.epp.imagem.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.FileUtil;
import br.com.infox.epp.imagem.entity.ImagemBin;
import br.com.infox.epp.imagem.query.ImagemBinQuery;

@Name(ImagemBinDAO.NAME)
@AutoCreate
public class ImagemBinDAO extends DAO<ImagemBin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "imagemBinDAO";
    
    @In
    private transient EntityManager entityManagerBin;
    
    @Override
    public EntityManager getEntityManager() {
        return entityManagerBin;
    }

    public List<ImagemBin> getTodasAsImagens() {
        return getNamedResultList(ImagemBinQuery.LIST_IMAGENS);
    }

    public void persistImageBin(ImagemBin imagemBin) throws DAOException {
        persist(imagemBin);
    }

    /**
     * Metodo que recebe um array de bytes e um File indicando o destino e salva
     * os bytes no arquivo de destino.
     * 
     * @param bytesOrigem
     * @param fileDestino
     * @throws IOException
     */
    public void saveFile(byte[] bytesOrigem, File fileDestino) throws IOException {
        fileDestino.createNewFile();
        OutputStream out = null;
        try {
            out = new FileOutputStream(fileDestino);
            out.write(bytesOrigem);
            out.flush();
        } finally {
            FileUtil.close(out);
        }
    }

}
