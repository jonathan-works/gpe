package br.com.infox.ibpm.dao;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.ibpm.entity.Variavel;
import br.com.infox.util.constants.WarningConstants;

@Name(VariavelDAO.NAME)
@AutoCreate
public class VariavelDAO extends GenericDAO {

	public static final String NAME = "variavelDAO";
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<Variavel> getVariaveisByTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento){
		String hql = "select o from Variavel o join o.variavelTipoModeloList tipos where tipos.tipoModeloDocumento = :tipo";
		return (List<Variavel>) entityManager.createQuery(hql).setParameter("tipo", tipoModeloDocumento).getResultList();
	}

}
