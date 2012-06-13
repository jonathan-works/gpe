package br.com.infox.epa.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;

/**
 * Classe DAO para a entidade NaturezaCategoriaAssunto
 * @author Daniel
 *
 */
@Name(NaturezaDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class NaturezaDAO extends GenericDAO {

	public static final String NAME = "naturezaDAO";

}