package br.com.infox.epp.processo.documento.manager;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.infox.core.manager.AbstractEntityListener;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.processo.documento.entity.Documento;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DocumentoEntityListener extends AbstractEntityListener<Documento> {
    
    @Override
    public void prePersist(Documento documento) {
        UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
        if (usuarioPerfil == null) {
            UsuarioLogin usuario = getUsuarioSistema();
            // Assumindo que o usuário do sistema só possui um perfil associado a ele, o perfil Sistema
            // com papel sistema e localização raiz
            usuarioPerfil = usuario.getUsuarioPerfilList().get(0);
        }
        
        if (documento.getPerfilTemplate() == null){
            documento.setPerfilTemplate(usuarioPerfil.getPerfilTemplate());
        }
        if (documento.getLocalizacao() == null) {
            documento.setLocalizacao(usuarioPerfil.getLocalizacao());
        }
        if (documento.getDataInclusao() == null) {
            documento.setDataInclusao(new Date());
        }
        if (documento.getUsuarioInclusao() == null) {
            documento.setUsuarioInclusao(getUsuarioLogadoOuSistema());
        }
        documento.setExcluido(Boolean.FALSE);
    }
    
    @Override
    public void preUpdate(Documento documento) {
        documento.setDataAlteracao(new Date());
        documento.setUsuarioAlteracao(getUsuarioLogadoOuSistema());
    }
    
    private UsuarioLogin getUsuarioLogadoOuSistema() {
        UsuarioLogin usuario = Authenticator.getUsuarioLogado();
        if (usuario == null) {
            usuario = getUsuarioSistema();
        }
        return usuario;
    }
    
    private UsuarioLogin getUsuarioSistema() {
        UsuarioLoginManager usuarioLoginManager = Beans.getReference(UsuarioLoginManager.class);
        return usuarioLoginManager.getUsuarioSistema();
    }

}
