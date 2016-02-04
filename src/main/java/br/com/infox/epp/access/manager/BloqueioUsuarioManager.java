package br.com.infox.epp.access.manager;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.BloqueioUsuarioDAO;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Name(BloqueioUsuarioManager.NAME)
@AutoCreate
@Stateless
public class BloqueioUsuarioManager extends Manager<BloqueioUsuarioDAO, BloqueioUsuario> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "bloqueioUsuarioManager";

    @In
    private BloqueioUsuarioDAO bloqueioUsuarioDAO;

    public Date getDataParaDesbloqueio(UsuarioLogin usuarioLogin) {
        assert usuarioLogin.getBloqueio();
        BloqueioUsuario bloqueioUsuario = bloqueioUsuarioDAO.getBloqueioUsuarioMaisRecente(usuarioLogin);
        return bloqueioUsuario.getDataPrevisaoDesbloqueio();
    }

    public void desfazerBloqueioUsuario(UsuarioLogin usuarioLogin) throws DAOException {
        BloqueioUsuario bloqueioUsuario = bloqueioUsuarioDAO.getBloqueioUsuarioMaisRecente(usuarioLogin);
        assert bloqueioUsuario.getDataDesbloqueio() != null;
        bloqueioUsuarioDAO.desfazerBloqueioUsuario(bloqueioUsuario);
    }

    public boolean liberarUsuarioBloqueado(UsuarioLogin usuarioLogin) {
        Date dataParaDesbloqueio = getDataParaDesbloqueio(usuarioLogin);
        if (dataParaDesbloqueio != null) {
            return dataParaDesbloqueio.before(new Date());
        } else {
            return false;
        }
    }

    public BloqueioUsuario getUltimoBloqueio(UsuarioLogin usuarioLogin) {
        return bloqueioUsuarioDAO.getBloqueioUsuarioMaisRecente(usuarioLogin);
    }

    public List<BloqueioUsuario> getBloqueiosAtivos() {
    	return getDao().getBloqueiosAtivos();
    }
}
