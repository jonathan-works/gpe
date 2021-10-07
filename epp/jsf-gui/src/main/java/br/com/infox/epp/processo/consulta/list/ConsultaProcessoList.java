package br.com.infox.epp.processo.consulta.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ValidationException;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.primefaces.context.RequestContext;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.componentes.column.DynamicColumnModel;
import br.com.infox.core.list.DataList;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.DateUtil;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.DadosAssinatura;
import br.com.infox.epp.assinador.assinavel.AssinavelDocumentoBinProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.TaskInstanceListagemDocumentoDTO;
import br.com.infox.epp.documento.TaskInstancePermitidaAssinarDocumentoSearch;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.documento.type.TipoMeioAssinaturaEnum;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoSearch;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.painel.FluxoBean;
import br.com.infox.epp.painel.PanelDefinition;
import br.com.infox.epp.painel.TaskBean;
import br.com.infox.epp.processo.list.FiltroVariavelProcessoSearch;
import br.com.infox.epp.processo.list.FiltroVariavelProcessoVO;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;
import br.com.infox.ibpm.sinal.SignalService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.ApplicationException;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ConsultaProcessoList extends DataList<TaskBean> {

    private static final long serialVersionUID = 1L;

    private static final LogProvider LOG = Logging.getLogProvider(ConsultaProcessoList.class);
    
    private static final String DYNAMIC_COLUMN_EXPRESSION = "#{consultaProcessoList.getVariavelProcesso(row.idProcesso, '%s', row.idTaskInstance).valor}";
    public static final String SINAL_ASSINATURA_LOTE_PAINEL_USUARIO = "assinaturaDocumentosEmLote";

    private static Comparator<TaskBean> TASK_COMPARATOR = new Comparator<TaskBean>() {

        @Override
        public int compare(TaskBean taskBean1, TaskBean taskBean2) {
            int pesoCompare = taskBean2.getPesoPrioridadeProcesso().compareTo(taskBean1.getPesoPrioridadeProcesso());
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
    protected DefinicaoVariavelProcessoSearch definicaoVariavelProcessoSearch;
    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private PapelManager papelManager;
    @Inject
    private TaskInstancePermitidaAssinarDocumentoSearch taskInstancePermitidaAssinarDocumentoSearch;
    @Inject
	private ClassificacaoDocumentoPapelManager classificacaoDocumentoPapelManager;
    @Inject
	private AssinadorService assinadorService;
    @Inject
    private DocumentoBinManager documentoBinManager;
    @Inject
	private SignalService signalService;

    private String numeroProcesso;
    private String numeroProcessoRoot;
    private Natureza natureza;
    private Categoria categoria;
    private Date dataInicio;
    private Date dataFim;

    @Getter @Setter
    private Long idTipoFiltroVariavelProcesso;
    @Getter @Setter
    private Object valorFiltroVariavelProcesso;
    @Getter @Setter
    private Object valorFiltroVariavelProcessoComplemento;
    @Getter @Setter
    private FiltroVariavelProcessoVO filtroVariavelSelecionado;
    @Getter
    private List<FiltroVariavelProcessoVO> filtrosVariaveisProcesso;

    private List<DynamicColumnModel> dynamicColumns;
    private List<TaskBean> filteredTasks;
    private FluxoBean fluxoBean;
    private PanelDefinition panelDefinition;

    @Getter
    private TaskInstanceListagemDocumentoDTO listagemDocumentoAssinatura;
    @Getter @Setter
    private String tokenAssinatura;
    @Getter @Setter
    private boolean showMarcarTodosAssinaveis;
    
    @Inject
    private FiltroVariavelProcessoSearch filtroVariavelProcessoSearch;

    public void onChangeTipoFiltroVariavelProcesso() {
        this.filtroVariavelSelecionado = null;
        setValorFiltroVariavelProcesso(null);
        setValorFiltroVariavelProcessoComplemento(null);
        if(getIdTipoFiltroVariavelProcesso() != null) {
            for (FiltroVariavelProcessoVO fvp : this.filtrosVariaveisProcesso) {
                if(getIdTipoFiltroVariavelProcesso().equals(fvp.getValue())) {
                    this.filtroVariavelSelecionado = fvp;
                }
            }
        }
    }

    public void onSelectFluxo(FluxoBean fluxoBean) {
        this.fluxoBean = fluxoBean;
        this.filtrosVariaveisProcesso = filtroVariavelProcessoSearch.getFiltros(Integer.parseInt(fluxoBean.getProcessDefinitionId()));
        this.dynamicColumns = null;
    }

    public void onSelectNode(PanelDefinition panelDefinition) {
        this.panelDefinition = panelDefinition;
        newInstance();
    }

    public void search() {
        filteredTasks = new ArrayList<>(getTasks());
        applyFilters();
        applySort();
    }

    @Override
    public List<TaskBean> getResultList() {
        return truncateList(filteredTasks);
    }

    @SuppressWarnings("unchecked")
    private void applyFilters() {
        List<TaskBean> tasksToRemove = new ArrayList<>();

        for (TaskBean taskBean : getTasks()) {
            if (numeroProcesso != null && !taskBean.getNumeroProcesso().contains(numeroProcesso)) {
                tasksToRemove.add(taskBean);
            } else if (numeroProcessoRoot != null && !taskBean.getNumeroProcessoRoot().contains(numeroProcessoRoot)) {
                tasksToRemove.add(taskBean);
            } else if (natureza != null && !taskBean.getNomeNaturezaProcessoRoot().equals(natureza.getNatureza())) {
                tasksToRemove.add(taskBean);
            } else if (categoria != null && !taskBean.getNomeCategoriaProcessoRoot().equals(categoria.getCategoria())) {
                tasksToRemove.add(taskBean);
            } else if (dataInicio != null && !DateUtil.isDataMaiorIgual(taskBean.getDataInicio(), dataInicio)){
                tasksToRemove.add(taskBean);
            } else if (dataFim != null && !DateUtil.isDataMenorIgual(taskBean.getDataInicio(), dataFim)) {
                tasksToRemove.add(taskBean);
            } else if(getIdTipoFiltroVariavelProcesso() != null &&
                (getValorFiltroVariavelProcesso() != null || getValorFiltroVariavelProcessoComplemento() != null)
            ){
                String hqlFiltroProcesso = "select o.idProcesso from Processo o where o.idProcesso in (:processos) ";
                hqlFiltroProcesso = filtroVariavelProcessoSearch
                    .getHqlFiltroVariavelProcesso(hqlFiltroProcesso
                        , getIdTipoFiltroVariavelProcesso()
                        , getValorFiltroVariavelProcesso()
                        , getValorFiltroVariavelProcessoComplemento()
                    );

                Set<Integer> processos = getTasks().stream().map(TaskBean::getIdProcesso).collect(Collectors.toSet());
                Set<Integer> processosFiltrados = new HashSet<Integer>(getEntityManager()
                    .createQuery(hqlFiltroProcesso)
                    .setParameter("processos", processos)
                    .getResultList()
                );

                if (!processosFiltrados.contains(taskBean.getIdProcesso())) {
                    tasksToRemove.add(taskBean);
                } else {
                    applyExtraFilters(taskBean, tasksToRemove);
                }
            } else {
                applyExtraFilters(taskBean, tasksToRemove);
            }
        }

        filteredTasks.removeAll(tasksToRemove);
    }

    protected void applyExtraFilters(TaskBean taskBean, List<TaskBean> tasksToRemove) {}

    private void applySort() {
        Collections.sort(filteredTasks, TASK_COMPARATOR);
    }

    private List<TaskBean> truncateList(List<TaskBean> resultList) {
        if (getResultCount() > getMaxResults()) {
            int lastIndex = getLastIndex() > resultList.size() ? resultList.size() : getLastIndex();
            return resultList.subList(getFirstIndex(), lastIndex);
        }
        return resultList;
    }

    private int getFirstIndex() {
        return (getPage() - 1) * getMaxResults();
    }

    private int getLastIndex() {
        return getPage() * getMaxResults();
    }

    @Override
    public void newInstance() {
    	listagemDocumentoAssinatura = new TaskInstanceListagemDocumentoDTO();
        filteredTasks = new ArrayList<>(getTasks());

        setValorFiltroVariavelProcesso(null);
        setValorFiltroVariavelProcessoComplemento(null);
        setIdTipoFiltroVariavelProcesso(null);
        setFiltroVariavelSelecionado(null);

        setNumeroProcesso(null);
        setNumeroProcessoRoot(null);
        setNatureza(null);
        setCategoria(null);
        setDataInicio(null);
        setDataFim(null);
        clearExtraFilters();
        setPage(1);
        search();
        buildExibirSelecaoAssinarDocumentosLote(filteredTasks);
        RequestContext.getCurrentInstance().execute("enableDisableAssinarButton();");
    }
    
    public void buildExibirSelecaoAssinarDocumentosLote(List<TaskBean> listaTaskBean) {
    	showMarcarTodosAssinaveis = false;
		for(TaskBean taskBean : listaTaskBean) {
			taskBean.setExibirSelecaoAssinaturaLote(taskInstancePermitidaAssinarDocumentoSearch.taskPossuiDocumentoParaSerAssinado(taskBean.getIdTaskInstance()));
			if(!showMarcarTodosAssinaveis && taskBean.isExibirSelecaoAssinaturaLote()) {
				showMarcarTodosAssinaveis = true;
			}
		}
	}
    
    public void marcarAssinaveisNaoAssinaveis() {
    	List<String> listaIdTaskInstance = new ArrayList<String>();
    	for(TaskBean taskBean : filteredTasks) {
    		if(taskBean.isSelecaoAssinaturaLote()) {
    			listaIdTaskInstance.add(taskBean.getIdTaskInstance());
    		}
    	}
    	listagemDocumentoAssinatura = taskInstancePermitidaAssinarDocumentoSearch.getListaDocumentoDTOParaSeremAssinados(listaIdTaskInstance);
    }
    
	public void marcarTodosAssinaveis(AjaxBehaviorEvent event) {
		boolean selecionarTodos = ((HtmlSelectBooleanCheckbox)event.getSource()).isSelected();
		for (TaskBean taskBean : filteredTasks) {
			if (taskBean.isExibirSelecaoAssinaturaLote()) {
				taskBean.setSelecaoAssinaturaLote(selecionarTodos);
			}
		}
	}

    protected void clearExtraFilters() {}

    @Override
    public boolean isNextExists() {
        return getPage() * getMaxResults() < filteredTasks.size();
    }

    @Override
    public boolean isPreviousExists() {
        return getFirstIndex() > 0;
    }

    @Override
    public Long getResultCount() {
        return (long) filteredTasks.size();
    }

    public List<DynamicColumnModel> getDynamicColumns() {
        if (dynamicColumns == null) {
            updateDatatable();
        }
        return dynamicColumns;
    }

    private void updateDatatable() {
        if (fluxoBean != null) {
            dynamicColumns = new ArrayList<>();
            Integer idFluxo = Integer.valueOf(fluxoBean.getProcessDefinitionId());
            List<DefinicaoVariavelProcesso> definicoes = definicaoVariavelProcessoSearch.getDefinicoesVariaveis(fluxoManager.find(idFluxo),
            		DefinicaoVariavelProcessoRecursos.PAINEL_INTERNO.getIdentificador(), papelManager.isUsuarioExterno(Authenticator.getPapelAtual().getIdentificador()));
            for (DefinicaoVariavelProcesso definicao : definicoes) {
                DynamicColumnModel columnModel = new DynamicColumnModel(definicao.getLabel(), String.format(DYNAMIC_COLUMN_EXPRESSION, definicao.getNome()));
                dynamicColumns.add(columnModel);
            }
        }
    }

    public VariavelProcesso getVariavelProcesso(Integer idProcesso, String nome, String idTaskInstance) {
    	if (StringUtil.isEmpty(idTaskInstance)) {
    		return variavelProcessoService.getVariavelProcesso(idProcesso, nome);
    	} else {
    		return variavelProcessoService.getVariavelProcesso(idProcesso, nome, Long.parseLong(idTaskInstance));
    	}
    }

    public Object getVariavelProcesso(TaskBean taskBean, String expression) {
        Token token = EntityManagerProducer.getEntityManager().find(TaskInstance.class, Long.parseLong(taskBean.getIdTaskInstance())).getToken();
        ExecutionContext executionContext = new ExecutionContext(token);
        Object object = JbpmExpressionEvaluator.evaluate("#{"+expression+"}", executionContext);
        return object;
    }
    
    public AssinavelProvider getAssinavelProvider() {
	    List<AssinavelDocumentoBinProvider.DocumentoComRegraAssinatura> lista = new ArrayList<>();
        for (Documento documento : listagemDocumentoAssinatura.getListaDocumentoAssinavel()) {
            TipoMeioAssinaturaEnum tma = classificacaoDocumentoPapelManager.getTipoMeioAssinaturaUsuarioLogadoByClassificacaoDocumento(documento.getClassificacaoDocumento());
			lista.add(new AssinavelDocumentoBinProvider.DocumentoComRegraAssinatura(tma, documento.getDocumentoBin()));
        }

		return new AssinavelDocumentoBinProvider(lista);
	}
    
    public void signDocuments() {
		try {
			List<DadosAssinatura> dadosAssinaturaList = assinadorService.getDadosAssinatura(tokenAssinatura);

			UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
			for (DadosAssinatura dadosAssinatura : dadosAssinaturaList) {
				DocumentoBin docBin =  getDocumentoTemporarioByUuid(dadosAssinatura.getUuidDocumentoBin());
				if (docBin != null) {
					if (!isAssinadoPor(docBin, usuarioPerfil)) {
						assinadorService.assinar(dadosAssinatura, usuarioPerfil);
					}
				} else {
					throw new ApplicationException("Documento não localizado!");
				}
			}
			FacesMessages.instance().add(InfoxMessages.getInstance().get("anexarDocumentos.sucessoAssinatura"));
		}catch(AssinaturaException | ValidationException e){
			LOG.error("Erro signDocuments ", e);
			FacesMessages.instance().add(Severity.ERROR,e.getMessage());
		}catch (Exception e) {
			LOG.error("Erro signDocuments ", e);
			FacesMessages.instance().add(Severity.ERROR,
					InfoxMessages.getInstance().get("anexarDocumentos.erroAssinarDocumentos"));
		}
	}
    
    public void dispararSinalAssinaturaEmLote() {
    	for (TaskBean taskBean : filteredTasks) {
            signalService.dispatch(taskBean.getIdProcesso(), SINAL_ASSINATURA_LOTE_PAINEL_USUARIO);
        }
    }
    
    private DocumentoBin getDocumentoTemporarioByUuid(UUID uuid) {
		for (Documento documento : listagemDocumentoAssinatura.getListaDocumentoAssinavel()) {
			UUID wrapperUuid = documento.getDocumentoBin().getUuid();
			if (uuid.equals(wrapperUuid))
				return documentoBinManager.getByUUID(wrapperUuid);
		}
		return null;
	}
    
    private boolean isAssinadoPor(DocumentoBin docBin, UsuarioPerfil usuarioPerfil) {
		List<AssinaturaDocumento> assinaturas = docBin.getAssinaturas();
		if (assinaturas != null) {
			for (AssinaturaDocumento assinatura : assinaturas) {
				if (usuarioPerfil.getPerfilTemplate().getPapel().equals(assinatura.getPapel())) {
					return true;
				}
			}
		}
		return false;
	}

    @Override
    protected String getDefaultEjbql() {
        return "";
    }

    @Override
    protected String getDefaultOrder() {
        return "";
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

    public List<TaskBean> getTasks() {
        return panelDefinition.getTasks();
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
