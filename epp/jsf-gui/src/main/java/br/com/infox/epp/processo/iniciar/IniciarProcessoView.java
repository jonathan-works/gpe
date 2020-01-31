package br.com.infox.epp.processo.iniciar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.def.Task;
import org.joda.time.DateTime;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.dao.NatCatFluxoLocalizacaoDAO;
import br.com.infox.epp.loglab.contribuinte.type.TipoParticipanteEnum;
import br.com.infox.epp.loglab.search.EmpresaSearch;
import br.com.infox.epp.loglab.search.ServidorContribuinteSearch;
import br.com.infox.epp.loglab.vo.EmpresaVO;
import br.com.infox.epp.loglab.vo.PesquisaParticipanteVO;
import br.com.infox.epp.loglab.vo.ServidorContribuinteVO;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.manager.MeioContatoManager;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.municipio.EstadoSearch;
import br.com.infox.epp.pessoa.dao.PessoaFisicaDAO;
import br.com.infox.epp.pessoa.dao.PessoaJuridicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.dao.ProcessoSearch;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoSearch;
import br.com.infox.epp.tipoParte.TipoParteSearch;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.seam.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class IniciarProcessoView extends AbstractIniciarProcesso {

    private static final long serialVersionUID = 1L;

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
    private ProcessoManager processoManager;
    @Inject
    private ProcessoSearch processoSearch;
    @Inject
    private StatusProcessoSearch statusProcessoSearch;
    @Inject
    private EstadoSearch estadoSearch;
    @Inject
    private ServidorContribuinteSearch servidorContribuinteSearch;
    @Inject
    private EmpresaSearch empresaSearch;

    @Getter
    private List<Processo> processosCriados;
    @Getter
    private List<NaturezaCategoriaFluxoItem> naturezaCategoriaFluxoItemList;
    @Getter
    private List<TipoParte> tipoParteList;
    @Getter
    private TreeNode root = new DefaultTreeNode("Root", null);
    @Getter
    private List<IniciarProcessoParticipanteVO> participanteProcessoList;

    @Getter
    @Setter
    private NaturezaCategoriaFluxoItem naturezaCategoriaFluxoItem;
    @Getter
    @Setter
    private Processo processo;
    @Getter
    @Setter
    private IniciarProcessoParticipanteVO iniciarProcessoParticipanteVO;

    @Getter
    @Setter
    private EmpresaVO empresaVO;

    @Getter
    private List<EmpresaVO> empresaList;

    @Getter
    @Setter
    private ServidorContribuinteVO servidorContribuinteVO;

    @Getter
    private List<ServidorContribuinteVO> servidorContribuinteList;

    @Getter
    @Setter
    private PesquisaParticipanteVO pesquisaParticipanteVO;

    @PostConstruct
    private void init() {
        Localizacao localizacao = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getLocalizacao();
        if (localizacao == null) {
            throw new EppConfigurationException("Usuário não possui localização abaixo de estrutura");
        }
        Papel papel = Authenticator.getPapelAtual();
        UsuarioLogin usuarioLogin = Authenticator.getUsuarioLogado();
        createProcesso(Authenticator.getUsuarioPerfilAtual().getLocalizacao(), usuarioLogin);
        iniciarProcessoParticipanteVO = new IniciarProcessoParticipanteVO();
        naturezaCategoriaFluxoItemList = natCatFluxoLocalizacaoDAO.listByLocalizacaoAndPapel(localizacao, papel);
        tipoParteList = tipoParteSearch.findAll();
        processosCriados = processoSearch.getProcessosNaoIniciados(Authenticator.getUsuarioLogado());
        participanteProcessoList = new ArrayList<>();

        limparDadosParticipante();
    }

    public void limparDadosParticipante() {
        empresaVO = null;
        empresaList = null;
        limparServidorContribuinte();
    }

    public void limparServidorContribuinte() {
        servidorContribuinteVO = null;
        servidorContribuinteList = null;
        servidorContribuinteVO.setTipoParticipante(TipoParticipanteEnum.CO);
        pesquisaParticipanteVO = new PesquisaParticipanteVO();
        pesquisaParticipanteVO.setTipoParticipante(TipoParticipanteEnum.CO);
    }

    private void createProcesso(Localizacao localizacao, UsuarioLogin usuarioLogin) {
        processo = new Processo();
        processo.setLocalizacao(localizacao);
        processo.setUsuarioCadastro(usuarioLogin);
        processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
        processo.setProcessoRoot(processo);
        processo.setDataInicio(DateTime.now().toDate());
    }

    @ExceptionHandled
    public void removerProcesso(Processo processo) {
        processoManager.removerProcessoNaoIniciado(processo);
        processosCriados.remove(processo);
    }

    @ExceptionHandled
    public String iniciar(Processo processo) {
        String processDefinitionName = processo.getNaturezaCategoriaFluxo().getFluxo().getFluxo();
        ProcessDefinition processDefinition = JbpmUtil.instance().findLatestProcessDefinition(processDefinitionName);
        Task startTask = processDefinition.getTaskMgmtDefinition().getStartTask();
        if (hasStartTaskForm(startTask)) {
            jsfUtil.addFlashParam("processo", processo);
            return "/Processo/startTaskForm.seam";
        } else {
            iniciarProcesso(processo);
            return "/Painel/list.seam?faces-redirect=true";
        }
    }

    @ExceptionHandled(createLogErro = true)
    public String iniciar() {
        if (naturezaCategoriaFluxoItem == null) {
            throw new BusinessException("Selecione um Agrupamento de Fluxo, por favor!");
        }
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
        StatusProcesso statusNaoIniciado = statusProcessoSearch.getStatusByName(StatusProcesso.STATUS_NAO_INICIADO);
        MetadadoProcesso metatadoStatus = processoProvider.gerarMetadado(EppMetadadoProvider.STATUS_PROCESSO, statusNaoIniciado.getIdStatusProcesso().toString());
        metadados.add(metatadoStatus);
        String processDefinitionName = processo.getNaturezaCategoriaFluxo().getFluxo().getFluxo();
        ProcessDefinition processDefinition = JbpmUtil.instance().findLatestProcessDefinition(processDefinitionName);
        Task startTask = processDefinition.getTaskMgmtDefinition().getStartTask();
        if (hasStartTaskForm(startTask)) {
            processoManager.gravarProcessoMetadadoParticipantePasta(processo, metadados, participantes);
            jsfUtil.addFlashParam("processo", processo);
            return "/Processo/startTaskForm.seam";
        } else {
            processoManager.gravarProcessoMetadadoParticipantePasta(processo, metadados, participantes);
            iniciarProcesso(processo);
            return "/Painel/list.seam?faces-redirect=true";
        }
    }

    private boolean hasStartTaskForm(Task startTask) {
        return startTask.getTaskController() != null
                && startTask.getTaskController().getVariableAccesses() != null
                && !startTask.getTaskController().getVariableAccesses().isEmpty()
                && !containsOnlyParameterVariable(startTask);
    }

    private boolean containsOnlyParameterVariable(Task startTask) {
        List<VariableAccess> variableAccesses = startTask.getTaskController().getVariableAccesses();
        for (VariableAccess variableAccess : variableAccesses) {
            if (!variableAccess.getMappedName().startsWith(VariableType.PARAMETER.name())) {
                return false;
            }
        }
        return true;
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
        limparDadosParticipante();
    }

    public void buscarServidorContribuinte() {
        servidorContribuinteList = servidorContribuinteSearch.pesquisaServidorContribuinte(pesquisaParticipanteVO);

        if(servidorContribuinteList != null && servidorContribuinteList.size() > 0) {
            if(servidorContribuinteList.size() == 1) {
                servidorContribuinteVO = servidorContribuinteList.get(0);
                servidorContribuinteList = null;
            } else {
                JsfUtil.instance().execute("PF('servidorContribuinteDialog').show();");
            }
        } else {
            servidorContribuinteVO = new ServidorContribuinteVO();
            servidorContribuinteVO.setTipoParticipante(pesquisaParticipanteVO.getTipoParticipante());
        }

    }

    public void selecionarServidorContribuinte(ServidorContribuinteVO row) {
        servidorContribuinteVO = row;
        servidorContribuinteList = null;
        JsfUtil.instance().execute("PF('servidorContribuinteDialog').hide();");
    }

    public void buscarEmpresa() {
        empresaList = empresaSearch.pesquisaEmpresaVO(pesquisaParticipanteVO);

        if(empresaList != null && empresaList.size() > 0) {
            if(empresaList.size() == 1) {
                empresaVO = empresaList.get(0);
                empresaList = null;
            } else {
                JsfUtil.instance().execute("PF('empresaDialog').show();");
            }
        } else {
            empresaVO = new EmpresaVO();
        }
    }

    public void selecionarEmpresa(EmpresaVO row) {
        empresaVO = row;
        empresaList = null;
        JsfUtil.instance().execute("PF('empresaDialog').hide();");
    }

    public void onChangeParticipanteCpf() {
        PessoaFisica pessoaFisica = pessoaFisicaDAO.searchByCpf(iniciarProcessoParticipanteVO.getCodigo());
        if (pessoaFisica != null) {
            iniciarProcessoParticipanteVO.loadPessoaFisica(pessoaFisica);
            MeioContato meioContato = meioContatoManager.getMeioContatoByPessoaAndTipo(pessoaFisica, TipoMeioContatoEnum.EM);
            iniciarProcessoParticipanteVO.loadMeioContato(meioContato);
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

    public void onClickNovoParticipante() {
        iniciarProcessoParticipanteVO = new IniciarProcessoParticipanteVO();
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
            participanteProcessoList.add(iniciarProcessoParticipanteVO);
            Collections.sort(participanteProcessoList);
            iniciarProcessoParticipanteVO = new IniciarProcessoParticipanteVO();
        }
    }

    private boolean podeAdicionarParticipante(IniciarProcessoParticipanteVO iniciarProcessoParticipanteVO) {
        TreeNode parent = iniciarProcessoParticipanteVO.getParent() == null ? root : iniciarProcessoParticipanteVO.getParent();
        return !parent.getChildren().contains(iniciarProcessoParticipanteVO);
    }

    public void removerParticipante(IniciarProcessoParticipanteVO iniciarProcessoParticipanteVO) {
        removerParticipante(iniciarProcessoParticipanteVO, null);
        Collections.sort(participanteProcessoList);
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
        participanteProcessoList.remove(iniciarProcessoParticipanteVO);
    }

    public List<String> getListCodEstado() {
        return estadoSearch.getListCodEstado();
    }
}
