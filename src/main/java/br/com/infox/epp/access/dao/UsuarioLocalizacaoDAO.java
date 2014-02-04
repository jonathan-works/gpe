package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.ESTRUTURA_CONDITION;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.ESTRUTURA_NULL_CONDITION;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.EXISTE_USUARIO_LOCALIZACAO_QUERY;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.PARAM_ESTRUTURA;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.PARAM_LOCALIZACAO;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.PARAM_PAPEL;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.PARAM_USUARIO;

import java.util.HashMap;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;

@Name(UsuarioLocalizacaoDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class UsuarioLocalizacaoDAO extends DAO<UsuarioLocalizacao> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioLocalizacaoDAO";
	
	public boolean existeUsuarioLocalizacao(final UsuarioLocalizacao usuarioLocalizacao) {
		final StringBuilder hql = new StringBuilder(EXISTE_USUARIO_LOCALIZACAO_QUERY);
		if (usuarioLocalizacao.getEstrutura() != null) {
			hql.append(ESTRUTURA_CONDITION);
		} else {
			hql.append(ESTRUTURA_NULL_CONDITION);
		}
		
		final HashMap<String, Object> params = new HashMap<>();
		params.put(PARAM_USUARIO, usuarioLocalizacao.getUsuario());
		params.put(PARAM_PAPEL, usuarioLocalizacao.getPapel());
		params.put(PARAM_LOCALIZACAO, usuarioLocalizacao.getLocalizacao());
		if (usuarioLocalizacao.getEstrutura() != null) {
		    params.put(PARAM_ESTRUTURA, usuarioLocalizacao.getEstrutura());
		}
		
		return (Long) getSingleResult(hql.toString(), params) > 0;
	}
	
	@Override
	public UsuarioLocalizacao persist(final UsuarioLocalizacao usuarioLocalizacao) throws DAOException {
		if (existeUsuarioLocalizacao(usuarioLocalizacao)) {
			throw new DAOException("#{messages['constraintViolation.uniqueViolation']}");
		}
		return super.persist(usuarioLocalizacao);
	}
}
