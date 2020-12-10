package br.com.infox.epp.gdprev.vidafuncional;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.jsf.util.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class VidaFuncionalGDPrevDataModel extends LazyDataModel<DocumentoVidaFuncionalDTO> {

    private static final long serialVersionUID = 1L;

    @Inject
    private VidaFuncionalGDPrevSearch vidaFuncionalGDPrevSearch;

    @Getter
    @Setter
    private FiltroVidaFuncionalGDPrev filtroVidaFuncionalGDPrev = new FiltroVidaFuncionalGDPrev();

    @SuppressWarnings("unchecked")
    @Override
    public List<DocumentoVidaFuncionalDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        if (getWrappedData() == null || isPaginationOrSorting()) {
            Integer pagina = Math.floorDiv(first, pageSize) + 1;
            VidaFuncionalGDPrevResponseDTO response = vidaFuncionalGDPrevSearch.getDocumentos(getFiltroVidaFuncionalGDPrev(), pagina, pageSize);
            setRowCount(response.getTotal());
            return response.getDocumentos();
        }
        return (List<DocumentoVidaFuncionalDTO>) getWrappedData();
    }

    public void search() {
        setWrappedData(null);
        getDataTable().reset();
        getDataTable().setRows(20);
        getDataTable().loadLazyData();
    }

    public void setDataTable(DataTable dataTable) {
        JsfUtil.instance().setRequestValue(getClass().getName() + "_binding", dataTable);
    }

    public DataTable getDataTable() {
        return JsfUtil.instance().getRequestValue(getClass().getName() + "_binding", DataTable.class);
    }

    private boolean isPaginationOrSorting() {
        String componentClientId = getDataTable().getClientId();
        JsfUtil jsfUtil = JsfUtil.instance();
        return jsfUtil.getRequestParameter(componentClientId + "_first") != null
                || jsfUtil.getRequestParameter(componentClientId + "_rows") != null
                || jsfUtil.getRequestParameter(componentClientId + "_sorting") != null;
    }
}
