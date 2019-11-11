package br.com.infox.epp.access.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.dao.PermissaoDAO;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Permissao;

@Name(PermissaoService.NAME)
@AutoCreate
@Stateless
public class PermissaoService extends Manager<PermissaoDAO, Permissao> {
    public static final String NAME = "permissaoService";
    private static final long serialVersionUID = 1L;
    
    @Inject
    private PapelManager papelManager;
    
    public Boolean papelPossuiPermissaoParaRecurso(Papel papel, String identificadorRecurso) {
        List<Permissao> permissoes = getDao().getByAlvo(identificadorRecurso);
        for (Permissao permissao : permissoes) {
        	if (papelManager.isPapelHerdeiro(papel.getIdentificador(), permissao.getDestinatario())) {
        		return true;
        	}
        }
        return false;
    }
}