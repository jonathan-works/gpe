package br.com.infox.epp.access.service;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.SimplePrincipal;
import org.jboss.seam.security.management.IdentityManager;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.UsuarioPerfilDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.processo.dao.ProcessoDAO;

@Name(AuthenticatorService.NAME)
@AutoCreate
public class AuthenticatorService implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "authenticatorService";

    @In
    private UsuarioLoginManager usuarioLoginManager;
    @In
    private BloqueioUsuarioManager bloqueioUsuarioManager;

    @In
    private UsuarioPerfilDAO usuarioPerfilDAO;
    @In
    private ProcessoDAO processoDAO;

    private static final LogProvider LOG = Logging.getLogProvider(AuthenticatorService.class);

    public static final String PAPEIS_USUARIO_LOGADO = "papeisUsuarioLogado";
    public static final String USUARIO_LOGADO = "usuarioLogado";
    public static final String USUARIO_PERFIL_LIST = "usuarioPerfilList";

    public void autenticaManualmenteNoSeamSecurity(String login,
            IdentityManager identityManager) {
        Principal principal = new SimplePrincipal(login);
        Identity identity = Identity.instance();
        identity.acceptExternallyAuthenticatedPrincipal(principal);
        Credentials credentials = (Credentials) Component.getInstance(Credentials.class);
        credentials.clear();
        credentials.setUsername(login);
        identity.getCredentials().clear();
        identity.getCredentials().setUsername(login);
        List<String> roles = identityManager.getImpliedRoles(login);
        if (roles != null) {
            for (String role : roles) {
                identity.addRole(role);
            }
        }
    }

    /**
     * Metodo que coloca o usuario logado na sessão
     * 
     * @param usuario
     */
    public void setUsuarioLogadoSessao(UsuarioLogin usuario) {
        Contexts.getSessionContext().set(USUARIO_LOGADO, usuario);
        List<UsuarioPerfil> usuarioPerfilList = new ArrayList<UsuarioPerfil>(usuario.getUsuarioPerfilAtivoList());
        Contexts.getSessionContext().set(USUARIO_PERFIL_LIST, usuarioPerfilList);
    }

    public void validarUsuario(UsuarioLogin usuario) throws LoginException, DAOException {
        if (usuario.getBloqueio()) {
            if (bloqueioUsuarioManager.liberarUsuarioBloqueado(usuario)) {
                bloqueioUsuarioManager.desfazerBloqueioUsuario(usuario);
            } else {
                throwUsuarioBloqueado(usuario);
            }
        } else if (usuario.getProvisorio()) {
            if (usuarioLoginManager.usuarioExpirou(usuario)) {
                usuarioLoginManager.inativarUsuario(usuario);
                throwUsuarioExpirou(usuario);
            }
        } else if (!usuario.getAtivo()) {
            throwUsuarioInativo(usuario);
        }
    }

    private void throwUsuarioExpirou(UsuarioLogin usuario) throws LoginException {
        throw new LoginException("O usuário " + usuario.getNomeUsuario()
                + " expirou. " + "Por favor, contate o adminstrador do sistema");
    }

    private void throwUsuarioInativo(UsuarioLogin usuario) throws LoginException {
        throw new LoginException("O usuário " + usuario.getNomeUsuario()
                + " não está ativo.\n");
    }

    private void throwUsuarioBloqueado(UsuarioLogin usuario) throws LoginException {
        throw new LoginException("O usuário " + usuario.getNomeUsuario()
                + " está bloqueado."
                + "Por favor, contate o adminstrador do sistema");
    }

    public UsuarioLogin getUsuarioByLogin(String login) {
        return usuarioLoginManager.getUsuarioLoginByLogin(login);
    }

    @SuppressWarnings(UNCHECKED)
    public void removeRolesAntigas() {
        Set<String> roleSet = (Set<String>) Contexts.getSessionContext().get(PAPEIS_USUARIO_LOGADO);
        if (roleSet != null) {
            for (String r : roleSet) {
                Identity.instance().removeRole(r);
            }
        }
    }

    public void logDaBuscaDasRoles(UsuarioPerfil usuarioPerfil) {
        LOG.warn("Obter role do Perfil: " + usuarioPerfil);
        LOG.warn("Obter role do papel: " + usuarioPerfil.getPerfil().getPapel());
    }

    public void addRolesAtuais(Set<String> roleSet) {
        for (String role : roleSet) {
            Identity.instance().addRole(role);
        }
    }

    //TODO refazer essa busca pelo PerfilAtual
    public UsuarioPerfil obterPerfilAtual(UsuarioLogin usuario) throws LoginException {
        List<UsuarioPerfil> usuarioPerfilList = new ArrayList<>(usuario.getUsuarioPerfilList());
        if (usuarioPerfilList.size() > 0) {
            UsuarioPerfil usuarioPerfil = usuarioPerfilList.get(0);
            return usuarioPerfilDAO.getReference(usuarioPerfil.getIdUsuarioPerfil());
        }
        throw new LoginException("O usuário " + usuario
                + " não possui Perfil");
    }

    public void anulaActorId(String actorId) throws DAOException {
        processoDAO.anulaActorId(actorId);
    }

    public void anularTodosActorId() throws DAOException {
        processoDAO.anularTodosActorId();
    }

}
