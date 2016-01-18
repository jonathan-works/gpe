package br.com.infox.epp.processo.comunicacao.envio.action;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NonUniqueResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.faces.FacesMessages;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.lowagie.text.DocumentException;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.component.tree.LocalizacaoSubTreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacaoSubTreeHandler.LocalizacaoSubTreeSelectEvent;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoInternaService;
import br.com.infox.epp.processo.comunicacao.service.DestinatarioComunicacaoService;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacaoManager;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.system.Parametros;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;

@Named
@ViewScoped
public class EnvioComunicacaoInternaView implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EnvioComunicacaoInternaView.class.getName());
    
    @Inject
    private TipoComunicacaoManager tipoComunicacaoManager;
    @Inject
    private LocalizacaoManager localizacaoManager;
    @Inject
    private ActionMessagesService actionMessagesService;
    @Inject
    private UsuarioPerfilManager usuarioPerfilManager;
    @Inject
    private ProcessoManager processoManager;
    @Inject
    private ModeloComunicacaoManager modeloComunicacaoManager;
    @Inject
    private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
    @Inject
    private DestinatarioComunicacaoService destinatarioComunicacaoService;
    @Inject
    private ComunicacaoInternaService comunicacaoInternaService;
    @Inject
    private ModeloDocumentoManager modeloDocumentoManager;
    
    private ModeloComunicacao modeloComunicacao;
    private TipoComunicacao tipoComunicacao;
    private Boolean expedida;
    private boolean taskPage = true;

    //Variáveis de tela para o destinatário
    private Localizacao localizacaoDestino;
    private PerfilTemplate perfilDestino;
    private PessoaFisica pessoaDestinatario;
    private Boolean individual;
    
    //Variáveis de tela para o documento da Comunicação
    private ClassificacaoDocumento classificacaoDocumento;
    private ModeloDocumento modeloDocumento;
    private Boolean minuta;
    private String textoDocumento;
    
    private List<TipoComunicacao> tiposComunicacao;
    private List<PerfilTemplate> perfisPermitidos;
    private List<PessoaFisica> pessoasDestinatario;
    private List<ClassificacaoDocumento> classificacoes;
    private List<ModeloDocumento> modelosDocumento;
    
    @PostConstruct
    private void init() {
        loadModeloComunicacaoInterna();
        loadTiposComunicacao();
        loadClassificacoesDocumento();
        loadModelosDocumento();
        initLocalizacaoRaiz();
        loadComunicacaoExpedida();
    }
    
    private void loadModeloComunicacaoInterna() {
        Map<String, String> paramsMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (paramsMap.get("abaComunicacao") != null) {
            taskPage = !Boolean.valueOf(paramsMap.get("abaComunicacaoInterna"));
        }
        TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
        Long idModeloComunicacao = null;
        if (paramsMap.get("idModeloComunicacao") != null) {
            idModeloComunicacao = Long.valueOf(paramsMap.get("idModeloComunicacao"));
        } else if (taskPage) {
            idModeloComunicacao = (Long) taskInstance.getVariable("idModeloComunicacaoInterna-" + taskInstance.getId());
        }
        if (idModeloComunicacao == null) {
            modeloComunicacao = new ModeloComunicacao();
            Integer idProcesso = (Integer) taskInstance.getVariable("processo");
            modeloComunicacao.setProcesso(processoManager.getReference(idProcesso));
        } else {
            modeloComunicacao = modeloComunicacaoManager.find(idModeloComunicacao);
        }
        loadModeloComunicacaoProperties();
    }

    private void loadModeloComunicacaoProperties() {
        setTipoComunicacao(getModeloComunicacao().getTipoComunicacao());
        setClassificacaoDocumento(getModeloComunicacao().getClassificacaoComunicacao());
        setModeloDocumento(getModeloComunicacao().getModeloDocumento());
        setMinuta(getModeloComunicacao().isMinuta());
        setTextoDocumento(getModeloComunicacao().getTextoComunicacao());
    }

    private void initLocalizacaoRaiz() {
        try {
            Localizacao localizacaoRaiz = localizacaoManager.getLocalizacaoByCodigo(getCodigoRaizLocalizacoesComunicacaoInterna());
            if (localizacaoRaiz != null) {
                getLocalizacaoSubTree().setIdLocalizacaoPai(localizacaoRaiz.getIdLocalizacao());
            } else {
                FacesMessages.instance().add("O parâmetro raizLocalizacoesComunicacao não foi definido.");
            }
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "", e);
            if (e.getCause() instanceof NonUniqueResultException) {
                FacesMessages.instance().add("Existe mais de uma localização com o nome definido no parâmetro raizLocalizacoesComunicacao: " + getCodigoRaizLocalizacoesComunicacaoInterna());
            } else {
                actionMessagesService.handleDAOException(e);
            }
        }
    }
    
    private void loadComunicacaoExpedida() {
        setExpedida(true);
        for (DestinatarioModeloComunicacao destinatario : getModeloComunicacao().getDestinatarios()) {
            if (!destinatario.getExpedido()) {
                setExpedida(false);
                break;
            }
        }
        if (getModeloComunicacao().getDestinatarios().isEmpty()) {
            setExpedida(false);
        }
    }
    
    public void onChangeLocalizacao(@Observes @LocalizacaoSubTreeSelectEvent Localizacao localizacao) {
        if (localizacao == null) {
            perfisPermitidos = Collections.emptyList();
        } else {
            perfisPermitidos = usuarioPerfilManager.getPerfisPermitidos(localizacao);
        }
    }
    
    public void onChangePerfilDestino() {
        if (getPerfilDestino() == null || getLocalizacaoDestino() == null) {
            pessoasDestinatario = Collections.emptyList();
        } else {
            pessoasDestinatario = usuarioPerfilManager.getPessoasPermitidos(getLocalizacaoDestino(), getPerfilDestino());
        }
    }
    
    public void onChangeTipoComunicacao() {
        if (getTipoComunicacao() != null && !getTipoComunicacao().equals(getModeloComunicacao().getTipoComunicacao())) {
            getModeloComunicacao().setTipoComunicacao(getTipoComunicacao());
            modeloComunicacao = modeloComunicacaoManager.update(getModeloComunicacao());
            TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
            if (taskPage) {
                taskInstance.setVariable("idModeloComunicacaoInterna-" + taskInstance.getId(), modeloComunicacao.getId());
            }
        }
        loadClassificacoesDocumento();
        loadModelosDocumento();
    }
    
    public void onChangeModeloDocumento() {
        if (getModeloDocumento() != null) {
            String textoModelo = modeloDocumentoManager.evaluateModeloDocumento(getModeloDocumento());
            setTextoDocumento(textoModelo);
        }
    }

    private void loadClassificacoesDocumento() {
        if (getTipoComunicacao() == null || getTipoComunicacao().getClassificacaoDocumento() == null) {
            classificacoes = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(true);
        } else {
            classificacoes = new ArrayList<>(1);
            classificacoes.add(getTipoComunicacao().getClassificacaoDocumento());
        }
        if (classificacoes.size() == 1) {
            setClassificacaoDocumento(classificacoes.get(0));
        }
    }
    
    private void loadModelosDocumento() {
        if (getTipoComunicacao() != null && getTipoComunicacao().getTipoModeloDocumento() != null) {
            modelosDocumento = getTipoComunicacao().getTipoModeloDocumento().getModeloDocumentoList();
        } else {
            modelosDocumento = modeloDocumentoManager.getModeloDocumentoByPapel(Authenticator.getPapelAtual());
        }
        if (modelosDocumento.size() == 1) {
            setModeloDocumento(modelosDocumento.get(0));
            onChangeModeloDocumento();
        }
    }
    
    @ExceptionHandled(value = MethodType.PERSIST, createdMessage="Destinatário inserido com sucesso!")
    public void adicionarDestinatarioComunicacao() {
        if (getModeloComunicacao().getId() == null) {
            throw new BusinessException("Necessário um tipo de Comunicação");
        } else {
            DestinatarioModeloComunicacao destinatario = new DestinatarioModeloComunicacao();
            destinatario.setIndividual(getIndividual());
            if (getPessoaDestinatario() != null) {
                destinatario.setDestinatario(getPessoaDestinatario()); 
            } else {
                destinatario.setDestino(getLocalizacaoDestino());
                destinatario.setPerfilDestino(getPerfilDestino());
            }
            destinatario.setMeioExpedicao(MeioExpedicao.SI);
            destinatario.setModeloComunicacao(getModeloComunicacao());
            comunicacaoInternaService.gravarDestinatario(destinatario);
            clearDestinatarioProperties();
        }
    }
    
    private void clearDestinatarioProperties() {
        setIndividual(Boolean.FALSE);
        setPessoaDestinatario(null);
        setLocalizacaoDestino(null);
        setPerfilDestino(null);
        getLocalizacaoSubTree().clearTree();
    }

    @ExceptionHandled(value = MethodType.REMOVE, removedMessage="Destinatário removido com sucesso!")
    public void removerDestinatario(DestinatarioModeloComunicacao destinatario) {
        destinatarioComunicacaoService.removeDestinatarioModeloComunicacao(destinatario);
        getModeloComunicacao().getDestinatarios().remove(destinatario);
    }
    
    @ExceptionHandled(value = MethodType.UPDATE)
    public void gravar() {
        getModeloComunicacao().setMinuta(getMinuta());
        getModeloComunicacao().setModeloDocumento(getModeloDocumento());
        getModeloComunicacao().setClassificacaoComunicacao(getClassificacaoDocumento());
        getModeloComunicacao().setTextoComunicacao(getTextoDocumento());
        modeloComunicacaoManager.update(getModeloComunicacao());
    }
    
    public void enviar() {
        try {
            gravar();
            comunicacaoInternaService.enviarComunicacao(getModeloComunicacao());
            loadComunicacaoExpedida();
        } catch (BusinessException e) {
            FacesMessages.instance().add(e.getMessage());
        } catch (DAOException | IOException | DocumentException e) {
            LOGGER.log(Level.SEVERE, "enviarComunicacaoInterna", e);
            FacesMessages.instance().add(e.getMessage());
        }
    }
    
    public void clearDestinoDestinatario() {
        setIndividual(Boolean.FALSE);
        setLocalizacaoDestino(null);
        setPerfilDestino(null);
        setPessoaDestinatario(null);
    }
    
    public List<ClassificacaoDocumento> getClassificacoes() {
        return classificacoes;
    }
    
    public List<ModeloDocumento> getModelosDocumento() {
        return modelosDocumento;
    }

    public List<PerfilTemplate> getPerfisDestino() {
        return perfisPermitidos;
    }
    
    public List<PessoaFisica> getPessoasDestinatario() {
        return pessoasDestinatario;
    }
    
    private void loadTiposComunicacao() {
        tiposComunicacao = tipoComunicacaoManager.listTiposComunicacaoAtivos();
    }
    
    public Boolean getIndividual() {
        return individual;
    }

    public void setIndividual(Boolean individual) {
        this.individual = individual;
    }

    public TipoComunicacao getTipoComunicacao() {
        return tipoComunicacao;
    }
    
    public void setTipoComunicacao(TipoComunicacao tipoComunicacao) {
        this.tipoComunicacao = tipoComunicacao;
    }
    
    public Boolean getExpedida() {
        return expedida;
    }

    public void setExpedida(Boolean expedida) {
        this.expedida = expedida;
    }

    public ClassificacaoDocumento getClassificacaoDocumento() {
        return classificacaoDocumento;
    }

    public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
        this.classificacaoDocumento = classificacaoDocumento;
    }

    public ModeloDocumento getModeloDocumento() {
        return modeloDocumento;
    }

    public void setModeloDocumento(ModeloDocumento modeloDocumento) {
        this.modeloDocumento = modeloDocumento;
    }

    public Boolean getMinuta() {
        return minuta;
    }

    public void setMinuta(Boolean minuta) {
        this.minuta = minuta;
    }

    public String getTextoDocumento() {
        return textoDocumento;
    }

    public void setTextoDocumento(String textoDocumento) {
        this.textoDocumento = textoDocumento;
    }

    public List<TipoComunicacao> getTiposComunicacao() {
        return tiposComunicacao;
    }
    
    public ModeloComunicacao getModeloComunicacao() {
        return modeloComunicacao;
    }

    public void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
        this.modeloComunicacao = modeloComunicacao;
    }

    public Localizacao getLocalizacaoDestino() {
        return localizacaoDestino;
    }

    public void setLocalizacaoDestino(Localizacao localizacaoDestino) {
        this.localizacaoDestino = localizacaoDestino;
    }

    public PerfilTemplate getPerfilDestino() {
        return perfilDestino;
    }

    public void setPerfilDestino(PerfilTemplate perfilDestino) {
        this.perfilDestino = perfilDestino;
    }

    public PessoaFisica getPessoaDestinatario() {
        return pessoaDestinatario;
    }

    public void setPessoaDestinatario(PessoaFisica pessoaDestinatario) {
        this.pessoaDestinatario = pessoaDestinatario;
    }

    public String getCodigoRaizLocalizacoesComunicacaoInterna() {
        return Parametros.RAIZ_LOCALIZACOES_COMUNICACAO_INTERNA.getValue();
    }
    
    public LocalizacaoSubTreeHandler getLocalizacaoSubTree() {
        return ComponentUtil.getComponent(LocalizacaoSubTreeHandler.NAME, ScopeType.PAGE);
    }

}
