package br.com.infox.epp.mail;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.Serializable;

import javax.mail.Session;
import javax.naming.NamingException;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.epp.system.Configuration;

@Name("org.jboss.seam.mail.mailSession")
@Install(precedence = Install.DEPLOYMENT, classDependencies = "javax.mail.Session")
@Scope(APPLICATION)
@BypassInterceptors
public class MailSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @Unwrap
    public Session getSession() throws NamingException {
        return Configuration.getInstance().getApplicationServer().getMailSession();
    }

}
