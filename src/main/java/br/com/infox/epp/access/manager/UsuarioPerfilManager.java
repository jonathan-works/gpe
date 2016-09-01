package br.com.infox.epp.access.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.UsuarioPerfilDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.entity.UsuarioPerfil_;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

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
    
    public List<PessoaFisica> getPessoasPermitidos(Localizacao localizacao, PerfilTemplate perfilTemplate) {
        return getDao().getPessoasPermitidos(localizacao, perfilTemplate);
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
	
	public List<PessoaFisica> listByLocalizacaoAtivo(Localizacao localizacao) {
	    return getDao().listByLocalizacaoAtivo(localizacao);
	}
	
    public List<PerfilTemplate> getPerfisAtivosByLocalizacaoContendoUsuario(Localizacao localizacao) {
        CriteriaBuilder cb = getDao().getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PerfilTemplate> query = cb.createQuery(PerfilTemplate.class);

        Root<UsuarioPerfil> up = query.from(UsuarioPerfil.class);
        Join<UsuarioPerfil, UsuarioLogin> ul = up.join(UsuarioPerfil_.usuarioLogin, JoinType.INNER);
        query.where(cb.equal(up.get(UsuarioPerfil_.localizacao), localizacao),
                cb.isTrue(up.get(UsuarioPerfil_.ativo)),
                cb.isTrue(ul.get(UsuarioLogin_.ativo)),
                cb.isNotNull(ul.get(UsuarioLogin_.pessoaFisica)));

        query.select(up.get(UsuarioPerfil_.perfilTemplate)).distinct(true);
        return getDao().getEntityManager().createQuery(query).getResultList();
    }
	
}
