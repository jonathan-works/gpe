package br.com.infox.epp.access.crud;

import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;

@Name(BloqueioUsuarioCrudAction.NAME)
public class BloqueioUsuarioCrudAction extends AbstractCrudAction<BloqueioUsuario> {
    
    public static final String NAME = "bloqueioUsuarioCrudAction";
    
    private UsuarioLogin usuarioAtual;
    
    @In private BloqueioUsuarioManager bloqueioUsuarioManager;
    private boolean isUsuarioAtualBloqueado = false;

    public UsuarioLogin getUsuarioAtual() {
        return usuarioAtual;
    }

    public void setUsuarioAtual(UsuarioLogin usuarioAtual) {
        this.usuarioAtual = usuarioAtual;
        if (existeBloqueioAtivo()){
            setInstance(bloqueioUsuarioManager.getUltimoBloqueio(usuarioAtual));
            this.isUsuarioAtualBloqueado = true;
        } else {
        	this.isUsuarioAtualBloqueado = false;
            newInstance();
        }
    }
    
    @Override
    protected boolean beforeSave() {
        if (!usuarioAtual.getBloqueio() && getInstance().getDataBloqueio() != null){
            getInstance().setDataDesbloqueio(new Date());
        } else {
            getInstance().setUsuario(usuarioAtual);
            getInstance().setDataBloqueio(new Date());
        }
        return super.beforeSave();
    }
    
    @Override
    public void newInstance() {
    	super.newInstance();
    }
    
    @Override
    protected void afterSave() {
    	super.afterSave();
    	this.isUsuarioAtualBloqueado = existeBloqueioAtivo();
    }
    
    private boolean existeBloqueioAtivo(){
        BloqueioUsuario ultimoBloqueio = bloqueioUsuarioManager.getUltimoBloqueio(usuarioAtual);
        if (ultimoBloqueio != null){
            return ultimoBloqueio.getDataDesbloqueio() == null;
        } else {
            return false;
        }
    }

    public boolean isUsuarioAtualBloqueado() {
		return this.isUsuarioAtualBloqueado;
	}
}
