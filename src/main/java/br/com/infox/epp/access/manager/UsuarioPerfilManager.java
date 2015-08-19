package br.com.infox.epp.access.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.UsuarioPerfilDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;

@AutoCreate
@Stateless
@Name(UsuarioPerfilManager.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UsuarioPerfilManager extends Manager<UsuarioPerfilDAO, UsuarioPerfil> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioPerfilManager";

    @In
    private UsuarioLoginManager usuarioLoginManager;

    public List<PerfilTemplate> getPerfisPermitidos(Localizacao localizacao) {
        return getDao().getPerfisPermitidos(localizacao);
    }

    public List<UsuarioPerfil> listByUsuarioLogin(UsuarioLogin usuarioLogin) {
        return getDao().listByUsuarioLogin(usuarioLogin);
    }

    public UsuarioPerfil getByUsuarioLoginPerfilTemplateLocalizacao(
            UsuarioLogin usuarioLogin, PerfilTemplate perfilTemplate,
            Localizacao localizacao) {
        return getByUsuarioLoginPerfilTemplateLocalizacaoAtivo(usuarioLogin, perfilTemplate, localizacao, true);
    }
    
    public UsuarioPerfil getByUsuarioLoginPerfilTemplateLocalizacaoAtivo(UsuarioLogin usuarioLogin, PerfilTemplate perfilTemplate,
            Localizacao localizacao, boolean ativo){
    	return getDao().getByUsuarioLoginPerfilTemplateLocalizacao(usuarioLogin, perfilTemplate, localizacao, ativo);
    }

    public void removeByUsuarioPerfilTemplateLocalizacao(
            UsuarioLogin usuarioLogin, PerfilTemplate perfilTemplate,
            Localizacao localizacao) throws DAOException {
        UsuarioPerfil usuarioPerfil = getByUsuarioLoginPerfilTemplateLocalizacaoAtivo(
                usuarioLogin, perfilTemplate, localizacao, true);
        if(usuarioPerfil != null){
        	usuarioPerfil.setAtivo(Boolean.FALSE);
        	update(usuarioPerfil);
        }
        if (listByUsuarioLogin(usuarioLogin).isEmpty()) {
            usuarioLogin.setAtivo(Boolean.FALSE);
            usuarioLoginManager.update(usuarioLogin);
        }
    }
    
    public boolean existeUsuarioPerfilAtivo(UsuarioLogin usuarioLogin, String descricaoPerfil, boolean ativo) {
    	return getDao().existeUsuarioPerfil(usuarioLogin, descricaoPerfil, ativo);
    }

	@Override
	public UsuarioPerfil persist(UsuarioPerfil o) throws DAOException {
		UsuarioPerfil perfilExistente  = getByUsuarioLoginPerfilTemplateLocalizacaoAtivo(o.getUsuarioLogin(), o.getPerfilTemplate(), o.getLocalizacao(), false);
		if (perfilExistente != null){
			perfilExistente.setAtivo(Boolean.TRUE);
			perfilExistente.setResponsavelLocalizacao(o.getResponsavelLocalizacao());
			return super.update(perfilExistente);
		}
		return super.persist(o);
	}
	
}
