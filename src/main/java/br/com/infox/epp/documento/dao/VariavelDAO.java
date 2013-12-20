package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.VariavelQuery.PARAM_TIPO;
import static br.com.infox.epp.documento.query.VariavelQuery.VARIAVEL_BY_TIPO_MODELO_DOCUMENTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;

@Name(VariavelDAO.NAME)
@AutoCreate
public class VariavelDAO extends GenericDAO {

	public static final String NAME = "variavelDAO";
	private static final long serialVersionUID = 1L;
	
	public List<Variavel> getVariaveisByTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento){
	    Map<String, Object> parameters = new HashMap<>();
	    parameters.put(PARAM_TIPO, tipoModeloDocumento);
	    return getNamedResultList(VARIAVEL_BY_TIPO_MODELO_DOCUMENTO, parameters);
	}

}
