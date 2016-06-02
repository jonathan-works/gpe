package br.com.infox.epp.entrega;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.modelo.ModeloDocumentoSearch;
import br.com.infox.epp.documento.pasta.ModeloPastaSearch;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.modelo.ClassificacaoDocumentoEntrega;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;
import br.com.infox.epp.entrega.modelo.TipoResponsavelEntrega;
import br.com.infox.epp.fluxo.entity.ModeloPasta;

public class ModeloEntregaController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ModeloDocumentoSearch modeloDocumentoSearch;
    @Inject
    private ModeloPastaSearch modeloPastaSearch;
    @Inject
    private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
    @Inject
    private ModeloEntregaSearch modeloEntregaSearch;
    @Inject
    private ModeloEntregaService modeloEntregaService;
    @Inject
    private ClassificacaoDocumentoEntregaController classificacaoDocumentoEntregaController;
    @Inject
    private TipoResponsavelEntregaController tipoResponsavelEntregaController;
    @Inject
    private InfoxMessages messages;
    
    private Long id;
    private List<CategoriaEntregaItem> itens;
    private Date dataLimite;
    private Date dataLiberacao;
    private ModeloDocumento modeloCertidao;
    private Boolean ativo;
    private ModeloPasta modeloPasta;

    @PostConstruct
    public void init() {
        clear();
    }

    public void validarDatas(final ComponentSystemEvent event) {
        final UIComponent panel = event.getComponent();
        final String datePattern = "{0}:{0}Input";
        final ValueHolder dataInicioComponent = (ValueHolder) panel.findComponent(format(datePattern, "dataLiberacao"));
        final ValueHolder dataFimComponent = (ValueHolder) panel.findComponent(format(datePattern, "dataLimite"));
        Date dataInicio = (Date) dataInicioComponent.getLocalValue();
        if (dataInicio != null) {
            Date dataFim = (Date) dataFimComponent.getLocalValue();
            dataFim = dataFim == null ? new Date(dataInicio.getTime()) : dataFim;
            if (dataInicio.after(dataFim)) {
                FacesMessages.instance().add(Severity.ERROR, "A data de liberação deve ser anterior à data limite.");
                FacesContext.getCurrentInstance().renderResponse();
            }
        }
    }

    public void iniciarConfiguracao(String[] path) {
        this.itens = new ArrayList<>();
        for (String codigoItem : path) {
            itens.add(categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoItem));
        }
        clear();
        initModeloEntrega(modeloEntregaSearch.findWithItems(itens));
    }

    public void clear(){
        this.id = null;
        this.modeloCertidao = null;
        this.modeloPasta = null;
        this.dataLimite = null;
        this.dataLiberacao = new Date();
        tipoResponsavelEntregaController.clear();
        classificacaoDocumentoEntregaController.clear();
        this.ativo = Boolean.TRUE;
    }
    
    private void initModeloEntrega(ModeloEntrega modeloEntrega) {
        if (modeloEntrega == null) {
            return;
        }
        this.id = modeloEntrega.getId();
        this.modeloCertidao = modeloEntrega.getModeloCertidao();
        this.modeloPasta = modeloEntrega.getModeloPasta();
        this.dataLiberacao = modeloEntrega.getDataLiberacao();
        this.dataLimite = modeloEntrega.getDataLimite();
        classificacaoDocumentoEntregaController.setClassificacoesDocumentosEntrega(modeloEntrega.getDocumentosEntrega());
        tipoResponsavelEntregaController.setTiposResponsaveis(modeloEntrega.getTiposResponsaveis());
        this.ativo=modeloEntrega.getAtivo();
        this.itens = modeloEntrega.getItens();
    }

    public List<CategoriaEntregaItem> getItens() {
        return itens;
    }

    public Date getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(Date dataLimite) {
        this.dataLimite = dataLimite;
    }
    public Boolean getAtivo() {
        return ativo;
    }
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    public Date getDataLiberacao() {
        return dataLiberacao;
    }

    public void setDataLiberacao(Date dataLiberacao) {
        this.dataLiberacao = dataLiberacao;
    }

    public ModeloPasta getModeloPasta() {
        return modeloPasta;
    }

    public void setModeloPasta(ModeloPasta modeloPasta) {
        this.modeloPasta = modeloPasta;
    }

    public ModeloDocumento getModeloCertidao() {
        return modeloCertidao;
    }

    public void setModeloCertidao(ModeloDocumento modeloCertidao) {
        this.modeloCertidao = modeloCertidao;
    }

    public List<ModeloDocumento> completeModeloCertidao(String consulta) {
        return modeloDocumentoSearch.getModeloDocumentoWithTituloLike(consulta);
    }
    
    public List<ModeloPasta> completeModeloPasta(String consulta) {
        return modeloPastaSearch.modeloPastaWithDescricaoLike(consulta);
    }

    private void applyChanges(ModeloEntrega modeloEntrega) {
        for (TipoResponsavelEntrega tipoResponsavelEntrega : tipoResponsavelEntregaController.getTiposResponsaveis()) {
            tipoResponsavelEntrega.setModeloEntrega(modeloEntrega);
        }
        for (ClassificacaoDocumentoEntrega classificacaoDocumentoEntrega : classificacaoDocumentoEntregaController.getClassificacoesDocumentosEntrega()) {
            classificacaoDocumentoEntrega.setModeloEntrega(modeloEntrega);
        }
        modeloEntrega.setItens(getItens());
        modeloEntrega.setDataLimite(getDataLimite());
        modeloEntrega.setDataLiberacao(getDataLiberacao());
        modeloEntrega.setModeloCertidao(getModeloCertidao());
        modeloEntrega.setModeloPasta(getModeloPasta());
        modeloEntrega.setTiposResponsaveis(tipoResponsavelEntregaController.getTiposResponsaveis());
        modeloEntrega.setDocumentosEntrega(classificacaoDocumentoEntregaController.getClassificacoesDocumentosEntrega());
        modeloEntrega.setAtivo(getAtivo());
    }

    public ClassificacaoDocumentoEntregaController getClassificacaoDocumentoEntregaController() {
        return classificacaoDocumentoEntregaController;
    }
    public TipoResponsavelEntregaController getTipoResponsavelEntregaController() {
        return tipoResponsavelEntregaController;
    }
    
    @ExceptionHandled
    public void save() {
        ModeloEntrega modeloEntrega = new ModeloEntrega();
        String resultMessageKey="entity_created";
        if (id != null) {
            modeloEntrega = modeloEntregaSearch.findById(id);
            if (modeloEntrega.getId()!=null){
                resultMessageKey="entity_updated";
            }
        }
        applyChanges(modeloEntrega);
        modeloEntregaService.salvarModeloEntrega(modeloEntrega);
        initModeloEntrega(modeloEntrega);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(messages.get(resultMessageKey)));
    }

}
