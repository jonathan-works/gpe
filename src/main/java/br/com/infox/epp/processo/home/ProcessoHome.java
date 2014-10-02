package br.com.infox.epp.processo.home;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException.Motivo;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.localizacao.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.sigilo.service.SigiloProcessoService;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.seam.context.ContextFacade;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.util.ComponentUtil;
import br.com.itx.component.AbstractHome;

/**
 * Deprecated : A superclasse AbstractHome está em processo de remoção, assim as
 * funções de ProcessoHome estão sendo repassadas a novos componentes
 * */
@Deprecated
@Name(ProcessoHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoHome extends AbstractHome<Processo> {
    private static final LogProvider LOG = Logging
            .getLogProvider(Processo.class);
    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoHome";

    private static final int ERRO_AO_VERIFICAR_CERTIFICADO = 0;

    @In
    private ProcessoLocalizacaoIbpmDAO processoLocalizacaoIbpmDAO;
    @In
    private ProcessoManager processoManager;
    @In
    private ProcessoEpaManager processoEpaManager;
    @In
    private AssinaturaDocumentoService assinaturaDocumentoService;
    @In
    private DocumentoManager processoDocumentoManager;
    @In
    private SigiloProcessoService sigiloProcessoService;

    private ModeloDocumento modeloDocumento;
    private TipoProcessoDocumento tipoProcessoDocumento;
    private TipoProcessoDocumento tipoProcessoDocumentoRO;
    private ProcessoDocumentoBin processoDocumentoBin = new ProcessoDocumentoBin();
    private String modeloDocumentoRO;
    private String observacaoMovimentacao;
    private boolean iniciaExterno;
    private String signature;
    private String certChain;
    private Documento pdFluxo;
    private Integer idProcessoDocumento;
    private boolean checkVisibilidade = true;
    private boolean possuiPermissaoVisibilidade = false;

    private Long tarefaId;

    // TODO confirmar se é método realmente não é mais utilizado e removê-lo
    public void limpar() {
        modeloDocumento = null;
        tipoProcessoDocumento = null;
        newInstance();
    }

    public void iniciarTarefaProcesso() {
        try {
            processoManager.iniciarTask(instance, tarefaId,
                    Authenticator.getUsuarioPerfilAtual());
        } catch (java.lang.NullPointerException e) {
            LOG.error("ProcessoHome.iniciarTarefaProcesso()", e);
        } catch (DAOException e) {
            LOG.error("Erro ao vincular Usuario", e);
        }
    }

    public void visualizarTarefaProcesso() {
        processoManager.visualizarTask(instance, tarefaId,
                Authenticator.getUsuarioPerfilAtual());
    }

    public static ProcessoHome instance() {
        return ComponentUtil.getComponent(NAME);
    }

    private void limparAssinatura() {
        certChain = null;
        signature = null;
    }

    public Boolean checarVisibilidade() {
        if (!sigiloProcessoService.usuarioPossuiPermissao(
                Authenticator.getUsuarioLogado(),
                processoEpaManager.find(getInstance().getIdProcesso()))) {
            possuiPermissaoVisibilidade = false;
        } else if (checkVisibilidade) {
            possuiPermissaoVisibilidade = processoLocalizacaoIbpmDAO
                    .possuiPermissao(getInstance());
            checkVisibilidade = false;
        }
        if (!possuiPermissaoVisibilidade) {
            avisarNaoHaPermissaoParaAcessarProcesso();
        }
        return possuiPermissaoVisibilidade;
    }

    private void avisarNaoHaPermissaoParaAcessarProcesso() {
        ContextFacade.setToEventContext("canClosePanel", true);
        FacesMessages.instance().clear();
        throw new ApplicationException(
                "Sem permissão para acessar o processo: "
                        + getInstance().getNumeroProcesso());
    }

    public Integer salvarProcessoDocumentoFluxo(Object value, Integer idDoc,
            Boolean assinado, String label) throws CertificadoException {
        Documento processoDocumento = buscarProcessoDocumento(idDoc);
        setIdProcessoDocumento(idDoc);
        Integer result = idDoc;
        FacesMessages messages = FacesMessages.instance();
        try {
            if (tipoProcessoDocumento != null) {
                String msgKey = "Registro gravado com sucesso!";
                if (processoDocumento != null) {
                    if (assinaturaDocumentoService
                            .isDocumentoAssinado(processoDocumento,
                                    Authenticator.getUsuarioLogado())) {
                        return result;
                    }
                    atualizarProcessoDocumentoFluxo(value, idDoc, assinado);
                    msgKey = "ProcessoDocumento_updated";
                } else {
                    result = inserirProcessoDocumentoFluxo(value, label,
                            assinado);
                    msgKey = "ProcessoDocumento_created";
                }
                messages.clear();
                messages.add(StatusMessage.Severity.INFO, Messages.instance().get(msgKey));
            }
        } catch (DAOException | AssinaturaException e) {
            LOG.error("Não foi possível salvar o ProcessoDocumento " + idDoc, e);
            messages.clear();
            messages.add(e.getMessage());
            result = null;
        }
        return result;
    }

    private Documento buscarProcessoDocumento(Integer idDoc) {
        return processoDocumentoManager.find(idDoc);
    }

    // Método para Atualizar o documento do fluxo
    private void atualizarProcessoDocumentoFluxo(Object value, Integer idDoc,
            Boolean assinado) throws CertificadoException, AssinaturaException,
            DAOException {
        if (validacaoCertificadoBemSucedida(assinado)) {
            Documento processoDocumento = buscarProcessoDocumento(idDoc);
            ProcessoDocumentoBin processoDocumentoBin = processoDocumento
                    .getProcessoDocumentoBin();
            String modeloDocumento = getDescricaoModeloDocumentoFluxoByValue(
                    value, processoDocumentoBin.getModeloDocumento());
            UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
            processoDocumento.setPapel(usuarioPerfil.getPerfilTemplate()
                    .getPapel());
            processoDocumento.setLocalizacao(usuarioPerfil.getPerfilTemplate()
                    .getLocalizacao());
            processoDocumento.getProcessoDocumentoBin().setModeloDocumento(
                    modeloDocumento);
            if (assinado) {
                assinaturaDocumentoService.assinarDocumento(processoDocumento,
                        usuarioPerfil, certChain, signature);
            }
            gravarAlteracoes(processoDocumento, processoDocumentoBin);
        }
    }

    private String getDescricaoModeloDocumentoFluxoByValue(Object value,
            String modeloDocumentoFluxo) {
        if (value == null) {
            value = modeloDocumentoFluxo != null ? modeloDocumentoFluxo : "";
        }
        return value.toString();
    }

    private void gravarAlteracoes(Documento processoDocumento,
            ProcessoDocumentoBin processoDocumentoBin) {
        processoDocumento.setTipoProcessoDocumento(tipoProcessoDocumento);
        getEntityManager().merge(processoDocumento);
        setIdProcessoDocumento(processoDocumento.getIdProcessoDocumento());
        getEntityManager().merge(processoDocumentoBin);
        getEntityManager().flush();
    }

    // Método para Inserir o documento do fluxo
    private Integer inserirProcessoDocumentoFluxo(Object value, String label,
            Boolean assinado) throws CertificadoException, AssinaturaException {
        if (validacaoCertificadoBemSucedida(assinado)) {
            try {
                Object newValue = processoManager.getAlteracaoModeloDocumento(
                        processoDocumentoBin, value);
                ProcessoDocumentoBin processoDocumentoBin = processoManager
                        .createProcessoDocumentoBin(newValue);
                label = label == null ? "-" : label;
                Documento doc;
                doc = processoDocumentoManager.createProcessoDocumento(
                        getInstance(), label, processoDocumentoBin,
                        getTipoProcessoDocumento());
                final int idProcessoDocumento = doc.getIdProcessoDocumento();
                setIdProcessoDocumento(idProcessoDocumento);
                if (assinado && certChain != null && signature != null) {
                    assinaturaDocumentoService.assinarDocumento(doc,
                            Authenticator.getUsuarioPerfilAtual(), certChain,
                            signature);
                }
                return idProcessoDocumento;
            } catch (DAOException e) {
                LOG.error("inserirProcessoDocumentoFluxo", e);
                return ERRO_AO_VERIFICAR_CERTIFICADO;
            }
        } else {
            return ERRO_AO_VERIFICAR_CERTIFICADO;
        }
    }

    private boolean validacaoCertificadoBemSucedida(boolean assinado)
            throws CertificadoException, AssinaturaException {
        if (assinado) {
            try {
                assinaturaDocumentoService.verificaCertificadoUsuarioLogado(
                        certChain, Authenticator.getUsuarioLogado());
            } catch (AssinaturaException e) {
                if (e.getMotivo() == Motivo.CERTIFICADO_USUARIO_DIFERENTE_CADASTRO) {
                    limparAssinatura();
                }
                LOG.error(
                        "Não foi possível verificar o certificado do usuário "
                                + Authenticator.getUsuarioLogado(), e);
                throw e;
            }
        }
        return true;
    }

    public void carregarDadosFluxo(Integer idProcessoDocumento) {
        Documento processoDocumento = buscarProcessoDocumento(idProcessoDocumento);
        if (processoDocumento != null) {
            setPdFluxo(processoDocumento);
            processoDocumentoBin = processoDocumento.getProcessoDocumentoBin();
            setIdProcessoDocumento(processoDocumento.getIdProcessoDocumento());
            setTipoProcessoDocumento(processoDocumento
                    .getTipoProcessoDocumento());
        }
    }

    public void updateProcessoDocumentoBin() {
        String modelo = "";
        if (taskInstancePossuiModeloDocumento()) {
            ModeloDocumentoManager modeloDocumentoManager = ComponentUtil
                    .getComponent(ModeloDocumentoManager.NAME);
            modelo = modeloDocumentoManager
                    .evaluateModeloDocumento(modeloDocumento);
        }
        processoDocumentoBin.setModeloDocumento(modelo);
    }

    private boolean taskInstancePossuiModeloDocumento() {
        return TaskInstanceHome.instance().getModeloDocumento() != null;
    }

    @Override
    protected Processo createInstance() {
        Processo processo = super.createInstance();
        processo.setUsuarioCadastroProcesso(Authenticator.getUsuarioLogado());
        return processo;
    }

    @Override
    public String remove() {
        Authenticator.getUsuarioLogado()
                .getProcessoListForIdUsuarioCadastroProcesso().remove(instance);
        return super.remove();
    }

    @Override
    public String remove(Processo obj) {
        setInstance(obj);
        String ret = super.remove();
        newInstance();
        return ret;
    }

    public List<Documento> getProcessoDocumentoList() {
        return getInstance() == null ? null : getInstance()
                .getProcessoDocumentoList();
    }

    public boolean hasPartes() {
        return processoManager.hasPartes(getInstance());
    }

    // -----------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------- Getters e Setters
    // --------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------------

    public void setProcessoIdProcesso(Integer id) {
        setId(id);
    }

    public Integer getProcessoIdProcesso() {
        return (Integer) getId();
    }

    @Observer("processoHomeSetId")
    @Override
    public void setId(Object id) {
        super.setId(id);
    }

    public TipoProcessoDocumento getTipoProcessoDocumento() {
        return tipoProcessoDocumento;
    }

    public void setTipoProcessoDocumento(
            TipoProcessoDocumento tipoProcessoDocumento) {
        this.tipoProcessoDocumento = tipoProcessoDocumento;
    }

    public String getObservacaoMovimentacao() {
        return observacaoMovimentacao;
    }

    public void setObservacaoMovimentacao(String observacaoMovimentacao) {
        this.observacaoMovimentacao = observacaoMovimentacao;
    }

    public void setModeloDocumentoRO(String modeloDocumentoRO) {
        this.modeloDocumentoRO = modeloDocumentoRO;
    }

    public String getModeloDocumentoRO() {
        return modeloDocumentoRO;
    }

    public void setTipoProcessoDocumentoRO(
            TipoProcessoDocumento tipoProcessoDocumentoRO) {
        this.tipoProcessoDocumentoRO = tipoProcessoDocumentoRO;
    }

    public TipoProcessoDocumento getTipoProcessoDocumentoRO() {
        return tipoProcessoDocumentoRO;
    }

    public void setTarefaId(Long tarefaId) {
        this.tarefaId = tarefaId;
    }

    public Long getTarefaId() {
        return tarefaId;
    }

    public boolean isIniciaExterno() {
        return iniciaExterno;
    }

    public void setIniciaExterno(boolean iniciaExterno) {
        this.iniciaExterno = iniciaExterno;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public void setCertChain(String certChain) {
        this.certChain = certChain;
    }

    public String getCertChain() {
        return certChain;
    }

    public void setProcessoDocumentoBin(
            ProcessoDocumentoBin processoDocumentoBin) {
        this.processoDocumentoBin = processoDocumentoBin;
    }

    public ProcessoDocumentoBin getProcessoDocumentoBin() {
        return processoDocumentoBin;
    }

    public void setPdFluxo(Documento pdFluxo) {
        this.pdFluxo = pdFluxo;
    }

    public Documento getPdFluxo() {
        return pdFluxo;
    }

    public void setIdProcessoDocumento(Integer idProcessoDocumento) {
        this.idProcessoDocumento = idProcessoDocumento;
    }

    public Integer getIdProcessoDocumento() {
        return idProcessoDocumento;
    }

    public String getNumeroProcesso(int idProcesso) {
        Processo processo = processoManager.find(idProcesso);
        if (processo != null) {
            return processo.getNumeroProcesso();
        }
        return String.valueOf(idProcesso);
    }

}
