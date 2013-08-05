package br.com.infox.epp.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.entity.ImagemBin;
import br.com.itx.util.FileUtil;

@Name(ImagemBinDAO.NAME)
@AutoCreate
public class ImagemBinDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "imagemBinDAO";
	
	@SuppressWarnings("unchecked")
	public List<ImagemBin> getTodasAsImagens(){
		String hql = "select o from ImagemBin o";
		return (List<ImagemBin>) entityManager.createQuery(hql).getResultList();
	}

    public void persistImageBin(ImagemBin imagemBin, File fileDestino) throws IOException {
        persist(imagemBin);
        saveFile(imagemBin.getImagem(), fileDestino);
    }
    
    /**
     * Metodo que recebe um array de bytes e um File indicando o destino e salva
     * os bytes no arquivo de destino.
     * @param bytesOrigem TODO
     * @param fileDestino TODO
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
