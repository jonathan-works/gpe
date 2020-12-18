package br.com.infox.epp.gdprev.vidafuncional;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class VidaFuncionalGDPrevDataModel extends LazyDataModel<DocumentoVidaFuncionalDTO> {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(DocumentoVidaFuncionalDTO.class);

    @Inject
    private VidaFuncionalGDPrevSearch vidaFuncionalGDPrevSearch;

    @Getter
    @Setter
    private FiltroVidaFuncionalGDPrev filtroVidaFuncionalGDPrev = new FiltroVidaFuncionalGDPrev();
    @Getter
    @Setter
    private Integer idProcesso;

    @Override
    public List<DocumentoVidaFuncionalDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        if (getWrappedData() == null || isPaginationOrSorting()) {
            Integer pagina = Math.floorDiv(first, pageSize) + 1;
            try {
                VidaFuncionalGDPrevResponseDTO response = vidaFuncionalGDPrevSearch.getDocumentos(getFiltroVidaFuncionalGDPrev(), pagina, pageSize, getIdProcesso());
                setRowCount(response.getTotal());
                return response.getDocumentos();
            } catch (BusinessException e) {
                // Necessário para não repetir o log nem a mensagem para o usuário, pois o JSF chama este método novamente durante o render response
                boolean repetido = false;
                for (FacesMessage facesMessage : FacesContext.getCurrentInstance().getMessageList()) {
                    if (facesMessage.getSummary().equals(e.getMessage())) {
                        repetido = true;
                        break;
                    }
                }
                if (!repetido) {
                    LOG.error("Erro ao buscar documentos", e);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
                }
            }
        }
        return getResultList();
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

    @SuppressWarnings("unchecked")
    public List<DocumentoVidaFuncionalDTO> getResultList() {
        return (List<DocumentoVidaFuncionalDTO>) getWrappedData();
    }

    public void marcarComoBaixado(Long idDocumentoVidaFuncional) {
        if (getResultList() != null) {
            for (DocumentoVidaFuncionalDTO documentoVidaFuncional : getResultList()) {
                if (documentoVidaFuncional.getId().equals(idDocumentoVidaFuncional)) {
                    documentoVidaFuncional.setBaixado(true);
                    break;
                }
            }
        }
    }
}
