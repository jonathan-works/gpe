package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.Papel;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.PapelDAO;
import br.com.infox.ibpm.entity.TipoModeloDocumento;

@Name(PapelManager.NAME)
@AutoCreate
public class PapelManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "papelManager";
	
	@In private PapelDAO papelDAO;
	
	public List<Papel> getPapeisNaoAssociadosATipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento){
		return papelDAO.getPapeisNaoAssociadosATipoModeloDocumento(tipoModeloDocumento);
	}
	
	public Papel getPapelByIdentificador(String identificador){
		return papelDAO.getPapelByIndentificador(identificador);
	}

}
