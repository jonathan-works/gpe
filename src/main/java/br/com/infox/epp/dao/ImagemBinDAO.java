package br.com.infox.epp.dao;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.entity.ImagemBin;

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

}
