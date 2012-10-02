package br.com.infox.ldap.util;

import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.util.ParametroUtil;

public final class LdapUtil {
	
	private LdapUtil() { }
	
	public static DirContext getLDAPDirContextAD() {
		DirContext ctx = null;
		Hashtable<String, String> env = gerarParametrosUsuarioPadraoLDAP();
		if(env == null)
			return ctx;
	      
	      try {
	         ctx = new InitialDirContext(env);
	      } catch (NamingException e) {
	    	  exibirMenssagemLDAP();
	      }
	      return ctx;
	}
	
	private static String gereParametrosAD(String domain) {
		String nome = "";
		StringTokenizer tokens = new StringTokenizer(domain, ".");
		while(tokens.hasMoreTokens()) {
			nome+= ",DC=" + tokens.nextToken();
		}
		return nome;
	}
	//InitialLdapContext
	
	public static LdapContext autentiqueUsuarioAD(String nome, String pw){
		LdapContext ctx = null;
		Hashtable<String, String> env = gerarParametrosLDAP(nome, pw);
		if(env ==null)
			return ctx;

	      try {
	         ctx = new InitialLdapContext(env, null);
	      } catch (NamingException e) {
	         //exibirMenssagemLDAP();
	      }
	      return ctx;
    }

	private static Hashtable<String, String> gerarParametrosUsuarioPadraoLDAP() {
		String host = ParametroUtil.getLDAPHost();
		String domain = ParametroUtil.getLDAPDomain();
		String password = ParametroUtil.getLDAPassword();
        if(password == null || host == null || domain ==null || "".equals(password)) {
        	exibirMenssagemLDAP();
      	  return null;
        }
		Hashtable<String, String> env = new Hashtable<String, String>();
	    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://"+ host + "/CN=users" + gereParametrosAD(domain));
        env.put(javax.naming.Context.SECURITY_AUTHENTICATION, "Simple");  
        env.put(javax.naming.Context.SECURITY_PRINCIPAL, ParametroUtil.getLDAPLogin() + "@" + domain);
        env.put(javax.naming.Context.SECURITY_CREDENTIALS, password);
		return env;
	}
	
	private static Hashtable<String, String> gerarParametrosLDAP(String login, String pw) {
		String host = ParametroUtil.getLDAPHost();
		String domain = ParametroUtil.getLDAPDomain();
        if(host == null || domain ==null) {
        	exibirMenssagemLDAP();
      	  return null;
        }
		Hashtable<String, String> env = new Hashtable<String, String>();
	    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://"+ host + "/CN=users" + gereParametrosAD(domain));
        env.put(javax.naming.Context.SECURITY_AUTHENTICATION, "Simple");  
        env.put(javax.naming.Context.SECURITY_PRINCIPAL, login + "@" + domain);
        env.put(javax.naming.Context.SECURITY_CREDENTIALS, pw);
		return env;
	}

	public static void exibirMenssagemLDAP() {
		if(FacesMessages.instance() != null) {
			String newLine = System.getProperty("line.separator");
			FacesMessages.instance().add(Severity.INFO, "Erro ao acessar o servidor Active Directory. Verifique os par�metro de acesso ao Active Directory : " + newLine +
				  "* ldap.domain" + newLine + "* ldap.host" + newLine + "* ldap.login" + newLine + "* ldap.password" + newLine + "* ldap.authentication");
		}
	}
	
	public static boolean pesquisarUsuarioAD(String login) {
		boolean retorno = false;
		String pes = "(&(objectclass=person)(userprincipalname=" + login + "@" + ParametroUtil.getLDAPDomain() + ")";
		NamingEnumeration<SearchResult> search = pesquisarLDAP(pes);
		try {
			retorno = search.hasMore();
		} catch (NamingException e) {
			exibirMenssagemLDAP();
		}
		return retorno;
	}
	
	/**
	 * Ap�s pesquisar sobre o Enumeration deve-se fechar a conex�o 
	 * @return 
	 */
	public static NamingEnumeration<SearchResult> pesquisarUsuariosLDAP() {
		String pes = "(objectclass=person)";
		NamingEnumeration<SearchResult> results = pesquisarLDAP(pes);
		return results;
	}
	
	private static NamingEnumeration<SearchResult> pesquisarLDAP(String search) {
		DirContext ctx = LdapUtil.getLDAPDirContextAD();
		if (ctx == null)
			return null;
		NamingEnumeration<SearchResult> results = null;
		try {
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			results = ctx.search("", search, controls);
			ctx.close();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return results;
	}
			

}
