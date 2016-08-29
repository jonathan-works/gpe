package br.com.infox.epp.login;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.jboss.seam.Component;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.manager.ldap.LDAPManager;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.system.manager.ParametroManager;

@Stateless
public class LoginService {
	
    @Inject
    protected InfoxMessages infoxMessages;
    @Inject
    protected UsuarioLoginManager usuarioLoginManager;
    @Inject
    private Logger logger;
    @Inject
    private LDAPManager ldapManager;
    @Inject
    private PasswordService passwordService;
    
    protected String getDomainName() {
        final ParametroManager parametroManager = (ParametroManager) Component.getInstance(ParametroManager.NAME);
        return parametroManager.getValorParametro("ldapDomainName");
    }

    protected String getProviderUrl() {
        final ParametroManager parametroManager = (ParametroManager) Component.getInstance(ParametroManager.NAME);
        return parametroManager.getValorParametro("ldapProviderUrl");
    }
    
    private boolean autenticarBanco(String login, String senha) {
        UsuarioLogin usuario = usuarioLoginManager.getUsuarioLoginByLogin(login);
        if(usuario == null) {
        	return false;
        }
        if(usuario.getSenha().equals(passwordService.generatePasswordHash(senha, usuario.getSalt()))) {
        	return true;
        }
        return false;        
    }
    
    private boolean autenticarLdap(String login, String senha) {
    	String providerUrl = getProviderUrl();
    	String domainName = getDomainName();
    	if (StringUtil.isEmpty(providerUrl)) return false;
		try {
			UsuarioLogin usuario = ldapManager.autenticarLDAP(login, senha, providerUrl, domainName);
	        if(usuario == null) {
	        	return false;
	        }
	        usuarioLoginManager.persist(usuario);
	        return true;
		} catch (NamingException e) {
			logger.log(Level.WARNING, "ldapException", e);
			return false;
		}
    }
    
    
    public boolean autenticar(String usuario, String senha) {
    	return (autenticarBanco(usuario, senha) || autenticarLdap(usuario, senha));
    }
}
