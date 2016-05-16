package br.com.infox.epp.processo.iniciar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.joda.time.DateTime;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.com.infox.core.log.LogErrorService;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.dao.NatCatFluxoLocalizacaoDAO;
import br.com.infox.epp.log.LogErro;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.manager.MeioContatoManager;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.dao.PessoaFisicaDAO;
import br.com.infox.epp.pessoa.dao.PessoaJuridicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.tipoParte.TipoParteSearch;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.util.ComponentUtil;

@Named
@ViewScoped
public class IniciarProcessoView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = Logger.getLogger(IniciarProcessoView.class.getName());
	private static final String SCRIPT = "open('{contextPath}/Processo/movimentar.seam?idProcesso={idProcesso}&idTaskInstance={idTaskInstance}', '{idProcesso}popUpFluxo', 'fullscreen=1, resizable=yes,scrollbars=1');";

    @Inject
    private NatCatFluxoLocalizacaoDAO natCatFluxoLocalizacaoDAO;
    @Inject
    private PessoaFisicaDAO pessoaFisicaDAO;
    @Inject
    private PessoaJuridicaDAO pessoaJuridicaDAO;
    @Inject
    private MeioContatoManager meioContatoManager;
    @Inject
    private TipoParteSearch tipoParteSearch;
    @Inject
    private SituacaoProcessoDAO situacaoProcessoDAO;
    @Inject
    private ProcessoManager processoManager;
    @Inject
    private LogErrorService logErrorService;
    @Inject
    private JsfUtil jsfUtil;
    @Inject
    private IniciarProcessoService iniciarProcessoService;

    private List<Processo> processosCriados;
    private List<NaturezaCategoriaFluxoItem> naturezaCategoriaFluxoItemList;
    private List<TipoParte> tipoParteList;
    private TreeNode root = new DefaultTreeNode("Root", null);
    
    private NaturezaCategoriaFluxoItem naturezaCategoriaFluxoItem;
    private Processo processo;
    private IniciarProcessoParticipanteVO iniciarProcessoParticipanteVO;
    
    @PostConstruct
    private void init() {
        Localizacao localizacao = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getLocalizacao();
        Papel papel = Authenticator.getPapelAtual();
        UsuarioLogin usuarioLogin = Authenticator.getUsuarioLogado();
        createProcesso(localizacao, usuarioLogin);
        iniciarProcessoParticipanteVO = new IniciarProcessoParticipanteVO();
        naturezaCategoriaFluxoItemList = natCatFluxoLocalizacaoDAO.listByLocalizacaoAndPapel(localizacao, papel);
        tipoParteList = tipoParteSearch.findAll();
        processosCriados = 
    }

    private void createProcesso(Localizacao localizacao, UsuarioLogin usuarioLogin) {
        processo = new Processo();
        processo.setLocalizacao(localizacao);
        processo.setUsuarioCadastro(usuarioLogin);
        processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
        processo.setProcessoRoot(processo);
        processo.setDataInicio(DateTime.now().toDate());
    }
    
    public String iniciarProcesso(Processo processo) {
        try {
            String processDefinitionName = processo.getNaturezaCategoriaFluxo().getFluxo().getFluxo();
            ProcessDefinition processDefinition = JbpmUtil.instance().findLatestProcessDefinition(processDefinitionName);
            Task startTask = processDefinition.getTaskMgmtDefinition().getStartTask();
            if (startTask.getTaskController() != null) {
                jsfUtil.addFlashParam("processo", processo);
                return "/Processo/startTaskForm.seam";
            } else {
                ProcessInstance processInstance = iniciarProcessoService.iniciarProcesso(processo);
                openMovimentarIfAccessible(processInstance);
                return "/Painel/list.seam?faces-redirect=true";
            }
        } catch (Exception e) {
            LogErro logErro = logErrorService.log(e);
            LOG.log(Level.SEVERE, logErro.getCodigo(), e);
            FacesMessages.instance().add("Código de Erro: " + logErro.getCodigo() + " Mensagem: " + e.getMessage());
            return "";
        }
    }
    
    public String iniciarProcesso() {
        if (naturezaCategoriaFluxoItem == null) {
            FacesMessages.instance().add("Selecione um Agrupamento de Fluxo, por favor!");
        }
        try {
            processo.setNaturezaCategoriaFluxo(naturezaCategoriaFluxoItem.getNaturezaCategoriaFluxo());
            List<ParticipanteProcesso> participantes = new ArrayList<>();
            if (!root.isLeaf()) {
                for (TreeNode treeNode : root.getChildren()) {
                    IniciarProcessoParticipanteVO participanteVO = (IniciarProcessoParticipanteVO) treeNode;
                    participantes.addAll(participanteVO.getListParticipantes(processo));
                }
            }
            List<MetadadoProcesso> metadados = new ArrayList<>();
            MetadadoProcessoProvider processoProvider = new MetadadoProcessoProvider();
            if (naturezaCategoriaFluxoItem.hasItem()) {
                MetadadoProcesso item = processoProvider.gerarMetadado(EppMetadadoProvider.ITEM_DO_PROCESSO, naturezaCategoriaFluxoItem.getItem().getIdItem().toString());
                metadados.add(item);
            }
            String processDefinitionName = processo.getNaturezaCategoriaFluxo().getFluxo().getFluxo();
            ProcessDefinition processDefinition = JbpmUtil.instance().findLatestProcessDefinition(processDefinitionName);
            Task startTask = processDefinition.getTaskMgmtDefinition().getStartTask();
            if (startTask.getTaskController() != null) {
                processoManager.gravarProcesso(processo, metadados, participantes);
                jsfUtil.addFlashParam("processo", processo);
                return "/Processo/startTaskForm.seam";
            } else {
                processoManager.gravarProcesso(processo, metadados, participantes);
                ProcessInstance processInstance = iniciarProcessoService.iniciarProcesso(processo);
                openMovimentarIfAccessible(processInstance);
                return "/Painel/list.seam?faces-redirect=true";
            }
        } catch (Exception e) {
            LogErro logErro = logErrorService.log(e);
            LOG.log(Level.SEVERE, logErro.getCodigo(), e);
            FacesMessages.instance().add("Código de Erro: " + logErro.getCodigo() + " Mensagem: " + e.getMessage());
            return "";
        }
    }

    private void openMovimentarIfAccessible(ProcessInstance processInstance) {
        Collection<TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();
        if (taskInstances != null) {
            for (TaskInstance taskInstance : taskInstances) {
                boolean canOpenTask = situacaoProcessoDAO.canOpenTask(taskInstance.getId(), null, false);
                if (canOpenTask) {
                    PathResolver pathResolver = ComponentUtil.getComponent(PathResolver.NAME);
                    String script = SCRIPT.replace("{contextPath}", pathResolver.getContextPath())
                            .replace("{idTaskInstance}", String.valueOf(taskInstance.getId()))
                            .replace("{idProcesso}", processo.getIdProcesso().toString());
                    jsfUtil.execute(script);
                    break;
                }
            }
        }
    }
    
    public void onSelectNaturezaCategoriaFluxoItem() {
        if (naturezaCategoriaFluxoItem != null) {
            processo.setNaturezaCategoriaFluxo(naturezaCategoriaFluxoItem.getNaturezaCategoriaFluxo());
        }
    }
    
    public void onChangeTipoPessoa() {
        TipoPessoaEnum tipoPessoa = iniciarProcessoParticipanteVO.getTipoPessoa();
        iniciarProcessoParticipanteVO = new IniciarProcessoParticipanteVO();
        iniciarProcessoParticipanteVO.setTipoPessoa(tipoPessoa);
    }
    
    public void onChangeParticipanteCpf() {
        PessoaFisica pessoaFisica = pessoaFisicaDAO.searchByCpf(iniciarProcessoParticipanteVO.getCodigo());
        if (pessoaFisica != null) {
            iniciarProcessoParticipanteVO.loadPessoaFisica(pessoaFisica);
            MeioContato meioContato = meioContatoManager.getMeioContatoByPessoaAndTipo(pessoaFisica, TipoMeioContatoEnum.EM);
            if (meioContato != null) {
                iniciarProcessoParticipanteVO.loadMeioContato(meioContato);
            }
        } else {
            iniciarProcessoParticipanteVO.limparDadosPessoaFisica();
        }
    }
    
    public void onChangeParticipanteCnpj() {
        PessoaJuridica pessoaJuridica = pessoaJuridicaDAO.searchByCnpj(iniciarProcessoParticipanteVO.getCodigo());
        if (pessoaJuridica != null) {
            iniciarProcessoParticipanteVO.loadPessoaJuridica(pessoaJuridica);
        } else {
            iniciarProcessoParticipanteVO.limparDadosPessoaJuridica();
        }
    }

    public void adicionarParticipante() {
        iniciarProcessoParticipanteVO.generateId();
        if (!podeAdicionarParticipante(iniciarProcessoParticipanteVO)) {
            FacesMessages.instance().add("Não pode adicionar a mesma pessoa com o mesmo tipo de parte e mesmo participante superior");
        } else {
            iniciarProcessoParticipanteVO.adicionar();
            if (iniciarProcessoParticipanteVO.getParent() == null) {
                root.getChildren().add(iniciarProcessoParticipanteVO);
            }
            iniciarProcessoParticipanteVO = new IniciarProcessoParticipanteVO();
        }
    }
    
    private boolean podeAdicionarParticipante(IniciarProcessoParticipanteVO iniciarProcessoParticipanteVO) {
        TreeNode parent = iniciarProcessoParticipanteVO.getParent() == null ? root : iniciarProcessoParticipanteVO.getParent();
        return !parent.getChildren().contains(iniciarProcessoParticipanteVO);
    }

    public void removerParticipante(IniciarProcessoParticipanteVO iniciarProcessoParticipanteVO) {
        removerParticipante(iniciarProcessoParticipanteVO, null);
    }
    
    private void removerParticipante(IniciarProcessoParticipanteVO iniciarProcessoParticipanteVO, Iterator<TreeNode> iterator) {
        if (!iniciarProcessoParticipanteVO.isLeaf()) {
            Iterator<TreeNode> iteratorList = iniciarProcessoParticipanteVO.getChildren().iterator();
            while (iteratorList.hasNext()) {
                IniciarProcessoParticipanteVO child = (IniciarProcessoParticipanteVO) iteratorList.next();
                removerParticipante(child, iteratorList);
            }
        }
        if (iterator != null) {
            iterator.remove();
            iniciarProcessoParticipanteVO.clearParent();
        } else {
            iniciarProcessoParticipanteVO.getParent().getChildren().remove(iniciarProcessoParticipanteVO);
        }
    }
    
    public List<Processo> getProcessosCriados() {
        return processosCriados;
    }

    public List<NaturezaCategoriaFluxoItem> getNaturezaCategoriaFluxoItemList() {
        return naturezaCategoriaFluxoItemList;
    }
    
    public List<TipoParte> getTipoParteList() {
        return tipoParteList;
    }
    
    public TreeNode getRoot() {
        return root;
    }

    public IniciarProcessoParticipanteVO getIniciarProcessoParticipanteVO() {
        return iniciarProcessoParticipanteVO;
    }

    public void setIniciarProcessoParticipanteVO(IniciarProcessoParticipanteVO iniciarProcessoParticipanteVO) {
        this.iniciarProcessoParticipanteVO = iniciarProcessoParticipanteVO;
    }

    public NaturezaCategoriaFluxoItem getNaturezaCategoriaFluxoItem() {
        return naturezaCategoriaFluxoItem;
    }

    public void setNaturezaCategoriaFluxoItem(NaturezaCategoriaFluxoItem naturezaCategoriaFluxoItem) {
        this.naturezaCategoriaFluxoItem = naturezaCategoriaFluxoItem;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }
}
