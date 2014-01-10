package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.UsuarioLoginQuery.INATIVAR_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_EMAIL;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_ID;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_ID_TASK_INSTANCE;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_LOGIN;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_EMAIL;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_LOGIN_TASK_INSTANCE;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_LOGIN_NAME;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Name(UsuarioLoginDAO.NAME)
@AutoCreate
public class UsuarioLoginDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioLoginDAO";

    public UsuarioLogin getUsuarioLoginByEmail(String email) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_EMAIL, email);
        return getNamedSingleResult(USUARIO_BY_EMAIL, parameters);
    }

    public UsuarioLogin getUsuarioByLoginTaskInstance(Long idTaskInstance, String actorId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_LOGIN, actorId);
        parameters.put(PARAM_ID_TASK_INSTANCE, idTaskInstance);
        return getNamedSingleResult(USUARIO_BY_LOGIN_TASK_INSTANCE, parameters);
    }

    public void inativarUsuario(UsuarioLogin usuario) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID, usuario.getIdUsuarioLogin());
        executeNamedQueryUpdate(INATIVAR_USUARIO, parameters);
    }

    public UsuarioLogin getUsuarioLoginByLogin(String login) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_LOGIN, login);
        return getNamedSingleResult(USUARIO_LOGIN_NAME, parameters);
    }

}
