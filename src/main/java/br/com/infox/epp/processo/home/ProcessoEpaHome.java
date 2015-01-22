package br.com.infox.epp.processo.home;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.messages.Messages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException.Motivo;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.localizacao.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.sigilo.service.SigiloProcessoService;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.seam.context.ContextFacade;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.util.ComponentUtil;
import br.com.itx.component.AbstractHome;

/**
 * Deprecated : A superclasse AbstractHome está em processo de remoção, assim as
 * funções de ProcessoHome estão sendo repassadas a novos componentes
 */
@Deprecated
@Name(ProcessoEpaHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoEpaHome extends AbstractHome<Processo> {

    private static final LogProvider LOG = Logging.getLogProvider(Processo.class);
    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoEpaHome";

    private static final int ERRO_AO_VERIFICAR_CERTIFICADO = 0;

    @In 
    private ProcessoLocalizacaoIbpmDAO processoLocalizacaoIbpmDAO;
    @In 
    private ProcessoManager processoManager;
    @In 
    private AssinaturaDocumentoService assinaturaDocumentoService;
    @In 
    private DocumentoManager documentoManager;
    @In 
    private SigiloProcessoService sigiloProcessoService;
    @In 
    private MetadadoProcessoManager metadadoProcessoManager;
    @In
    private SituacaoProcessoDAO situacaoProcessoDAO;
    @In
    private Authenticator authenticator;

    private ModeloDocumento modeloDocumento;
    private ClassificacaoDocumento classificacaoDocumento;
    private ClassificacaoDocumento classificacaoDocumentoRO;
    private DocumentoBin documentoBin = new DocumentoBin();
    private String modeloDocumentoRO;
    private String observacaoMovimentacao;
    private boolean iniciaExterno;
    private String signature;
    private String certChain;
    private Documento pdFluxo;
    private Integer idDocumento;
    private List<MetadadoProcesso> detalhesMetadados;

    private Long tarefaId;

    // TODO confirmar se é método realmente não é mais utilizado e removê-lo
    public void limpar() {
        modeloDocumento = null;
        classificacaoDocumento = null;
        newInstance();
    }

    public void iniciarTarefaProcesso() {
        try {
            processoManager.iniciarTask(instance, tarefaId, Authenticator.getUsuarioPerfilAtual());
        } catch (java.lang.NullPointerException e) {
            LOG.error("ProcessoEpaHome.iniciarTarefaProcesso()", e);
        } catch (DAOException e) {
            LOG.error("Erro ao vincular Usuario", e);
        }
    }

    public List<MetadadoProcesso> getDetalhesMetadados() {
        if (detalhesMetadados == null) {
            detalhesMetadados = metadadoProcessoManager.getListMetadadoVisivelByProcesso(getInstance());
        }
        return detalhesMetadados;
    }

    public void visualizarTarefaProcesso() {
        processoManager.visualizarTask(instance, tarefaId, Authenticator.getUsuarioPerfilAtual());
    }

    public static ProcessoEpaHome instance() {
        return ComponentUtil.getComponent(NAME);
    }

    private void limparAssinatura() {
        certChain = null;
        signature = null;
    }

    public boolean checarVisibilidade() {
    	MetadadoProcesso metadadoProcesso = getInstance().getMetadado(EppMetadadoProvider.TIPO_PROCESSO);
    	TipoProcesso tipoProcesso = metadadoProcesso != null ? metadadoProcesso.<TipoProcesso>getValue() : null;
    	boolean visivel = situacaoProcessoDAO.canAccessProcesso(getInstance().getIdProcesso(), tipoProcesso);
        if (!visivel) {
        	avisarNaoHaPermissaoParaAcessarProcesso();
        }
    	return visivel;
    }

    private void avisarNaoHaPermissaoParaAcessarProcesso() {
        ContextFacade.setToEventContext("canClosePanel", true);
        FacesMessages.instance().clear();
        throw new ApplicationException("Sem permissão para acessar o processo: " + getInstance().getNumeroProcesso());
    }

    public Integer salvarProcessoDocumentoFluxo(Object value,
            Integer idDocumento, Boolean assinado, String label)
            throws CertificadoException {
        Documento documento = buscarProcessoDocumento(idDocumento);
        setIdDocumento(idDocumento);
        Integer result = idDocumento;
        FacesMessages messages = FacesMessages.instance();
        try {
            if (classificacaoDocumento != null) {
                String msgKey = "Registro gravado com sucesso!";
                if (documento != null) {
                    if (assinaturaDocumentoService.isDocumentoAssinado(
                            documento, Authenticator.getUsuarioLogado())) {
                        return result;
                    }
                    atualizarProcessoDocumentoFluxo(value, idDocumento,
                            assinado);
                    msgKey = "ProcessoDocumento_updated";
                } else {
                    result = inserirProcessoDocumentoFluxo(value, label,
                            assinado);
                    msgKey = "ProcessoDocumento_created";
                }
                messages.clear();
                messages.add(StatusMessage.Severity.INFO,
                        Messages.resolveMessage(msgKey));
            }
        } catch (DAOException | AssinaturaException e) {
            LOG.error("Não foi possível salvar o Documento " + idDocumento, e);
            messages.clear();
            messages.add(e.getMessage());
            result = null;
        }
        return result;
    }

    private Documento buscarProcessoDocumento(Integer idDoc) {
        return documentoManager.find(idDoc);
    }

    private void atualizarProcessoDocumentoFluxo(Object value, Integer idDoc,
            Boolean assinado) throws CertificadoException, AssinaturaException,
            DAOException {
        if (validacaoCertificadoBemSucedida(assinado)) {
            Documento documento = buscarProcessoDocumento(idDoc);
            DocumentoBin processoDocumentoBin = documento.getDocumentoBin();
            String modeloDocumento = getDescricaoModeloDocumentoFluxoByValue(
                    value, processoDocumentoBin.getModeloDocumento());
            UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
            documento.setPerfilTemplate(usuarioPerfil.getPerfilTemplate());
            documento.getDocumentoBin().setModeloDocumento(modeloDocumento);
            if (assinado) {
                assinaturaDocumentoService.assinarDocumento(documento,
                        usuarioPerfil, certChain, signature);
            }
            gravarAlteracoes(documento, processoDocumentoBin);
        }
    }

    private String getDescricaoModeloDocumentoFluxoByValue(Object value,
            String modeloDocumentoFluxo) {
        if (value == null) {
            value = modeloDocumentoFluxo != null ? modeloDocumentoFluxo : "";
        }
        return value.toString();
    }

    private void gravarAlteracoes(Documento documento, DocumentoBin documentoBin) {
        documento.setClassificacaoDocumento(classificacaoDocumento);
        getEntityManager().merge(documento);
        setIdDocumento(documento.getId());
        getEntityManager().merge(documentoBin);
        getEntityManager().flush();
    }

    private Integer inserirProcessoDocumentoFluxo(Object value, String label,
            Boolean assinado) throws CertificadoException, AssinaturaException {
        if (validacaoCertificadoBemSucedida(assinado)) {
            try {
                Object newValue = processoManager.getAlteracaoModeloDocumento(
                        documentoBin, value);
                DocumentoBin documentoBin = processoManager
                        .createDocumentoBin(newValue);
                label = label == null ? "-" : label;
                Documento doc;
                doc = documentoManager.createDocumento(getInstance(), label,
                        documentoBin, getClassificacaoDocumento());
                final int idDocumento = doc.getId();
                setIdDocumento(idDocumento);
                if (assinado && certChain != null && signature != null) {
                    assinaturaDocumentoService.assinarDocumento(doc,
                            Authenticator.getUsuarioPerfilAtual(), certChain,
                            signature);
                }
                return idDocumento;
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

    public void carregarDadosFluxo(Integer idDocumento) {
        Documento documento = buscarProcessoDocumento(idDocumento);
        if (documento != null) {
            setPdFluxo(documento);
            documentoBin = documento.getDocumentoBin();
            setIdDocumento(documento.getId());
            setClassificacaoDocumento(documento.getClassificacaoDocumento());
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
        documentoBin.setModeloDocumento(modelo);
    }

    private boolean taskInstancePossuiModeloDocumento() {
        return TaskInstanceHome.instance().getModeloDocumento() != null;
    }

    @Override
    protected Processo createInstance() {
        Processo processo = super.createInstance();
        processo.setUsuarioCadastro(Authenticator.getUsuarioLogado());
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
        return getInstance() == null ? null : getInstance().getDocumentoList();
    }

    public boolean hasPartes() {
        return getInstance() != null && getInstance().hasPartes();
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

    public ClassificacaoDocumento getClassificacaoDocumento() {
        return classificacaoDocumento;
    }

    public void setClassificacaoDocumento(
            ClassificacaoDocumento classificacaoDocumento) {
        this.classificacaoDocumento = classificacaoDocumento;
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

    public ClassificacaoDocumento getClassificacaoDocumentoRO() {
        return classificacaoDocumentoRO;
    }

    public void setClassificacaoDocumentoRO(
            ClassificacaoDocumento classificacaoDocumentoRO) {
        this.classificacaoDocumentoRO = classificacaoDocumentoRO;
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

    public DocumentoBin getDocumentoBin() {
        return documentoBin;
    }

    public void setDocumentoBin(DocumentoBin documentoBin) {
        this.documentoBin = documentoBin;
    }

    public void setPdFluxo(Documento pdFluxo) {
        this.pdFluxo = pdFluxo;
    }

    public Documento getPdFluxo() {
        return pdFluxo;
    }

    public Integer getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Integer idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getNumeroProcesso(int idProcesso) {
        Processo processo = processoManager.find(idProcesso);
        if (processo != null) {
            return processo.getNumeroProcesso();
        }
        return String.valueOf(idProcesso);
    }

}
