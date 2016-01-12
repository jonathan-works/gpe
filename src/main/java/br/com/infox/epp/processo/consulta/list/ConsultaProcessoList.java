package br.com.infox.epp.processo.consulta.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.painel.FluxoBean;
import br.com.infox.epp.painel.PanelDefinition;
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
    
    private static Comparator<TaskBean> TASK_COMPARATOR = new Comparator<TaskBean>() {

        @Override
        public int compare(TaskBean taskBean1, TaskBean taskBean2) {
            int pesoCompare = taskBean1.getPesoPrioridadeProcesso().compareTo(taskBean2.getPesoPrioridadeProcesso());
            if (pesoCompare != 0) {
                return pesoCompare;
            } else {
                return taskBean1.getDataInicio().compareTo(taskBean2.getDataInicio());
            }
        }
    };
    
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
    private List<TaskBean> filteredTasks;
    private FluxoBean fluxoBean;
    
    public void onSelectFluxo(FluxoBean fluxoBean) {
        this.fluxoBean = fluxoBean;
        this.dynamicColumns = null;
    }
    
    public void onSelectNode(PanelDefinition panelDefinition) {
        this.tasks = panelDefinition.getTasks();
        this.filteredTasks = new ArrayList<>(tasks);
    }
    
    @Override
    public List<TaskBean> getResultList() {
        if (tasks != null) {
            applyFilters();
            applySort();
        }
        return filteredTasks;
    }

    private void applyFilters() {
        List<TaskBean> tasksToRemove = new ArrayList<>();
        for (TaskBean taskBean : tasks) {
            if (numeroProcesso != null && !taskBean.getNumeroProcesso().contains(numeroProcesso)) {
                tasksToRemove.add(taskBean);
            } else if (numeroProcessoRoot != null && !taskBean.getNumeroProcessoRoot().contains(numeroProcessoRoot)) {
                tasksToRemove.add(taskBean);
            } else if (natureza != null && !taskBean.getNomeNatureza().equals(natureza.getNatureza())) {
                tasksToRemove.add(taskBean);
            } else if (categoria != null && !taskBean.getNomeCategoria().equals(categoria.getCategoria())) {
                tasksToRemove.add(taskBean);
            } else if (dataInicio != null && !isDataMaiorIgual(taskBean.getDataInicio(), dataInicio)){
                tasksToRemove.add(taskBean);
            } else if (dataFim != null && !isDataMenorIgual(taskBean.getDataInicio(), dataFim)) {
                tasksToRemove.add(taskBean);
            }
        }
        filteredTasks.removeAll(tasksToRemove);
    }
    
    private void applySort() {
        Collections.sort(filteredTasks, TASK_COMPARATOR);
    }

    @Override
    public void newInstance() {
        filteredTasks = null;
        dynamicColumns = null;
    }

    @Override
    public boolean isNextExists() {
        return getPage() * getMaxResults() < filteredTasks.size();
    }
    
    @Override
    public boolean isPreviousExists() {
        return filteredTasks.size() > getMaxResults();
    }
    
    @Override
    public Long getResultCount() {
        return (long) getResultList().size();
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
    
    private boolean isDataMaiorIgual(Date date1, Date date2) {
        date1 = DateUtil.getBeginningOfDay(date1);
        date2 = DateUtil.getBeginningOfDay(date2);
        return date1.getTime() >= date2.getTime();
    }
    
    private boolean isDataMenorIgual(Date date1, Date date2) {
        date1 = DateUtil.getBeginningOfDay(date1);
        date2 = DateUtil.getBeginningOfDay(date2);
        return date1.getTime() <= date2.getTime();
    }
    
    public List<TaskBean> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskBean> tasks) {
        this.tasks = tasks;
    }
    
    public List<TaskBean> getFilteredTasks() {
        return filteredTasks;
    }

    public FluxoBean getFluxoBean() {
        return fluxoBean;
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
