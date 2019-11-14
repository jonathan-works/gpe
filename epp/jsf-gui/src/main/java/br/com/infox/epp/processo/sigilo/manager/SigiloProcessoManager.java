package br.com.infox.epp.processo.sigilo.manager;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.dao.SigiloProcessoDAO;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;

@Stateless
@AutoCreate
@Name(SigiloProcessoManager.NAME)
public class SigiloProcessoManager extends Manager<SigiloProcessoDAO, SigiloProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloProcessoManager";

    public SigiloProcesso getSigiloProcessoAtivo(Processo processo) {
        return getDao().getSigiloProcessoAtivo(processo);
    }
    
    public SigiloProcesso getSigiloProcessoUsuario(Processo processo,UsuarioLogin usuarioLogin) {
    	return getDao().getSigiloProcessoUsuario(processo, usuarioLogin);
    }
    
    public boolean isSigiloso(Processo processo) {
        SigiloProcesso sigiloProcesso = getSigiloProcessoAtivo(processo);
        if (sigiloProcesso != null) {
            return sigiloProcesso.getSigiloso();
        }
        return false;
    }
}
