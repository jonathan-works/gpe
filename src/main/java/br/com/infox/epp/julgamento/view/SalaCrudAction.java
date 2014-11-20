package br.com.infox.epp.julgamento.view;

import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.manager.SalaManager;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraColegiadaManager;

@Name(SalaCrudAction.NAME)
public class SalaCrudAction extends AbstractCrudAction<Sala, SalaManager> {

    @In
    private UnidadeDecisoraColegiadaManager unidadeDecisoraColegiadaManager;
    
    public List<UnidadeDecisoraColegiada> getUnidadeDecisoraColegiadaList() {
        return unidadeDecisoraColegiadaManager.findAll();
    }

    public boolean selectUDC(ValueChangeEvent event) {
        
        return true;
    }
    
    public static final String NAME = "salaCrudAction";
    private static final long serialVersionUID = 1L;
    
}
