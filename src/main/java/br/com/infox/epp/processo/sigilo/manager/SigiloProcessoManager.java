package br.com.infox.epp.processo.sigilo.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.dao.SigiloProcessoDAO;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;

@Name(SigiloProcessoManager.NAME)
@Scope(ScopeType.EVENT)
@Stateless
@AutoCreate
public class SigiloProcessoManager extends Manager<SigiloProcessoDAO, SigiloProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloProcessoManager";

    public SigiloProcesso getSigiloProcessoAtivo(Processo processo) {
        return getDao().getSigiloProcessoAtivo(processo);
    }
    
    public SigiloProcesso getSigiloProcessoUsuario(Processo processo,UsuarioLogin usuarioLogin) {
    	return getDao().getSigiloProcessoUsuario(processo, usuarioLogin);
    }
    
    public List<SigiloProcesso> getSigilosProcesso(Processo processo) {
    	return getDao().getSigilosProcessoAtivo(processo);
    }

    public boolean isSigiloso(Processo processo) {
        SigiloProcesso sigiloProcesso = getSigiloProcessoAtivo(processo);
        if (sigiloProcesso != null) {
            return sigiloProcesso.getSigiloso();
        }
        return false;
    }
}
