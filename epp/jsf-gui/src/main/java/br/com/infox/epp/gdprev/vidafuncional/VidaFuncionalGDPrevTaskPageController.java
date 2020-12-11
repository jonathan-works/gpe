package br.com.infox.epp.gdprev.vidafuncional;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.context.def.VariableAccess;

import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.tipoParte.TipoParteSearch;
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
    @Inject
    private ParticipanteProcessoManager participanteProcessoManager;
    @Inject
    private TipoParteSearch tipoParteSearch;

    @Getter
    private String label;
    @Getter
    private FiltroVidaFuncionalGDPrev filtroVidaFuncionalGDPrev;
    private TipoParte tipoParteInteressadoServidor;
    private String cpfInteressadoServidor;
    private boolean semInteressadoServidor;

    @PostConstruct
    private void init() {
        this.label = TASKPAGE_NAME;
        this.filtroVidaFuncionalGDPrev = new FiltroVidaFuncionalGDPrev();
        configurarLabelTaskPage();
        buscarTipoParteInteressadoServidor();
        buscarParticipanteInteressadoServidor();
    }

    private void buscarParticipanteInteressadoServidor() {
        List<ParticipanteProcesso> participantesInteressadosServidor = participanteProcessoManager.getParticipantesByTipo(getProcesso(), tipoParteInteressadoServidor);
        this.semInteressadoServidor = participantesInteressadosServidor.isEmpty();
        if (participantesInteressadosServidor.size() == 1) {
            this.cpfInteressadoServidor = participantesInteressadosServidor.get(0).getPessoa().getCodigo();
        } else {
            this.cpfInteressadoServidor = null;
        }
    }

    private void configurarLabelTaskPage() {
        for (VariableAccess variableAccess : getTaskInstance().getTask().getTaskController().getVariableAccesses()) {
            if (variableAccess.getVariableName().equals(TASKPAGE_ID) && variableAccess.getType().equals(VariableType.TASK_PAGE.name())) {
                this.label = variableAccess.getLabel();
                break;
            }
        }
    }

    private void buscarTipoParteInteressadoServidor() {
        String codigoTipoParteInteressadoServidor = Parametros.TIPO_PARTE_SERVIDOR.getValue();
        if (StringUtil.isEmpty(codigoTipoParteInteressadoServidor)) {
            throw new EppConfigurationException(String.format("O parâmetro '%s' não está configurado", Parametros.TIPO_PARTE_SERVIDOR.getLabel()));
        }
        this.tipoParteInteressadoServidor = tipoParteSearch.getTipoParteByIdentificador(codigoTipoParteInteressadoServidor);
        if (this.tipoParteInteressadoServidor == null) {
            throw new EppConfigurationException(String.format("Não foi encontrado tipo de parte com o código '%s'", codigoTipoParteInteressadoServidor));
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

    public String getAvisoErroInteressadoServidor() {
        if (cpfInteressadoServidor != null) {
            return null;
        }
        if (semInteressadoServidor) {
            return String.format("Não foi encontrado participante do tipo %s", tipoParteInteressadoServidor.getDescricao());
        } else {
            return String.format("Foi encontrado mais de um participante do tipo %s", tipoParteInteressadoServidor.getDescricao());
        }
    }
}
