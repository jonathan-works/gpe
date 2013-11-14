package br.com.infox.epp.documento.dao;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.TipoModeloDocumentoPapel;
import br.com.infox.util.constants.WarningConstants;

@Name(TipoModeloDocumentoPapelDAO.NAME)
@AutoCreate
public class TipoModeloDocumentoPapelDAO extends GenericDAO {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoModeloDocumentoPapelDAO";
	
	/**
	 * Retorna uma lista com os tipos de modelo de documento que o perfil (localização+papel)
	 * do usuário logado possue permissão para acessar.
	 * */
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<TipoModeloDocumentoPapel> getTiposModeloDocumentoPermitidos(){
		String hql = "select t from TipoModeloDocumentoPapel tmdp " +
				"join tmdp.tipoModeloDocumento t " +
				"where tmdp.papel = :papel " +
				"order by t.tipoModeloDocumento";
		return entityManager.createQuery(hql)
			.setParameter("papel", Authenticator.getUsuarioLocalizacaoAtual()
			.getPapel()).getResultList();
	}

}
