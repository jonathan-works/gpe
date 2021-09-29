package br.com.infox.epp.tarefa.component.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.richfaces.event.TreeSelectionChangeEvent;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.layout.manager.SkinSessaoManager;
import br.com.infox.epp.painel.CaixaDefinitionBean;
import br.com.infox.epp.painel.FluxoBean;
import br.com.infox.epp.painel.PainelUsuarioController;
import br.com.infox.epp.painel.TaskBean;
import br.com.infox.epp.painel.TaskDefinitionBean;

@Named
@ViewScoped
public class PainelTreeHandler extends AbstractTreeHandler<TaskDefinitionBean> {

    private static final long serialVersionUID = 1L;

    @Inject
    private PainelUsuarioController painelUsuarioController;
    @Inject
    private SkinSessaoManager skinSessaoManager;

    private List<PainelEntityNode> rootList;
    private List<PainelEntityNode> subCaixaList;
    private List<PainelEntityNode> subTarefaList;
    private Map<String, Collection<TaskDefinitionBean>> subMapa;

    private FluxoBean fluxoBean;

    @Override
    protected String getQueryRoots() {
        throw new IllegalStateException("Usar SituacaoProcessoDAO::createQueryRoots ao invés de TarefasTreeHanlder::getQueryRoots");
    }

    @Override
    protected String getQueryChildren() {
        throw new IllegalStateException("Usar SituacaoProcessoDAO::createQueryChildren ao invés de TarefasTreeHanlder::getQueryChildren");
    }

    @Override
    protected String getEventSelected() {
        return "selectedTarefasTree";
    }

    public void processTreeSelectionChange(TreeSelectionChangeEvent ev) {
    	super.processTreeSelectionChange(ev);
		painelUsuarioController.onSelectNode();
    }

    public List<PainelEntityNode> getTarefasRoots() {
        if (rootList == null) {
            rootList = new ArrayList<>();
            List<TaskDefinitionBean> ltTarefaFluxoRaiz = fluxoBean.getTaskDefinitions().values().stream()
                    .filter(d -> d.getTasks().stream().allMatch(t -> t.getNomeFluxo().equals(fluxoBean.getName()))).collect(Collectors.toList());
            for (TaskDefinitionBean taskDefinitionBean : ltTarefaFluxoRaiz) {
                rootList.add(new PainelEntityNode(null, taskDefinitionBean, PainelEntityNode.TASK_TYPE));
            }
        }
        return rootList;
    }

    public List<PainelEntityNode> getCaixasSub() {
        if (subCaixaList == null) {
            subCaixaList = new ArrayList<>();
            Map<String, Collection<TaskDefinitionBean>> mapa = getMapaSub();
            for (Entry<String, Collection<TaskDefinitionBean>> agrupador : mapa.entrySet()) {
                CaixaDefinitionBean caixaDefinitionBean = new CaixaDefinitionBean(null, agrupador.getKey(), null);
                subCaixaList.add(new PainelEntityNode(null, caixaDefinitionBean, PainelEntityNode.CAIXA_TYPE));
                for (TaskDefinitionBean taskDefinitionBean : agrupador.getValue()) {
                    caixaDefinitionBean.getTasks().addAll(taskDefinitionBean.getTasks());
                }
            }
            subCaixaList.sort((o1, o2) -> o1.getEntity().getName().compareTo(o2.getEntity().getName()));
        }
        return subCaixaList;
    }

    public List<PainelEntityNode> getTarefasSub(String nomeSubFluxo) {
        if (subTarefaList == null || !StringUtils.isEmpty(nomeSubFluxo)) {
            subTarefaList = new ArrayList<>();
            for (TaskDefinitionBean taskDefinitionBean : subMapa.get(nomeSubFluxo)) {
                subTarefaList.add(new PainelEntityNode(null, taskDefinitionBean, PainelEntityNode.TASK_TYPE));
            }
            subTarefaList.sort((o1, o2) -> o1.getEntity().getName().compareTo(o2.getEntity().getName()));
        }
        return subTarefaList;
    }

    private Map<String, Collection<TaskDefinitionBean>> getMapaSub() {
        if (subMapa == null) {
            subMapa = new HashMap<>();
            List<TaskDefinitionBean> ltTarefaSub = fluxoBean.getTaskDefinitions().values().stream()
                    .filter(d -> d.getTasks().stream().noneMatch(t -> t.getNomeFluxo().equals(fluxoBean.getName()))).collect(Collectors.toList());
            for (TaskDefinitionBean taskDefinitionBean : ltTarefaSub) {
                String nomeFLuxo = taskDefinitionBean.getTasks().stream().map(TaskBean::getNomeFluxo).findFirst().get();
                if (!subMapa.containsKey(nomeFLuxo)) {
                    List<TaskDefinitionBean> ltTarefa = new ArrayList<>();
                    ltTarefa.add(taskDefinitionBean);
                    subMapa.put(nomeFLuxo, ltTarefa);
                } else {
                    subMapa.get(nomeFLuxo).add(taskDefinitionBean);
                }
            }
        }
        return subMapa;
    }

    public void refresh() {
        if (rootList != null) {
            rootList.clear();
        }
    }

    @Override
    public void clearTree() {
        rootList = null;
        subCaixaList = null;
        subTarefaList = null;
        subMapa = null;
        super.clearTree();
    }

	public FluxoBean getFluxoBean() {
		return fluxoBean;
	}

	public void setFluxoBean(FluxoBean fluxoBean) {
		this.fluxoBean = fluxoBean;
	}

    public String getBackgroundColorSub() {
        String codColor = "#006600";
        if ("vermelho".equals(skinSessaoManager.getSkin())) {
            codColor = "#990000";
        } else if ("altoContraste".equals(skinSessaoManager.getSkin())) {
            codColor = "#FFFFFF";
        } else if ("branco".equals(skinSessaoManager.getSkin())) {
            codColor = "#333333";
        } else if ("branco-vermelho".equals(skinSessaoManager.getSkin())) {
            codColor = "#995555";
        } else if ("cinza".equals(skinSessaoManager.getSkin())) {
            codColor = "#000000";
        } else if ("azul".equals(skinSessaoManager.getSkin())) {
            codColor = "#02215D";
        } else if ("azul-etc".equals(skinSessaoManager.getSkin())) {
            codColor = "#002DA5";
        }
        return codColor;
    }

}
