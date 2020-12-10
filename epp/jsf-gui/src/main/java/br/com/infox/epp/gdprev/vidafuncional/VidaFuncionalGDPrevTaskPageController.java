package br.com.infox.epp.gdprev.vidafuncional;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.context.def.VariableAccess;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.variable.components.AbstractTaskPageController;
import br.com.infox.ibpm.variable.components.Taskpage;
import br.com.infox.seam.exception.BusinessException;
import lombok.Getter;

@Taskpage(
        id = VidaFuncionalGDPrevTaskPageController.TASKPAGE_ID,
        name = VidaFuncionalGDPrevTaskPageController.TASKPAGE_NAME,
        description = "Consulta à vida funcional no GDPrev",
        xhtmlPath = "/WEB-INF/taskpages/gdprev/vidaFuncional/vidaFuncional.xhtml"
        )
@Named
@ViewScoped
public class VidaFuncionalGDPrevTaskPageController extends AbstractTaskPageController implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TASKPAGE_ID = "vidaFuncionalGDPrev";
    public static final String TASKPAGE_NAME = "Buscar Vida Funcional no GDPrev";

    @Inject
    private VidaFuncionalGDPrevDataModel vidaFuncionalGDPrevDataModel;

    @Getter
    private String label;

    @Getter
    private FiltroVidaFuncionalGDPrev filtroVidaFuncionalGDPrev;

    @PostConstruct
    private void init() {
        this.label = TASKPAGE_NAME;
        this.filtroVidaFuncionalGDPrev = new FiltroVidaFuncionalGDPrev();
        for (VariableAccess variableAccess : getTaskInstance().getTask().getTaskController().getVariableAccesses()) {
            if (variableAccess.getVariableName().equals(TASKPAGE_ID) && variableAccess.getType().equals(VariableType.TASK_PAGE.name())) {
                this.label = variableAccess.getLabel();
                break;
            }
        }
    }

    @ExceptionHandled
    public void buscarDocumentos() {
        if (getFiltroVidaFuncionalGDPrev().isEmpty()) {
            throw new BusinessException("É necessário informar o nome, CPF e/ou matrícula do servidor");
        }
        vidaFuncionalGDPrevDataModel.setFiltroVidaFuncionalGDPrev(getFiltroVidaFuncionalGDPrev());
        vidaFuncionalGDPrevDataModel.search();
    }
}
