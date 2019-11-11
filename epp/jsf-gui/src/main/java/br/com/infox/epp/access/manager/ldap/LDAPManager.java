package br.com.infox.epp.access.manager.ldap;

import static java.text.MessageFormat.format;

import java.util.Hashtable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.system.manager.ParametroManager;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class LDAPManager {
	
    protected static final String CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

    public enum SecurityAuthenticationType {
        NONE, SIMPLE, SASL_MECH
    }
    
    @Inject
    private ParametroManager parametroManager;

    private UsuarioLogin createUsuario(final String login, final String senha,
            final Attributes attributes) throws NamingException {
        final Attribute mail = attributes.get("mail");
        final Attribute userPrincipalName = attributes.get("userPrincipalName");
        final Attribute displayName = attributes.get("displayName");

        final UsuarioLogin usuarioLogin = new UsuarioLogin();
        usuarioLogin.setLogin(login);
        usuarioLogin.setSenha(senha);
        usuarioLogin.setAtivo(Boolean.TRUE);
        usuarioLogin.setTipoUsuario(UsuarioEnum.H);
        usuarioLogin.setBloqueio(Boolean.FALSE);
        usuarioLogin.setProvisorio(Boolean.FALSE);
        if (displayName != null) {
            usuarioLogin.setNomeUsuario(displayName.get().toString());
        }
        if (mail != null) {
            usuarioLogin.setEmail(mail.get().toString());
        } else if (userPrincipalName != null) {
            usuarioLogin.setEmail(userPrincipalName.get().toString());
        }

        return usuarioLogin;
    }
    
    protected String getDomainName() {
        return parametroManager.getValorParametro("ldapDomainName");
    }

    protected String getProviderUrl() {
        return parametroManager.getValorParametro("ldapProviderUrl");
    }
    
    public UsuarioLogin autenticarLDAP(String usuario, String senha) throws NamingException {
        return autenticarLDAP(usuario, senha, getProviderUrl(), getDomainName());
    }
    
    public UsuarioLogin autenticarLDAP(String usuario, String senha, String providerUrl, String domainName) throws NamingException {
        if (StringUtils.isEmpty(providerUrl) || "-1".equals(providerUrl)){
            return null;
        }
    	Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
        env.put(Context.SECURITY_AUTHENTICATION, SecurityAuthenticationType.SIMPLE.name());
        env.put(Context.SECURITY_PRINCIPAL, usuario);
        env.put(Context.SECURITY_CREDENTIALS, senha);
        env.put(Context.PROVIDER_URL, providerUrl);

        final DirContext ctx = new InitialDirContext(env);
        final String[] subDomains = domainName.split("\\.");
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < subDomains.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("DC=");
            sb.append(subDomains[i]);
        }
        final String searchBase = sb.toString();
        final String searchFilter = format("(&(objectClass=person)&(userPrincipalName={0}@{1}))", usuario, domainName);
        final SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        final NamingEnumeration<SearchResult> results = ctx.search(searchBase, searchFilter, controls);
        UsuarioLogin usuarioLogin = null;
        while (results.hasMoreElements()) {
            final SearchResult el = results.nextElement();
            final Attributes attributes = el.getAttributes();
            usuarioLogin = createUsuario(usuario, senha, attributes);
            if (usuarioLogin != null) {
            	break;
            }
        }
        return usuarioLogin;
    }

}
