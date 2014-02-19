package br.com.infox.epp.processo.consulta.action;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;

@Name(ConsultaController.NAME)
public class ConsultaController extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaController";
    
    private ProcessoEpa processoEpa;
    @In private ProcessoEpaManager processoEpaManager;
    
    @Override
    public void setId(Object id) {
        this.setProcessoEpa(processoEpaManager.find(Integer.valueOf((String)id)));
        super.setId(id);
        
    }

    public ProcessoEpa getProcessoEpa() {
        return processoEpa;
    }

    public void setProcessoEpa(ProcessoEpa processoEpa) {
        this.processoEpa = processoEpa;
    }

}
