package br.com.infox.epp.access.api;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.auth.login.LoginException;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Named
@RequestScoped
public class PasswordRequester {

    private static final LogProvider LOG = Logging.getLogProvider(PasswordRequester.class);

    @Inject
    private UsuarioLoginManager usuarioLoginManager;

    private String login;
    private String email;

    public void requisitarNovaSenha() throws LoginException {
        try {
            UsuarioLogin usuario = null;
            if (!login.isEmpty()) {
                usuario = usuarioLoginManager.getUsuarioLoginByLogin(login);
                usuarioLoginManager.requisitarNovaSenhaPorEmail(usuario, "login");
            } else if (!email.isEmpty()) {
                usuario = usuarioLoginManager.getUsuarioLoginByEmail(email);
                usuarioLoginManager.requisitarNovaSenhaPorEmail(usuario, "email");
            }
        } catch (BusinessException be) {
            LOG.warn(".requisitarNovaSenha()", be);
            FacesMessages.instance().add(be.getLocalizedMessage());
        } catch (DAOException e) {
            LOG.warn(".requisitarNovaSenha()", e);
            FacesMessages.instance().add(e.getLocalizedMessage());
        }
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
