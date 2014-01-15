package br.com.infox.epp.access.service;

import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.RandomStringUtils;
import org.richfaces.component.util.Strings;

import br.com.infox.core.exception.BusinessException;
import br.com.infox.core.operation.ChangePasswordOperation;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.system.util.ParametroUtil;

@Name(PasswordService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class PasswordService {
    public static final String NAME = "passwordService";
    
    private static final int PASSWORD_LENGTH = 8;
    
    @In private AccessMailService accessMailService;
    @In private UsuarioLoginManager usuarioLoginManager;

    public void requisitarNovaSenha(final boolean usingLogin, final String value) throws LoginException, DAOException {
        if (Strings.isEmpty(value)) {
            throw new LoginException("É preciso informar o login ou o e-mail do usuário");
        }
        UsuarioLogin usuario;
        String mode;
        if (usingLogin) {
            usuario = usuarioLoginManager.getUsuarioLoginByLogin(value);
            mode = "login";
        } else {
            usuario = usuarioLoginManager.getUsuarioLoginByEmail(value);
            mode = "email";
        }
        recoverUsuario(usuario, mode);
    }
    
    public void requisitarNovaSenha(final String email, final String login) throws LoginException, BusinessException, DAOException {
        UsuarioLogin usuario;
        String mode;
        if (!login.isEmpty()) {
            usuario = usuarioLoginManager.getUsuarioLoginByLogin(login);
            mode = "login";
        } else if (!email.isEmpty()) {
            usuario = usuarioLoginManager.getUsuarioLoginByEmail(email);
            mode = "email";
        } else {
            throw new LoginException("É preciso informar o login ou o e-mail do usuário");
        }
        recoverUsuario(usuario, mode);
    }
    
    private void recoverUsuario(UsuarioLogin usuario, String tipoParametro) throws LoginException, BusinessException, DAOException {
        if (usuario == null) {
            throw new LoginException("Usuário não encontrado");
        }
        final String password = gerarNovaSenha(usuario);
        accessMailService.enviarEmailDeMudancaDeSenha(tipoParametro, usuario, password);
    }
    
    private String gerarNovaSenha(final UsuarioLogin usuario) throws DAOException {
        if (ParametroUtil.LOGIN_USUARIO_EXTERNO.equals(usuario.getLogin())) {
            usuario.setSenha("");
        } else {
            usuario.setSenha(RandomStringUtils.randomAlphabetic(PASSWORD_LENGTH));
        }
        
        new ChangePasswordOperation(usuario.getLogin(), usuario.getSenha()).run();
        usuarioLoginManager.update(usuario);
        return usuario.getSenha();
    }
    
}
