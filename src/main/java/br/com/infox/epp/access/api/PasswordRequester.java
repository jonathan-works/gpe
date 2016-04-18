package br.com.infox.epp.access.api;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.auth.login.LoginException;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.access.service.RecuperacaoSenhaService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Named
@RequestScoped
public class PasswordRequester {

    private static final LogProvider LOG = Logging.getLogProvider(PasswordRequester.class);

    @Inject
    private UsuarioLoginManager usuarioLoginManager;
    @Inject
    private PasswordService passwordService;
    @Inject
    private RecuperacaoSenhaService recuperacaoSenhaService;

    private String login;
    private String email;
    private String codigo;
    private String newPass1;
    private String newPass2;

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

    public void requisitarCodigoRecuperacao() throws LoginException {
    	try {
    		UsuarioLogin usuario = null;
    		if (!login.isEmpty()) {
    			usuario = usuarioLoginManager.getUsuarioLoginByLogin(login);
    		}
    		if (usuario == null && !email.isEmpty()) {
    			usuario = usuarioLoginManager.getUsuarioLoginByEmail(email);
    		}
			if (usuario == null) {
				FacesMessages.instance().add("Usuário não encontrado");
			} else {
				recuperacaoSenhaService.requisitarCodigoRecuperacao(usuario);
				FacesMessages.instance().add("O código de recuperação de senha foi enviado para seu email.");
    		}
    	} catch (Exception e) {
    		LOG.error("Erro ao requisitar código de recuperação de senha", e);
    		FacesMessages.instance().add(e.getMessage());
    	}
    }

    public void trocarSenha() {
    	UsuarioLogin usuario = usuarioLoginManager.getUsuarioLoginByLogin(login);
    	if (usuario == null || !recuperacaoSenhaService.verificarValidadeCodigo(codigo, usuario)) {
    		FacesMessages.instance().add("Código de recuperação de senha inválido ou expirado.");
    		return;
    	}
    	if (newPass1 == null || newPass2 == null || newPass1.isEmpty() || newPass2.isEmpty() || !newPass1.equals(newPass2)) {
    		FacesMessages.instance().add(InfoxMessages.getInstance().get("login.error.novaSenhaNaoConfere"));
    		return;
    	}
		passwordService.changePassword(usuario, newPass1);
		FacesMessages.instance().add("Senha alterada com sucesso");
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

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNewPass1() {
		return newPass1;
	}

	public void setNewPass1(String newPass1) {
		this.newPass1 = newPass1;
	}

	public String getNewPass2() {
		return newPass2;
	}

	public void setNewPass2(String newPass2) {
		this.newPass2 = newPass2;
	}

}
