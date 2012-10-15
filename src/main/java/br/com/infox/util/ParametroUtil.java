 package br.com.infox.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.ibpm.entity.Parametro;
import br.com.infox.ibpm.util.CarregarParametrosAplicacao;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(ParametroUtil.NAME)
@Scope(ScopeType.APPLICATION)
@Install(dependencies = { CarregarParametrosAplicacao.NAME })
@Startup(depends = CarregarParametrosAplicacao.NAME)
@BypassInterceptors
public class ParametroUtil {

	public static final String NAME = "parametroUtil";
	public static LogProvider log = Logging.getLogProvider(ParametroUtil.class);
	
	public static String getLDAPDomain() {
		String retorno = null;
		try {
			retorno = getParametro("ldap.domain");
		} catch (IllegalArgumentException e) {
			System.err.println("[#####] Erro ao acessar o parâmetro [#####]");
		}
		return retorno;
	}
	
	public static String getLDAPHost() {
		String retorno = null;
		try {
			retorno = getParametro("ldap.host");
		} catch (IllegalArgumentException e) {
			System.err.println("[#####] Erro ao acessar o parâmetro [#####]");
		}
		return retorno;
	}
	
	public static String getLDAPAuthentication() {
		String retorno = null;
		try {
			retorno = getParametro("ldap.authentication");
		} catch (IllegalArgumentException e) {
			System.err.println("[#####] Erro ao acessar o parâmetro [#####]");
		}
		return retorno;
	}
	
	public static String getLDAPLogin() {
		String retorno = null;
		try {
			retorno = getParametro("ldap.login");
		} catch (IllegalArgumentException e) {
			System.err.println("[#####] Erro ao acessar o parâmetro [#####]");
		}
		return retorno;
	}
	
	public static String getLDAPassword() {
		return getFromContext("ldap.password", false);
	}

	public static String getFromContext(String nomeParametro, boolean validar) {
		String value = (String) Contexts.getApplicationContext().get(nomeParametro);
		if (validar && value == null) {
			String erroMsg = "Parâmetro não encontrado: " + nomeParametro;
			log.error(erroMsg);
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, erroMsg);
		}
		return value;
	}

	public static ParametroUtil instance() {
		return ComponentUtil.getComponent(NAME);
	}

	public static String getParametro(String nome) {
		EntityManager em = EntityUtil.getEntityManager();
		List<Parametro> resultList = em.createQuery("select p from Parametro p where nomeVariavel = :nome")
				.setParameter("nome", nome).getResultList();
		if (!resultList.isEmpty()) {
			return resultList.get(0).getValorVariavel();
		}
		throw new IllegalArgumentException();
	}

	public String executarFactorys() {
		for (Method metodo : this.getClass().getDeclaredMethods()) {
			try {
				metodo.invoke(this);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return "OK";
	}
}