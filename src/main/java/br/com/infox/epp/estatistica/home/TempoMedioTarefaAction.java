package br.com.infox.epp.estatistica.home;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.estatistica.entity.TempoMedioProcesso;
import br.com.infox.epp.estatistica.manager.TempoMedioProcessoManager;

@Name(TempoMedioTarefaAction.NAME)
public class TempoMedioTarefaAction extends AbstractController {
    public static final String NAME = "tempoMedioTarefaAction";
    
    private List<TempoMedioProcesso> listTempoMedioProcesso;
    
    @In TempoMedioProcessoManager tempoMedioProcessoManager;
    
    public List<TempoMedioProcesso> getListTempoMedioProcesso() {
        if (listTempoMedioProcesso == null) {
            listTempoMedioProcesso = tempoMedioProcessoManager.findAll();
        }
        return listTempoMedioProcesso;
    }

    public void setListTempoMedioProcesso(List<TempoMedioProcesso> listTempoMedioProcesso) {
        this.listTempoMedioProcesso = listTempoMedioProcesso;
    }

}
