package br.com.infox.epp.processo.consulta.list;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.componentes.column.DynamicColumnModel;
import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.painel.FluxoBean;
import br.com.infox.epp.painel.TaskBean;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;

@Named
@ViewScoped
public class ConsultaProcessoList extends DataList<TaskBean> {

    private static final long serialVersionUID = 1L;
    
    private static final String DYNAMIC_COLUMN_EXPRESSION = "#{consultaProcessoList.getVariavelProcesso(row.idProcesso, '%s', row.idTaskInstance).valor}";

    private static final String DEFAULT_EJBQL = "select o from ProcessoTarefa pt inner join pt.processo o left join o.prioridadeProcesso pp ";
    private static final String DEFAULT_ORDER = "coalesce(pp.peso, -1) DESC, pt.dataInicio ASC";
    
    @Inject
    protected VariavelProcessoService variavelProcessoService;
    @Inject
    protected DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;

    private String numeroProcesso;
    private String numeroProcessoRoot;
    private Natureza natureza;
    private Categoria categoria;
    private Date dataInicio;
    private Date dataFim;
    
    private List<DynamicColumnModel> dynamicColumns;
    private List<TaskBean> tasks;
    private FluxoBean fluxoBean;
    
    @Override
    public List<TaskBean> getResultList() {
        return tasks;
    }
    
    @Override
    protected void addRestrictionFields() {
//        addRestrictionField("numeroProcessoRoot", R1);
//        addRestrictionField("natureza", R2);
//        addRestrictionField("categoria", R3);
        addRestrictionField("dataInicio", RestrictionType.maiorIgual);
        addRestrictionField("dataFim", RestrictionType.menorIgual);
    }
    
    public List<DynamicColumnModel> getDynamicColumns() {
        if (dynamicColumns == null) {
            updateDatatable();
        }
        return dynamicColumns;
    }

    private void updateDatatable() {
        if (dynamicColumns == null) return;
        if (fluxoBean != null) {
            dynamicColumns = new ArrayList<>();
            Integer idFluxo = Integer.valueOf(fluxoBean.getProcessDefinitionId());
            List<DefinicaoVariavelProcesso> definicoes = definicaoVariavelProcessoManager.getDefinicaoVariavelProcessoVisivelPainel(idFluxo);
            for (DefinicaoVariavelProcesso definicao : definicoes) {
                DynamicColumnModel columnModel = new DynamicColumnModel(definicao.getLabel(), String.format(DYNAMIC_COLUMN_EXPRESSION, definicao.getNome()));
                dynamicColumns.add(columnModel);
            }
        }
    }
    
    public VariavelProcesso getVariavelProcesso(Integer idProcesso, String nome, Long idTaskInstance) {
        return variavelProcessoService.getVariavelProcesso(idProcesso, nome, idTaskInstance);
    }
    
    public Object getVariavelProcesso(TaskBean taskBean, String nome) {
        Token token = EntityManagerProducer.getEntityManager().find(TaskInstance.class, taskBean.getIdTaskInstance()).getToken();
        ExecutionContext executionContext = new ExecutionContext(token);
        Object object = JbpmExpressionEvaluator.evaluate("#{" + nome + "}", executionContext);
        return object;
    }
    
    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }
    
    public List<TaskBean> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskBean> tasks) {
        this.tasks = tasks;
    }
    
    public FluxoBean getFluxoBean() {
        return fluxoBean;
    }

    public void setFluxoBean(FluxoBean fluxoBean) {
        this.fluxoBean = fluxoBean;
    }
    
    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroProcessoRoot() {
        return numeroProcessoRoot;
    }

    public void setNumeroProcessoRoot(String numeroProcessoRoot) {
        this.numeroProcessoRoot = numeroProcessoRoot;
    }

    public Natureza getNatureza() {
        return natureza;
    }

    public void setNatureza(Natureza natureza) {
        this.natureza = natureza;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
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
    
}
