package br.com.infox.epp.tarefaexterna.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.municipio.EstadoSearch;
import br.com.infox.epp.pessoa.type.TipoGeneroEnum;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.service.DocumentoUploaderService;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.tarefaexterna.CadastroTarefaExternaDocumentoDTO;
import br.com.infox.epp.tarefaexterna.DocumentoUploadTarefaExternaService;
import br.com.infox.ibpm.sinal.SignalParam;
import br.com.infox.ibpm.sinal.SignalParam.Type;
import br.com.infox.ibpm.sinal.SignalService;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaSearch;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.exception.BusinessRollbackException;
import lombok.Getter;

@Named
@ViewScoped
public class CadastroTarefaExternaView implements FileUploadListener, Serializable {
    private static final long serialVersionUID = 1L;

    public static final String PARAM_TAREFA_EXTERNA = "tarefaExterna";
    public static final String PARAM_UUID_TAREFA_EXTERNA = "uuidTarefaExterna";

    public static final String DOMINIO_TAREFA_EXTERNA_TIPOS_MANIFESTO = "tarefaExternaTiposManifesto";
    private static final String DOMINIO_TAREFA_EXTERNA_MEIOS_RESPOSTA = "tarefaExternaMeiosResposta";
    private static final String DOMINIO_TAREFA_EXTERNA_GRUPO_OUVIDORIAS = "tarefaExternaGrupoOuvidorias";

    @Getter
    private CadastroTarefaExternaVO vo;

    @Inject
    private TarefaExternaSearch tarefaExternaSearch;
    @Inject
    private EstadoSearch estadoSearch;
    @Inject
    private DominioVariavelTarefaSearch dominioVariavelTarefaSearch;
    @Inject
    private ConfiguracaoTarefaExternaViewSearch configuracaoTarefaExternaViewSearch;

    @Inject
    @GenericDao
    private Dao<ClassificacaoDocumento, Integer> classificacaoDocumentoDao;

    @Getter
    private List<SelectItem> estados;
    @Getter
    private TipoGeneroEnum[] tiposGenero;
    @Getter
    private List<SelectItem> grupoOuvidorias;
    @Getter
    private List<SelectItem> meiosResposta;
    private List<SelectItem> meiosRespostaBase;
    @Getter
    private List<SelectItem> tiposManifesto;

    private UUID uuidTarefaExterna;


    @Getter
    private CadastroTarefaExternaDocumentoDTO docVO;
    @Inject
    @Getter
    private CadastroTarefaDocumentoDataModel documentosDataModel;
    @Getter
    private List<ClassificacaoDocumentoVO> classificacoes;
    @Getter
    private List<PastaVO> pastas;
    @Getter
    private String acceptedTypes;
    @Inject
    private DocumentoUploadTarefaExternaService documentoUploadTarefaExternaService;
    @Inject
    private DocumentoUploaderService documentoUploaderService;
    @Inject
    private SignalService signalService;

    @PostConstruct
    private void init() {
        this.estados = estadoSearch.findAll()
            .stream()
            .map(o -> new SelectItem(o.getCodigo(), o.getNome()))
            .collect(Collectors.toList());
        this.tiposGenero = TipoGeneroEnum.values();

        classificacoes = configuracaoTarefaExternaViewSearch.getClassificacoes();
        pastas = configuracaoTarefaExternaViewSearch.getPastas();
        initListas();
        reset();
    }

    private void reset() {
        uuidTarefaExterna = UUID.randomUUID();
        documentosDataModel.setUuidTarefaExterna(uuidTarefaExterna);
        this.vo = new CadastroTarefaExternaVO();
        this.docVO = new CadastroTarefaExternaDocumentoDTO(uuidTarefaExterna);
        onChangeTipoManifestacao();
    }

    @PreDestroy
    private void preDestroy() {
        documentoUploadTarefaExternaService.removerCascade(this.uuidTarefaExterna);
    }

    private void initListas() {
        this.grupoOuvidorias = getListaDados(DOMINIO_TAREFA_EXTERNA_GRUPO_OUVIDORIAS);
        this.meiosRespostaBase = getListaDados(DOMINIO_TAREFA_EXTERNA_MEIOS_RESPOSTA);
        this.tiposManifesto = getListaDados(DOMINIO_TAREFA_EXTERNA_TIPOS_MANIFESTO);
    }

    public void onChangeClassificacaoDocumento() {
        if(getDocVO().getIdClassificacaoDocumento() != null) {
            ClassificacaoDocumento classificacaoDocumento = classificacaoDocumentoDao.findById(getDocVO().getIdClassificacaoDocumento());
            acceptedTypes = classificacaoDocumento.getAcceptedTypes();
        } else {
            acceptedTypes = null;
        }
    }

    public void onChangeDesejaResposta() {
        if(!getVo().getDesejaResposta()) {
            getVo().setCodMeioResposta(null);
            getVo().setEmail(null);
        }
    }

    public void onChangeTipoManifestacao() {
        if("A".equals(getVo().getCodTipoManifestacao())) {
            this.meiosResposta = this.meiosRespostaBase.stream()
                .filter(o -> Arrays.asList("EM","PE").contains(o.getValue()))
                .collect(Collectors.toList());
            getVo().setCodMeioResposta(null);
        } else {
            this.meiosResposta = this.meiosRespostaBase;
        }
    }

    private List<SelectItem> getListaDados(String codigo) {
        DominioVariavelTarefa dominioVarTarefa = dominioVariavelTarefaSearch
                .findByCodigo(codigo);
        if(dominioVarTarefa == null) {
            throw new EppConfigurationException(String.format("Domínio de dados não configurado: %s", codigo));
        } else if(dominioVarTarefa.isDominioSqlQuery()) {
            throw new EppConfigurationException(String.format("Domínio de dados não configurado corretamente: %s", codigo));
        }

        String[] itens = dominioVarTarefa.getDominio().split(";");
        List<SelectItem> resultado = new ArrayList<>();
        for (String item : itens) {
            String[] pair = item.split("=");
            resultado.add(new SelectItem(pair[0], pair[1]));
        }

        return resultado;
    }

    @Override
    @ExceptionHandled
    public void processFileUpload(FileUploadEvent event) {
        getDocVO().setUploadValido(false);
        try {
            UploadedFile file = event.getUploadedFile();
            ClassificacaoDocumento classificacao = classificacaoDocumentoDao.findById(getDocVO().getIdClassificacaoDocumento());
            documentoUploaderService.validaDocumento(file, classificacao, file.getData());
            getDocVO().getBins().add(documentoUploaderService.createProcessoDocumentoBin(file));
            getDocVO().setUploadValido(true);
        } catch (Exception e) {
            throw new BusinessRollbackException("Falha no upload", e);
        }
    }

    @ExceptionHandled
    public void limparArquivos() {
        String arquivosParaLimpar = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("arquivosParaLimpar");
        List<String> arquivos = arquivosParaLimpar == null ? Collections.emptyList() : Arrays.asList(arquivosParaLimpar.split(";"));
        if(!arquivos.isEmpty()) {
            List<DocumentoBin> arquivosparaRemover = new ArrayList<>();
            for (DocumentoBin documentoBin : getDocVO().getBins()) {
                if(arquivos.contains(documentoBin.getNomeArquivo())){
                    arquivosparaRemover.add(documentoBin);
                }
            }
            getDocVO().getBins().removeAll(arquivosparaRemover);
            if(getDocVO().getBins().isEmpty()) {
                getDocVO().setUploadValido(false);
            }
        }
    }

    @ExceptionHandled(successMessage = "Operação efetuada com sucesso")
    public void removerDocumentos() {
        List<DocumentoVO> selecionados = getDocumentosDataModel().getSelecionados();

        if(selecionados == null || selecionados.isEmpty()) {
            return;
        }
        documentoUploadTarefaExternaService.removerCascade(
                selecionados.stream().map(DocumentoVO::getId).collect(Collectors.toList())
        );
        getDocumentosDataModel().getSelecionados().clear();
    }

    @ExceptionHandled(value = MethodType.PERSIST)
    public void inserirDocumento() {
        getDocVO().setDataInclusao(Calendar.getInstance().getTime());

        documentoUploadTarefaExternaService.inserir(getDocVO());

        this.docVO = new CadastroTarefaExternaDocumentoDTO(uuidTarefaExterna);
        onChangeClassificacaoDocumento();
        RequestContext.getCurrentInstance().addCallbackParam("sucesso", true);
    }

    private void validarCadastro() {
        if(getVo().getDesejaResposta() && !StringUtil.isEmpty(getVo().getCodMeioResposta())){
            switch (getVo().getCodMeioResposta()) {
            case "EM":
                if(
                    ("A".equals(getVo().getCodTipoManifestacao()) && StringUtil.isEmpty(getVo().getEmail())) ||
                    ("I".equals(getVo().getCodTipoManifestacao()) && StringUtil.isEmpty(getVo().getDadosPessoais().getEmail()))
                ){
                    throw new BusinessRollbackException("Atenção: O e-mail deve ser informado, conforme escolha do meio de resposta.");
                }
                break;
            case "TL":
                if("I".equals(getVo().getCodTipoManifestacao()) && (
                        StringUtil.isEmpty(getVo().getDadosPessoais().getTelefoneCelular()) ||
                        StringUtil.isEmpty(getVo().getDadosPessoais().getTelefoneFixo())
                    )
                ){
                    throw new BusinessRollbackException("Atenção: O telefone deve ser informado, conforme escolha do meio de resposta.");
                }
                break;
            default:
                break;
            }
        }
    }

    @ExceptionHandled(value = MethodType.PERSIST)
    public void cadastrar() {
        try {
            validarCadastro();

            String sinalTarefaExterna = Parametros.SINAL_TAREFA_EXTERNA.getValue();
            if(StringUtil.isEmpty(sinalTarefaExterna)) {
                throw new BusinessRollbackException(String.format("Parametro não encontrado: %s", Parametros.SINAL_TAREFA_EXTERNA.getLabel()));
            }

            List<SignalParam> params = new ArrayList<>();

            boolean anonimo = "A".equals(getVo().getCodTipoManifestacao());
            getVo().setTipoManifestacao(
                anonimo ?  "Anônimo" : "Identificado"
            );
            if(!anonimo) {
                getVo().getDadosPessoais().setSexo(
                    TipoGeneroEnum.M.equals(TipoGeneroEnum.valueOf(getVo().getDadosPessoais().getCodSexo())) ?  TipoGeneroEnum.M.getLabel() : TipoGeneroEnum.F.getLabel()
                );
            }
            getVo().setTipoManifesto(getTiposManifesto().stream()
                .filter(tm -> getVo().getCodTipoManifesto().equals(tm.getValue()))
                .map(SelectItem::getLabel)
                .findFirst()
                .get()
            );
            if(getVo().getDesejaResposta()) {
                getVo().setMeioResposta(getMeiosResposta().stream()
                    .filter(tm -> getVo().getCodMeioResposta().equals(tm.getValue()))
                    .map(SelectItem::getLabel)
                    .findFirst()
                    .get()
                );
            } else {
                getVo().setMeioResposta(null);
            }
            getVo().setGrupoOuvidoria(getGrupoOuvidorias().stream()
                .filter(tm -> getVo().getCodGrupoOuvidoria().equals(tm.getValue()))
                .map(SelectItem::getLabel)
                .findFirst()
                .get()
            );

            params.add(new SignalParam(PARAM_TAREFA_EXTERNA, getVo(), Type.VARIABLE));
            params.add(new SignalParam(PARAM_UUID_TAREFA_EXTERNA, this.uuidTarefaExterna.toString(), Type.VARIABLE));

            JsfUtil jsfUtil = JsfUtil.instance();
            jsfUtil.addFlashParam(PARAM_UUID_TAREFA_EXTERNA, this.uuidTarefaExterna.toString());
            jsfUtil.applyLastPhaseFlashAction();

            try {
                signalService.startStartStateListening(sinalTarefaExterna, params);
            }catch (Exception e) {
                throw new BusinessRollbackException(InfoxMessages.getInstance().get("configuracao.erroGenerico"), e);
            }

            if(tarefaExternaSearch.getProcessoJbpmByUUID(this.uuidTarefaExterna) == null){
                throw new BusinessRollbackException(
                    InfoxMessages.getInstance().get("configuracao.erroGenerico")
                );
            };
        } catch (Exception e) {
            RequestContext.getCurrentInstance().addCallbackParam("erro", true);
            if(e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessRollbackException("Erro inesperado", e);
        }
    }

}