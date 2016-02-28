package br.com.infox.epp.log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.core.log.ErrorLogService;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;

@Named
@ViewScoped
public class ConsultaLogErroList extends DataList<LogErro> {

    private static final long serialVersionUID = 1L;
    
    private static final String DEFAULT_JPQL = "select o from LogErro o";
    
    private static final String DEFAULT_ORDER = "o.data desc";
    
    @Inject
    private ErrorLogService errorLogService;
    
    private String codigo;
    private String instancia;
    private Date dataInicio;
    private Date dataFim;
    private Date dataEnvio;
    private StatusLog status;
    
    @Override
    protected void addRestrictionFields() {
        addRestrictionField("codigo", RestrictionType.contendo);
        addRestrictionField("instancia", RestrictionType.contendo);
        addRestrictionField("dataInicio", "o.data >= #{consultaLogErroList.dataInicio}");
        addRestrictionField("dataFim", "o.data <= #{consultaLogErroList.dataFim}");
        addRestrictionField("status", RestrictionType.igual);
    }
    
    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        Map<String, String> mapOrder = new HashMap<>();
        mapOrder.put("codigo", "o.codigo");
        mapOrder.put("instancia", "o.instancia");
        mapOrder.put("data", "o.data");
        mapOrder.put("status", "o.status");
        return mapOrder;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_JPQL;
    }
    
    public StatusLog[] getStatusList() {
        return StatusLog.values();
    }
    
    @ExceptionHandled(value = MethodType.UNSPECIFIED)
    public void send(LogErro logErro) {
        try {
            errorLogService.send(logErro, FacesContext.getCurrentInstance().getExternalContext().getRequestServerName());
            FacesMessages.instance().add("Registro enviado com sucesso!");
        } catch (Exception e) {
            FacesMessages.instance().add(e.getMessage());
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getInstancia() {
        return instancia;
    }

    public void setInstancia(String instancia) {
        this.instancia = instancia;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public StatusLog getStatus() {
        return status;
    }

    public void setStatus(StatusLog status) {
        this.status = status;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
    
}
