package br.com.infox.epp.access.manager;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.security.management.IdentityStore;

import com.google.common.base.Strings;

import br.com.infox.core.exception.EppConfigurationException;
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
import br.com.infox.epp.system.Parametros;
import br.com.infox.seam.util.ComponentUtil;

@Stateless
public class PapelManager extends Manager<PapelDAO, Papel> {

    private static final long serialVersionUID = 1L;
    
    private RolesMap rolesMap = ComponentUtil.getComponent(RolesMap.NAME);

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
    
    //Lista todos os identificadores dos papéis que herdam (direta ou indiretamente) do papel dado 
    public List<String> getIdentificadoresPapeisMembros(String identificador) {
    	return ComponentUtil.<IdentityStore>getComponent("org.jboss.seam.security.identityStore").listAllMembers(identificador);
	}
    
    public boolean isPapelHerdeiro(String identificadorPapelHerdeiro, String identificadorPapelBase) {
    	return ComponentUtil.<IdentityStore>getComponent("org.jboss.seam.security.identityStore").isImpliedMemberOf(identificadorPapelHerdeiro, identificadorPapelBase);
    }
    
    public boolean hasToSignTermoAdesao(UsuarioLogin usuario){
    	return getDao().hasToSignTermoAdesao(usuario);
    }
    
    public UsuarioPerfil getPerfilTermoAdesao(UsuarioLogin usuario){
    	return getDao().getPerfilTermoAdesao(usuario);
    }
    
    public List<Papel> getPapeisOrdemAlfabetica() {
        return getDao().getPapeisOrdemAlfabetica();
    }
    
    public List<String> getIdentificadoresPapeisHerdeiros(String identificadorPapelBase) {
        List<String> roles = getIdentificadoresPapeisMembros(identificadorPapelBase);
        roles.add(identificadorPapelBase);
        return roles;
    }
    
    public boolean isUsuarioExterno(String identificador) {
    	String identificadorUsuarioExterno = Parametros.PAPEL_USUARIO_EXTERNO.getValue();
    	if (Strings.isNullOrEmpty(identificadorUsuarioExterno)) {
    		throw new EppConfigurationException("O parâmetro " + Parametros.PAPEL_USUARIO_EXTERNO.getLabel() + " não está configurado");
    	}
    	return isPapelHerdeiro(identificador, identificadorUsuarioExterno);
    }
    
    public boolean isUsuarioInterno(String identificador) {
    	String identificadorUsuarioInterno = Parametros.PAPEL_USUARIO_INTERNO.getValue();
    	if (Strings.isNullOrEmpty(identificadorUsuarioInterno)) {
    		throw new EppConfigurationException("O parâmetro " + Parametros.PAPEL_USUARIO_INTERNO.getLabel() + " não está configurado");
    	}
    	return isPapelHerdeiro(identificador, identificadorUsuarioInterno);
    }
}
