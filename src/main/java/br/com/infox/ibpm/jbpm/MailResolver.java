package br.com.infox.ibpm.jbpm;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

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
	
	@SuppressWarnings("unchecked")
	public String resolve(int idGrupoEmail) {		
		List<String> lista = EntityUtil.getEntityManager()
			.createQuery("select distinct u.email from Usuario u " +
				"join u.usuarioLocalizacaoList ul " +
				"where exists (" + 
				"select o from ListaEmail o where o.idGrupoEmail = :idGrupoEmail and (" +
				"(ul.localizacao = o.localizacao and (ul.papel = o.papel or o.papel is null) and (ul.estrutura = o.estrutura or o.estrutura is null)) " +
				"or (ul.papel = o.papel and (ul.localizacao = o.localizacao or o.localizacao is null) and (ul.estrutura = o.estrutura or o.estrutura is null)) " +
				"or (ul.estrutura = o.estrutura and (ul.localizacao = o.localizacao or o.localizacao is null) and (ul.papel = o.papel or o.papel is null))))")
			.setParameter("idGrupoEmail", idGrupoEmail)
			.getResultList();	
		StringBuilder ret = new StringBuilder();
		for(String s : lista) {
			if (ret.length() > 0) {
				ret.append(";");
			}
			ret.append(s);
		}
		return ret.toString();
	}
	
}