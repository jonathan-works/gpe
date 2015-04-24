package br.com.infox.epp.access.manager;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.RolesMap;
import br.com.infox.epp.access.dao.PapelDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.seam.security.operation.PopulateRoleMembersListOperation;

@Name(PapelManager.NAME)
@AutoCreate
public class PapelManager extends Manager<PapelDAO, Papel> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "papelManager";
    
    @In
    private RolesMap rolesMap;

    public List<Papel> getPapeisNaoAssociadosATipoModeloDocumento(
            TipoModeloDocumento tipoModeloDocumento) {
        return getDao().getPapeisNaoAssociadosATipoModeloDocumento(tipoModeloDocumento);
    }

    public List<Papel> getPapeisNaoAssociadosAClassificacaoDocumento(
            ClassificacaoDocumento classificacaoDocumento) {
        return getDao().getPapeisNaoAssociadosAClassificacaoDocumento(classificacaoDocumento);
    }

    public Papel getPapelByIdentificador(String identificador) {
        return getDao().getPapelByIndentificador(identificador);
    }

    public List<Papel> getPapeisByListaDeIdentificadores(
            List<String> identificadores) {
        if (identificadores == null || identificadores.isEmpty()) {
            return new ArrayList<Papel>();
        }
        return getDao().getPapeisByListaDeIdentificadores(identificadores);
    }

    public List<Papel> getPapeisDeUsuarioByLocalizacao(Localizacao localizacao) {
        return getDao().getPapeisDeUsuarioByLocalizacao(localizacao);
    }

    public List<String> getListaDeNomesDosPapeis() {
        return getDao().getListaDeNomesDosPapeis();
    }
    
    @Override
    public Papel persist(Papel o) throws DAOException {
        Papel papel = super.persist(o);
        rolesMap.clear();
        return papel;
    }
    
    @Override
    public Papel update(Papel o) throws DAOException {
        Papel papel = super.update(o);
        rolesMap.clear();
        return papel;
    }
    
    public List<String> getIdentificadoresPapeisMembros(String identificadorPapelBase) {
    	List<Principal> roles = new ArrayList<>();
		new PopulateRoleMembersListOperation(identificadorPapelBase, roles).run();
		List<String> papeisMembros = new ArrayList<>();
		for (Principal role : roles) {
			papeisMembros.add(role.getName());
		}
		return papeisMembros;
    }
    
    public boolean hasToSignTermoAdesao(UsuarioLogin usuario){
    	return getDao().hasToSignTermoAdesao(usuario);
    }
    
    public UsuarioPerfil getPerfilTermoAdesao(UsuarioLogin usuario){
    	return getDao().getPerfilTermoAdesao(usuario);
    }
}
