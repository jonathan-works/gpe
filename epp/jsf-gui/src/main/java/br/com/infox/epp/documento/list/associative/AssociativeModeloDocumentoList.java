package br.com.infox.epp.documento.list.associative;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.list.AbstractPageableList;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.ibpm.process.definition.fitter.TaskFitter;

@Named
@ViewScoped
public class AssociativeModeloDocumentoList extends AbstractPageableList<ModeloDocumento> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from ModeloDocumento o";

    @Inject
    private TaskFitter taskFitter;
    
    private List<ModeloDocumento> modelosAssociados = new ArrayList<>();
    
    private boolean validateFitter() {
        return taskFitter == null
                || taskFitter.getCurrentTask() == null
                || taskFitter.getCurrentTask().getCurrentVariable() == null
                || taskFitter.getCurrentTask().getCurrentVariable().getModeloEditorHandler().getModeloDocumentoList() == null
                || taskFitter.getCurrentTask().getCurrentVariable().getModeloEditorHandler().getModeloDocumentoList().isEmpty();
    }

    @Override
    protected void initCriteria() {
        addSearchCriteria("tituloModeloDocumento", "lower(o.tituloModeloDocumento) like concat('%', lower(:tituloModeloDocumento), '%')");
        addSearchCriteria("tipoModeloDocumento", "o.tipoModeloDocumento = :tipoModeloDocumento");
        addSearchCriteria("ativo", "o.ativo = :ativo");
    }

    @Override
    protected String getQuery() {
        return DEFAULT_EJBQL;
    }

    public void refreshModelosAssociados() {
        modelosAssociados.clear();
        removeParameter("modelosAssociados");
        removeSearchCriteria("modelosAssociados");
        if (!validateFitter()) {
            addSearchCriteria("modelosAssociados", "o not in :modelosAssociados");
            modelosAssociados.addAll(taskFitter.getCurrentTask().getCurrentVariable().getModeloEditorHandler().getModeloDocumentoList());
            addParameter("modelosAssociados", modelosAssociados);
        }
    }
    
    @Override
    public void newInstance() {
        super.newInstance();
        refreshModelosAssociados();
    }
}
