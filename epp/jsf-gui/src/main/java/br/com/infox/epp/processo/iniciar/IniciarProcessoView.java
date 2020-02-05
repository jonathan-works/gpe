package br.com.infox.epp.processo.iniciar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import br.com.infox.core.util.StringUtil;
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
import br.com.infox.epp.loglab.service.ParticipanteProcessoLoglabService;
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
    @Inject
    private ParticipanteProcessoLoglabService participanteProcessoLoglabService;

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

    private Map<String, ServidorContribuinteVO> mapServidorContribuinteVO;
    private Map<String, EmpresaVO> mapEmpresaVO;

    @PostConstruct
    private void init() {
        Localizacao localizacao = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getLocalizacao();
        if (localizacao == null) {
            throw new EppConfigurationException("Usuário não possui localização abaixo de estrutura");
        }
        Papel papel = Authenticator.getPapelAtual();
        UsuarioLogin usuarioLogin = Authenticator.getUsuarioLogado();
        createProcesso(Authenticator.getUsuarioPerfilAtual().getLocalizacao(), usuarioLogin);
        naturezaCategoriaFluxoItemList = natCatFluxoLocalizacaoDAO.listByLocalizacaoAndPapel(localizacao, papel);
        tipoParteList = tipoParteSearch.findAll();
        processosCriados = processoSearch.getProcessosNaoIniciados(Authenticator.getUsuarioLogado());
        participanteProcessoList = new ArrayList<>();
        mapServidorContribuinteVO = new HashMap<>();
        mapEmpresaVO = new HashMap<>();

        limparDadosParticipante();
    }

    public void limparDadosParticipante() {
        TipoPessoaEnum tipoPessoa = iniciarProcessoParticipanteVO != null ? iniciarProcessoParticipanteVO.getTipoPessoa() : TipoPessoaEnum.F;
        iniciarProcessoParticipanteVO = new IniciarProcessoParticipanteVO();
        iniciarProcessoParticipanteVO.setTipoPessoa(tipoPessoa);
        empresaVO = null;
        empresaList = null;
        pesquisaParticipanteVO = new PesquisaParticipanteVO();
        pesquisaParticipanteVO.setTipoParticipante(TipoParticipanteEnum.CO);
        limparServidorContribuinte();
    }

    public void limparServidorContribuinte() {
        servidorContribuinteVO = null;
        servidorContribuinteList = null;
        limparCamposPesquisa();
    }

    public void limparCamposPesquisa() {
        pesquisaParticipanteVO.setCpf(null);
        pesquisaParticipanteVO.setMatricula(null);
        pesquisaParticipanteVO.setNomeCompleto(null);
        pesquisaParticipanteVO.setNomeFantasia(null);
        pesquisaParticipanteVO.setRazaoSocial(null);
        pesquisaParticipanteVO.setCnpj(null);
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
        participanteProcessoLoglabService.persistenciaIniciarProcessoView(processo, metadados,
                participantes, new ArrayList<>(mapServidorContribuinteVO.values()),
                new ArrayList<>(mapEmpresaVO.values()));
        if (hasStartTaskForm(startTask)) {
            jsfUtil.addFlashParam("processo", processo);
            return "/Processo/startTaskForm.seam";
        } else {
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
        limparDadosParticipante();
    }

    public void buscarServidorContribuinte() {
        if (StringUtil.isEmpty(pesquisaParticipanteVO.getCpf())
                && StringUtil.isEmpty(pesquisaParticipanteVO.getNomeCompleto())
                && StringUtil.isEmpty(pesquisaParticipanteVO.getMatricula())) {
            FacesMessages.instance().add("Por favor, preencha pelo menos um campo de busca.");
        } else {
            servidorContribuinteList = servidorContribuinteSearch.pesquisaServidorContribuinte(pesquisaParticipanteVO);

            if(servidorContribuinteList != null && servidorContribuinteList.size() > 0) {
                if(servidorContribuinteList.size() == 1) {
                    servidorContribuinteVO = servidorContribuinteList.get(0);
                    onChangeParticipanteCpf();
                    servidorContribuinteList = null;
                } else {
                    JsfUtil.instance().execute("PF('servidorContribuinteDialog').show();");
                }
            } else if(pesquisaParticipanteVO.getTipoParticipante().equals(TipoParticipanteEnum.CO)) {
                servidorContribuinteVO = new ServidorContribuinteVO();
                servidorContribuinteVO.setTipoParticipante(pesquisaParticipanteVO.getTipoParticipante());
                servidorContribuinteVO.setCpf(pesquisaParticipanteVO.getCpf());
                FacesMessages.instance().add("Contribuinte não encontrado. Preencha os dados para adicionar um novo.");
            } else {
                FacesMessages.instance().add("Nenhum registro foi encontrado com os dados da busca.");
            }
        }
    }

    public void selecionarServidorContribuinte(ServidorContribuinteVO row) {
        servidorContribuinteVO = row;
        onChangeParticipanteCpf();
        servidorContribuinteList = null;
        JsfUtil.instance().execute("PF('servidorContribuinteDialog').hide();");
    }

    public void buscarEmpresa() {
        if (StringUtil.isEmpty(pesquisaParticipanteVO.getCnpj())
                && StringUtil.isEmpty(pesquisaParticipanteVO.getNomeFantasia())
                && StringUtil.isEmpty(pesquisaParticipanteVO.getRazaoSocial())) {
            FacesMessages.instance().add("Por favor, preencha pelo menos um campo de busca.");
        } else {
            empresaList = empresaSearch.pesquisaEmpresaVO(pesquisaParticipanteVO);

            if(empresaList != null && empresaList.size() > 0) {
                if(empresaList.size() == 1) {
                    empresaVO = empresaList.get(0);
                    onChangeParticipanteCnpj();
                    empresaList = null;
                } else {
                    JsfUtil.instance().execute("PF('empresaDialog').show();");
                }
            } else {
                empresaVO = new EmpresaVO();
                empresaVO.setCnpj(pesquisaParticipanteVO.getCnpj());
                FacesMessages.instance().add("Registro não encontrado. Preencha os dados para adicionar um novo.");
            }
        }
    }

    public void selecionarEmpresa(EmpresaVO row) {
        empresaVO = row;
        onChangeParticipanteCnpj();
        empresaList = null;
        JsfUtil.instance().execute("PF('empresaDialog').hide();");
    }

    public void onChangeParticipanteCpf() {
        iniciarProcessoParticipanteVO.setCodigo(servidorContribuinteVO.getCpf());
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
        iniciarProcessoParticipanteVO.setCodigo(empresaVO.getCnpj());
        PessoaJuridica pessoaJuridica = pessoaJuridicaDAO.searchByCnpj(iniciarProcessoParticipanteVO.getCodigo());
        if (pessoaJuridica != null) {
            iniciarProcessoParticipanteVO.loadPessoaJuridica(pessoaJuridica);
        } else {
            iniciarProcessoParticipanteVO.limparDadosPessoaJuridica();
        }
    }

    public void onClickNovoParticipante() {
        limparDadosParticipante();
    }

    public void adicionarParticipante() {
        if(servidorContribuinteVO == null && empresaVO == null) {
            FacesMessages.instance().add("É necessário inserir os dados de uma pessoa física ou jurídica");
        } else {
            iniciarProcessoParticipanteVO.generateId();
            if (!podeAdicionarParticipante(iniciarProcessoParticipanteVO)) {
                FacesMessages.instance().add("Não pode adicionar a mesma pessoa com o mesmo tipo de parte e mesmo participante superior");
            } else {
                if(iniciarProcessoParticipanteVO.getTipoPessoa().equals(TipoPessoaEnum.F)) {
                    iniciarProcessoParticipanteVO.setCodigo(servidorContribuinteVO.getCpf());
                    iniciarProcessoParticipanteVO.setEmail(servidorContribuinteVO.getEmail());
                    iniciarProcessoParticipanteVO.setDataNascimento(servidorContribuinteVO.getDataNascimento());
                    iniciarProcessoParticipanteVO.setNome(servidorContribuinteVO.getNomeCompleto());
                    mapServidorContribuinteVO.put(iniciarProcessoParticipanteVO.getId(), servidorContribuinteVO);
                } else {
                    iniciarProcessoParticipanteVO.setCodigo(empresaVO.getCnpj());
                    iniciarProcessoParticipanteVO.setRazaoSocial(empresaVO.getRazaoSocial());
                    iniciarProcessoParticipanteVO.setNome(empresaVO.getNomeFantasia());
                    mapEmpresaVO.put(iniciarProcessoParticipanteVO.getId(), empresaVO);
                }
                iniciarProcessoParticipanteVO.adicionar();
                if (iniciarProcessoParticipanteVO.getParent() == null) {
                    root.getChildren().add(iniciarProcessoParticipanteVO);
                }
                participanteProcessoList.add(iniciarProcessoParticipanteVO);
                Collections.sort(participanteProcessoList);


                limparDadosParticipante();
            }
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
        if(mapServidorContribuinteVO.containsKey(iniciarProcessoParticipanteVO.getId())) {
            mapServidorContribuinteVO.remove(iniciarProcessoParticipanteVO.getId());
        }
        if(mapEmpresaVO.containsKey(iniciarProcessoParticipanteVO.getId())) {
            mapEmpresaVO.remove(iniciarProcessoParticipanteVO.getId());
        }
    }

    public List<String> getListCodEstado() {
        return estadoSearch.getListCodEstado();
    }


}
