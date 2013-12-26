package br.com.infox.epp.access.dao;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.*;

@Name(UsuarioLocalizacaoDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class UsuarioLocalizacaoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioLocalizacaoDAO";
	
	public boolean existeUsuarioLocalizacao(UsuarioLocalizacao usuarioLocalizacao) {
		StringBuilder hql = new StringBuilder(EXISTE_USUARIO_LOCALIZACAO_QUERY);
		if (usuarioLocalizacao.getEstrutura() != null) {
			hql.append(ESTRUTURA_CONDITION);
		} else {
			hql.append(ESTRUTURA_NULL_CONDITION);
		}
		
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter(PARAM_USUARIO, usuarioLocalizacao.getUsuario())
			.setParameter(PARAM_PAPEL, usuarioLocalizacao.getPapel())
			.setParameter(PARAM_LOCALIZACAO, usuarioLocalizacao.getLocalizacao());
		if (usuarioLocalizacao.getEstrutura() != null) {
			query.setParameter(PARAM_ESTRUTURA, usuarioLocalizacao.getEstrutura());
		}
		
		return (Long) query.getSingleResult() > 0;
	}
	
	@Override
	public <T> T persist(T object) throws DAOException {
		if (object instanceof UsuarioLocalizacao) {
			UsuarioLocalizacao usuarioLocalizacao = (UsuarioLocalizacao) object;
			if (existeUsuarioLocalizacao(usuarioLocalizacao)) {
				throw new DAOException("#{messages['constraintViolation.uniqueViolation']}");
			}
		}
		return super.persist(object);
	}
}
