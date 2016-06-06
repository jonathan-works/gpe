package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.BLOQUEIOS_ATIVOS;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.BLOQUEIO_MAIS_RECENTE;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.PARAM_BLOQUEIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.PARAM_DATA_DESBLOQUEIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.PARAM_ID_USUARIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.PARAM_USUARIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.SAVE_DATA_DESBLOQUEIO;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.UNDO_BLOQUEIO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Stateless
@AutoCreate
@Name(BloqueioUsuarioDAO.NAME)
public class BloqueioUsuarioDAO extends DAO<BloqueioUsuario> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "bloqueioUsuarioDAO";

    public BloqueioUsuario getBloqueioUsuarioMaisRecente(
            UsuarioLogin usuarioLogin) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_USUARIO, usuarioLogin);
        return getNamedSingleResult(BLOQUEIO_MAIS_RECENTE, parameters);
    }

    public void desfazerBloqueioUsuario(BloqueioUsuario bloqueioUsuario) throws DAOException {
        desbloquearUsuario(bloqueioUsuario.getUsuario());
        gravarDesbloqueio(bloqueioUsuario);
    }

    private void desbloquearUsuario(UsuarioLogin usuarioLogin) throws DAOException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_ID_USUARIO, usuarioLogin.getIdUsuarioLogin());
        executeNamedQueryUpdate(UNDO_BLOQUEIO, parameters);
    }

    private void gravarDesbloqueio(BloqueioUsuario bloqueioUsuario) throws DAOException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_BLOQUEIO, bloqueioUsuario.getIdBloqueioUsuario());
        parameters.put(PARAM_DATA_DESBLOQUEIO, new Date());
        executeNamedQueryUpdate(SAVE_DATA_DESBLOQUEIO, parameters);
    }

    public List<BloqueioUsuario> getBloqueiosAtivos() {
    	return getNamedResultList(BLOQUEIOS_ATIVOS);
    }
}
