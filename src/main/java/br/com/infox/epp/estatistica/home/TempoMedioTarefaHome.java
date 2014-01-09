package br.com.infox.epp.estatistica.home;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.estatistica.entity.TempoMedioProcesso;
import br.com.infox.epp.estatistica.entity.TempoMedioTarefa;
import br.com.infox.epp.estatistica.manager.TempoMedioProcessoManager;
import br.com.itx.component.AbstractHome;

@Name(TempoMedioTarefaHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class TempoMedioTarefaHome extends AbstractHome<TempoMedioTarefa> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "tempoMedioTarefaHome";
    
    private List<TempoMedioProcesso> listTempoMedioProcesso;
    
    @In TempoMedioProcessoManager tempoMedioProcessoManager;
    
    public List<TempoMedioProcesso> getListTempoMedioProcesso() {
        if (listTempoMedioProcesso == null) {
            listTempoMedioProcesso = tempoMedioProcessoManager.findAll();
        }
        return listTempoMedioProcesso;
    }

    public void setListTempoMedioProcesso(
            List<TempoMedioProcesso> listTempoMedioProcesso) {
        this.listTempoMedioProcesso = listTempoMedioProcesso;
    }
    
}
