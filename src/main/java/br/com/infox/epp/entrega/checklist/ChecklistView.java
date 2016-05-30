package br.com.infox.epp.entrega.checklist;

import java.io.Serializable;

import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.ibpm.variable.Taskpage;
import br.com.infox.ibpm.variable.TaskpageParameter;

@Named
@ViewScoped
@Taskpage(name = "checklist", description = "Checklist de entrega de documentos")
public class ChecklistView implements Serializable {
    private static final long serialVersionUID = 1L;

    @TaskpageParameter(name = "pastaChecklist", description = "checklist.parameter.pasta.description")
    private Pasta pasta;

    private void init() {
        
    }
}
