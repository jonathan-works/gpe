package br.com.infox.epp.access.crud;

import java.util.Date;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Name(BloqueioUsuarioCrudAction.NAME)
public class BloqueioUsuarioCrudAction extends AbstractCrudAction<BloqueioUsuario> {
    
    public static final String NAME = "bloqueioUsuarioCrudAction";
    
    private UsuarioLogin usuarioAtual;

    public UsuarioLogin getUsuarioAtual() {
        return usuarioAtual;
    }

    public void setUsuarioAtual(UsuarioLogin usuarioAtual) {
        this.usuarioAtual = usuarioAtual;
        if (usuarioAtual.permaneceBloqueado()){
            setInstance(usuarioAtual.getUltimoBloqueio());
        } else {
            newInstance();
        }
    }
    
    @Override
    protected boolean beforeSave() {
        getInstance().setUsuario(usuarioAtual);
        getInstance().setDataBloqueio(new Date());
        return super.beforeSave();
    }
    
    @Override
    public String save() {
        usuarioAtual.setBloqueio(true);
        update(usuarioAtual);
        return super.save();
    }

}
