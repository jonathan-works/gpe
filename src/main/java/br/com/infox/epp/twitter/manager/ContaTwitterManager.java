package br.com.infox.epp.twitter.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.twitter.dao.ContaTwitterDAO;
import br.com.infox.epp.twitter.entity.ContaTwitter;

@Name(ContaTwitterManager.NAME)
@AutoCreate
public class ContaTwitterManager extends Manager<ContaTwitterDAO, ContaTwitter> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "contaTwitterManager";

    public ContaTwitter getContaTwitterByLocalizacao(Localizacao localizacao) {
        return getDao().getContaTwitterByLocalizacao(localizacao);
    }

    public ContaTwitter getContaTwitterByUsuario(UsuarioLogin usuario) {
        return getDao().getContaTwitterByUsuario(usuario);
    }

    public ContaTwitter getContaTwitterByIdUsuario(Integer idUsuario) {
        return getDao().getContaTwitterByIdUsuario(idUsuario);
    }

    public List<ContaTwitter> listaContasTwitter(int idGrupoEmail) {
        return getDao().listaContasTwitter(idGrupoEmail);
    }

}
