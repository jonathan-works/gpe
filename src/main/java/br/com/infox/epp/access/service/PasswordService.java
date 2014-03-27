package br.com.infox.epp.access.service;

import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.RandomStringUtils;
import org.richfaces.component.util.Strings;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.security.operation.ChangePasswordOperation;

@Name(PasswordService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class PasswordService {
    public static final String NAME = "passwordService";

    private static final int PASSWORD_LENGTH = 8;

    @In
    private AccessMailService accessMailService;
    @In
    private UsuarioLoginManager usuarioLoginManager;

    /**
     * @deprecated use {@link requisitarNovaSenha(email, login)} instead.
     * */
    @Deprecated
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

    /**
     * Inicia uma requisição de nova senha para o usuário baseado no e-mail ou
     * no login (note que um dos parâmetros pode ser nulo, mas não os dois
     * simultaneamente)
     * @param email o e-mail do usuário
     * @param login o login do usuário
     * 
     * @throws BusinessException caso o sistema não consiga enviar o email
     * @throws LoginException caso o método seja invocado com ambos os parâmetros nulos
     * @throws DAOException caso não seja possível gerar/gravar uma nova senha
     * */
    public void requisitarNovaSenha(final String email, final String login) throws LoginException, DAOException {
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

    private void recoverUsuario(UsuarioLogin usuario, String tipoParametro) throws LoginException, DAOException {
        if (usuario == null) {
            throw new LoginException("Usuário não encontrado");
        }
        final String plainTextPassword = gerarNovaSenha(usuario);
        changePassword(usuario, plainTextPassword);
        accessMailService.enviarEmailDeMudancaDeSenha(tipoParametro, usuario, plainTextPassword);
    }

    private String gerarNovaSenha(final UsuarioLogin usuario) throws DAOException {
        String senha;
        if (ParametroUtil.LOGIN_USUARIO_EXTERNO.equals(usuario.getLogin())) {
            senha = "";
        } else {
            senha = RandomStringUtils.randomAlphabetic(PASSWORD_LENGTH);
        }
        return senha;
    }

    public void changePassword(final UsuarioLogin usuario,
            final String newPassword) throws DAOException {
        usuario.setSenha(newPassword);
        new ChangePasswordOperation(usuario.getLogin(), usuario.getSenha()).run();
        usuarioLoginManager.update(usuario);
    }

}
