package br.com.infox.epp.documento.dao;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;

@Name(VariavelDAO.NAME)
@AutoCreate
public class VariavelDAO extends GenericDAO {

	public static final String NAME = "variavelDAO";
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<Variavel> getVariaveisByTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento){
		String hql = "select o from Variavel o join o.variavelTipoModeloList tipos where tipos.tipoModeloDocumento = :tipo";
		return (List<Variavel>) getEntityManager().createQuery(hql).setParameter("tipo", tipoModeloDocumento).getResultList();
	}

}
