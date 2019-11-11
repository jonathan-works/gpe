package br.com.infox.epp.quartz.log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class ConsultaLogQuartzList extends DataList<LogQuartz>{

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_JPQL = "select o from LogQuartz o";
	private static final String DEFAULT_ORDER = "o.dataInicioProcessamento desc";

	private String jobName;
	private String expressao;
	private String instancia;
	private Date dataInicioIntervalo;
	private Date dataFimIntervalo;
	
	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_JPQL;
	}
	
    @Override
    protected void addRestrictionFields() {
        addRestrictionField("jobName", RestrictionType.contendo);
        addRestrictionField("expressao", RestrictionType.contendo);
        addRestrictionField("instancia", RestrictionType.contendo);
        addRestrictionField("dataInicioIntervalo", "o.dataInicioProcessamento >= #{consultaLogQuartzList.dataInicioIntervalo}");
        addRestrictionField("dataFimIntervalo", "o.dataInicioProcessamento <= #{consultaLogQuartzList.dataFimIntervalo}");
        
    }
    
    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        Map<String, String> mapOrder = new HashMap<>();
        mapOrder.put("jobName", "o.jobName");
        mapOrder.put("expressao", "o.expressao");
        mapOrder.put("instancia", "o.instancia");
        mapOrder.put("dataInicioProcessamento", "o.dataInicioProcessamento");
        mapOrder.put("dataFimProcessamento", "o.dataFimProcessamento");
        return mapOrder;
    }

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getExpressao() {
		return expressao;
	}

	public void setExpressao(String expressao) {
		this.expressao = expressao;
	}

	public String getInstancia() {
		return instancia;
	}

	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}

	public Date getDataInicioIntervalo() {
		return dataInicioIntervalo;
	}

	public void setDataInicioIntervalo(Date dataInicioIntervalo) {
		this.dataInicioIntervalo = dataInicioIntervalo;
	}

	public Date getDataFimIntervalo() {
		return dataFimIntervalo;
	}

	public void setDataFimIntervalo(Date dataFimIntervalo) {
		this.dataFimIntervalo = dataFimIntervalo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
