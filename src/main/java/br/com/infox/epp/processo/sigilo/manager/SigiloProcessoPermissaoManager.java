package br.com.infox.epp.processo.sigilo.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.sigilo.dao.SigiloProcessoPermissaoDAO;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao;

@AutoCreate
@Name(SigiloProcessoPermissaoManager.NAME)
public class SigiloProcessoPermissaoManager extends Manager<SigiloProcessoPermissaoDAO, SigiloProcessoPermissao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloProcessoPermissaoManager";

    public boolean usuarioPossuiPermissao(UsuarioLogin usuario,
            SigiloProcesso sigiloProcesso) {
        return getDao().usuarioPossuiPermissao(usuario, sigiloProcesso);
    }

    public void inativarPermissoes(SigiloProcesso sigiloProcesso) throws DAOException {
        getDao().inativarPermissoes(sigiloProcesso);
    }

    public List<SigiloProcessoPermissao> getPermissoes(SigiloProcesso sigiloProcesso) {
        return getDao().getPermissoes(sigiloProcesso);
    }

    public void gravarPermissoes(List<SigiloProcessoPermissao> permissoes,
            SigiloProcesso sigiloProcesso) throws DAOException {
        inativarPermissoes(sigiloProcesso);
        for (SigiloProcessoPermissao permissao : permissoes) {
            permissao.setSigiloProcesso(sigiloProcesso);
            persist(permissao);
        }
    }

    public static final String getPermissaoConditionFragment() {
        StringBuilder sb = new StringBuilder("(not exists (select 1 from SigiloProcesso sp where sp.processo.idProcesso = o.idProcesso and sp.ativo = true and sp.sigiloso = true) ");
        UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
        if (usuarioLogado != null) {
            sb.append("or exists (select 1 from SigiloProcessoPermissao spp inner join spp.sigiloProcesso sp where spp.usuario.idUsuarioLogin = " + usuarioLogado.getIdUsuarioLogin());
            sb.append(" and spp.ativo = true and sp.ativo = true and sp.sigiloso = true))");
        } else {
            sb.append("or 1 = 0)");
        }
        return sb.toString();
    }
}
