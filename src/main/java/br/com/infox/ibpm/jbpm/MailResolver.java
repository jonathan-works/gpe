package br.com.infox.ibpm.jbpm;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.entity.ContaTwitter;
import br.com.infox.util.constants.WarningConstants;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

/**
 * Classe que busca os usuários cadastrados para receberem email
 * @author luiz
 *
 */
@Name(MailResolver.NAME)
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
public class MailResolver {

	public static final String NAME = "mailResolver";
	
	private static final String QUERY = "select distinct u.email from UsuarioLogin u " +
			"join u.usuarioLocalizacaoList ul ";
	private static final String QUERY_TWITTER = "select distinct c from ContaTwitter c " +
			"join c.usuario u " +
			"join u.usuarioLocalizacaoList ul ";
	private static final String QUERY_CONDITION = "where exists (" + 
								"select o from ListaEmail o where o.idGrupoEmail = :idGrupoEmail and (" +
								"(ul.localizacao = o.localizacao and (ul.papel = o.papel or o.papel is null) and (ul.estrutura = o.estrutura or o.estrutura is null)) " +
								"or (ul.papel = o.papel and (ul.localizacao = o.localizacao or o.localizacao is null) and (ul.estrutura = o.estrutura or o.estrutura is null)) " +
								"or (ul.estrutura = o.estrutura and (ul.localizacao = o.localizacao or o.localizacao is null) and (ul.papel = o.papel or o.papel is null))))";;
	
	public static MailResolver instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<ContaTwitter> listaContasTwitter(int idGrupoEmail) {
		return EntityUtil.getEntityManager().createQuery(QUERY_TWITTER+QUERY_CONDITION)
				.setParameter("idGrupoEmail", idGrupoEmail)
				.getResultList();
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<String> resolve(int idGrupoEmail) {		
		return EntityUtil.getEntityManager().createQuery(QUERY+QUERY_CONDITION)
				.setParameter("idGrupoEmail", idGrupoEmail)
				.getResultList();
	}
	
}