package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.ImagemBinDAO;
import br.com.infox.epp.entity.ImagemBin;

@Name(ImagemBinManager.NAME)
@AutoCreate
public class ImagemBinManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "imagemBinManager";
	
	@In private ImagemBinDAO imagemBinDAO;

	public List<ImagemBin> getTodasAsImagens(){
		return imagemBinDAO.getTodasAsImagens();
	}
	
}
