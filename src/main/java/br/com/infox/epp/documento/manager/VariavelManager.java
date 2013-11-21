package br.com.infox.epp.documento.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;

@Name(VariavelManager.NAME)
@AutoCreate
public class VariavelManager extends GenericManager {

	public static final String NAME = "variavelManager";
	private static final long serialVersionUID = 1L;
	
	@In private VariavelDAO variavelDAO;
	
	public List<Variavel> getVariaveisByTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento){
		return variavelDAO.getVariaveisByTipoModeloDocumento(tipoModeloDocumento);
	}

}
