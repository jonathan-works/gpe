package br.com.infox.epp.access.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.dao.EstruturaDAO;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Localizacao;

@Name(EstruturaManager.NAME)
@AutoCreate
public class EstruturaManager extends Manager<EstruturaDAO, Estrutura> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "estruturaManager";
    
    public List<Estrutura> getEstruturasDisponiveisLocalizacao(Localizacao localizacao) {
        return getDao().getEstruturasDisponiveisLocalizacao(localizacao);
    }
    
    public List<Estrutura> getEstruturasDisponiveis() {
        return getDao().getEstruturasDisponiveis();
    }
}
