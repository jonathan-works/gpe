package br.com.infox.ibpm.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.ClassificacaoDocumentoDAO;

@Name(ClassificacaoDocumentoManager.NAME)
@AutoCreate
public class ClassificacaoDocumentoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "classificacaoDocumentoManager";
	
	@In private ClassificacaoDocumentoDAO classificacaoDocumentoDAO;

}
