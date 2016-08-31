package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.BLOQUEIOS_ATIVOS;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.BLOQUEIO_MAIS_RECENTE;
import static br.com.infox.epp.access.query.BloqueioUsuarioQuery.PARAM_USUARIO;

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
        getEntityManager().flush();
    }

    private void desbloquearUsuario(UsuarioLogin usuarioLogin) throws DAOException {
        usuarioLogin.setBloqueio(false);
        getEntityManager().merge(usuarioLogin);
    }

    private void gravarDesbloqueio(BloqueioUsuario bloqueioUsuario) throws DAOException {
        bloqueioUsuario.setDataDesbloqueio(new Date());
        getEntityManager().merge(bloqueioUsuario);
    }

    public List<BloqueioUsuario> getBloqueiosAtivos() {
    	return getNamedResultList(BLOQUEIOS_ATIVOS);
    }
}
