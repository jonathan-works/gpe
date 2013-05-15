package br.com.infox.epp.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(ClassificacaoDocumentoDAO.NAME)
@AutoCreate
public class ClassificacaoDocumentoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "classificacaoDocumentoDAO";
	
}
