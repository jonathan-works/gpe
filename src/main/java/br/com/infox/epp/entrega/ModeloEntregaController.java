package br.com.infox.epp.entrega;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.documento.ClassificacaoDocumentoSearch;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento_;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.modelo.ClassificacaoDocumentoEntrega;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;
import br.com.infox.epp.entrega.modelo.TipoResponsavelEntrega;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.entity.ModeloPasta_;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.tipoParte.TipoParteSearch;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ModeloEntregaController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
    @Inject private ModeloEntregaSearch modeloEntregaSearch;
    @Inject private ModeloEntregaDao modeloEntregaDao;

    private Integer id;
    private List<CategoriaEntregaItem> itens;
    private Date dataLimite;
    private Date dataLiberacao;
    private ModeloDocumento modeloCertidao;
    private Boolean ativo;
    private ModeloPasta modeloPasta;
    
    @Inject private TipoParteSearch tipoParteSearch;
    private TipoParte tipoResponsavel;
    private Boolean tipoResponsavelObrigatorio;
    private List<TipoResponsavelEntrega> tiposResponsaveis;

    @Inject private ClassificacaoDocumentoSearch classificacaoDocumentoSearch;
    private ClassificacaoDocumento classificacaoDocumentoEntrega;
    private Boolean classificacaoDocumentoEntregaMultiplosDocumentos;
    private Boolean classificacaoDocumentoEntregaObrigatorio;
    private List<ClassificacaoDocumentoEntrega> classificacoesDocumentosEntrega;

    @PostConstruct
    public void init() {
        itens = new ArrayList<>();
    }

    public void iniciarConfiguracao(String[] path) {
        this.itens = new ArrayList<>();
        for (String codigoItem : path) {
            itens.add(categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoItem));
        }
        initModeloEntrega(modeloEntregaSearch.findWithItems(itens));
    }

    private void initModeloEntrega(ModeloEntrega modeloEntrega) {
        this.id = null;
        this.modeloCertidao = null;
        this.modeloPasta = null;
        this.dataLimite = null;
        this.dataLiberacao = new Date();
        this.classificacoesDocumentosEntrega = new ArrayList<>();
        this.tiposResponsaveis = new ArrayList<>();
        this.ativo = Boolean.TRUE;
        if (modeloEntrega != null) {
            this.id = modeloEntrega.getId();
            this.modeloCertidao = modeloEntrega.getModeloCertidao();
            this.modeloPasta = modeloEntrega.getModeloPasta();
            this.dataLiberacao = modeloEntrega.getDataLiberacao();
            this.dataLimite = modeloEntrega.getDataLimite();
            this.classificacoesDocumentosEntrega = modeloEntrega.getDocumentosEntrega();
            this.tiposResponsaveis = modeloEntrega.getTiposResponsaveis();
            this.ativo=modeloEntrega.getAtivo();
            this.itens = modeloEntrega.getItens();
            return;
        }
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

    public List<TipoResponsavelEntrega> getTiposResponsaveis() {
        return tiposResponsaveis;
    }

    public void setTiposResponsaveis(List<TipoResponsavelEntrega> tiposResponsaveis) {
        this.tiposResponsaveis = tiposResponsaveis;
    }

    public List<ClassificacaoDocumentoEntrega> getClassificacoesDocumentosEntrega() {
        return classificacoesDocumentosEntrega;
    }

    public void setClassificacoesDocumentosEntrega(List<ClassificacaoDocumentoEntrega> classificacoesDocumentosEntrega) {
        this.classificacoesDocumentosEntrega = classificacoesDocumentosEntrega;
    }

    public List<ModeloDocumento> completeModeloCertidao(String consulta) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ModeloDocumento> cq = cb.createQuery(ModeloDocumento.class);
        Root<ModeloDocumento> modelo = cq.from(ModeloDocumento.class);

        cq = cq.select(modelo).where(cb.like(cb.lower(modelo.get(ModeloDocumento_.tituloModeloDocumento)),
                cb.lower(cb.literal("%" + consulta.toLowerCase() + "%"))));
        return em.createQuery(cq).getResultList();
    }
    
    public List<ModeloPasta> completeModeloPasta(String consulta) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ModeloPasta> cq = cb.createQuery(ModeloPasta.class);
        Root<ModeloPasta> modelo = cq.from(ModeloPasta.class);
        
        Predicate descricaoLike = cb.like(cb.lower(modelo.get(ModeloPasta_.descricao)),
                cb.lower(cb.literal("%" + consulta.toLowerCase() + "%")));
        cq = cq.select(modelo).where(descricaoLike);
        return em.createQuery(cq).getResultList();
    }

    public List<TipoParte> completeTipoResponsavel(String pattern) {
        List<TipoParte> result = tipoParteSearch.findTipoParteWithDescricaoLike(pattern);
        for (TipoResponsavelEntrega tipoResponsavelEntrega : getTiposResponsaveis()) {
            result.remove(tipoResponsavelEntrega.getTipoParte());
        }
        return result;
    }

    public List<ClassificacaoDocumento> completeClassificacaoDocumento(String pattern) {
        List<ClassificacaoDocumento> result = classificacaoDocumentoSearch.findClassificacaoDocumentoWithDescricaoLike(pattern);
        for (ClassificacaoDocumentoEntrega documentoEntrega : getClassificacoesDocumentosEntrega()) {
            result.remove(documentoEntrega.getClassificacaoDocumento());
        }
        return result;
    }

    @ExceptionHandled
    public void adicionarDocumentoEntrega() {
        ClassificacaoDocumentoEntrega classificacaoDocumentoEntrega = new ClassificacaoDocumentoEntrega();
        classificacaoDocumentoEntrega.setClassificacaoDocumento(getClassificacaoDocumentoEntrega());
        classificacaoDocumentoEntrega.setMultiplosDocumentos(Boolean.TRUE.equals(getClassificacaoDocumentoEntregaMultiplosDocumentos()));
        classificacaoDocumentoEntrega.setObrigatorio(Boolean.TRUE.equals(getClassificacaoDocumentoEntregaObrigatorio()));
        
        getClassificacoesDocumentosEntrega().add(classificacaoDocumentoEntrega);
        setClassificacaoDocumentoEntrega(null);
        setClassificacaoDocumentoEntregaMultiplosDocumentos(null);
        setClassificacaoDocumentoEntregaObrigatorio(null);
    }

    @ExceptionHandled
    public void adicionarResponsavel() {
        TipoResponsavelEntrega tipoResponsavelEntrega = new TipoResponsavelEntrega();
        tipoResponsavelEntrega.setObrigatorio(Boolean.TRUE.equals(getTipoResponsavelObrigatorio()));
        tipoResponsavelEntrega.setTipoParte(getTipoResponsavel());
        getTiposResponsaveis().add(tipoResponsavelEntrega);
        setTipoResponsavelObrigatorio(null);
        setTipoResponsavel(null);
    }

    private void applyChanges(ModeloEntrega modeloEntrega) {
        modeloEntrega.setItens(getItens());
        modeloEntrega.setDataLimite(getDataLimite());
        modeloEntrega.setDataLiberacao(getDataLiberacao());
        modeloEntrega.setModeloCertidao(getModeloCertidao());
        modeloEntrega.setModeloPasta(getModeloPasta());
        for (TipoResponsavelEntrega tipoResponsavelEntrega : getTiposResponsaveis()) {
            tipoResponsavelEntrega.setModeloEntrega(modeloEntrega);
        }
        for (ClassificacaoDocumentoEntrega classificacaoDocumentoEntrega : getClassificacoesDocumentosEntrega()) {
            classificacaoDocumentoEntrega.setModeloEntrega(modeloEntrega);
        }
        modeloEntrega.setTiposResponsaveis(getTiposResponsaveis());
        modeloEntrega.setDocumentosEntrega(getClassificacoesDocumentosEntrega());
        modeloEntrega.setAtivo(getAtivo());
    }

    @ExceptionHandled
    public void save() {
        ModeloEntrega modeloEntrega = new ModeloEntrega();
        if (id != null) {
            modeloEntrega = modeloEntregaDao.findById(id);
        }
        applyChanges(modeloEntrega);
        if (id == null) {
            persist(modeloEntrega);
        } else {
            modeloEntrega = update(modeloEntrega);
        }
        initModeloEntrega(modeloEntrega);
    }

    public TipoParte getTipoResponsavel() {
        return tipoResponsavel;
    }

    public void setTipoResponsavel(TipoParte tipoResponsavel) {
        this.tipoResponsavel = tipoResponsavel;
    }

    public Boolean getTipoResponsavelObrigatorio() {
        return tipoResponsavelObrigatorio;
    }

    public void setTipoResponsavelObrigatorio(Boolean tipoResponsavelObrigatorio) {
        this.tipoResponsavelObrigatorio = tipoResponsavelObrigatorio;
    }

    public ClassificacaoDocumento getClassificacaoDocumentoEntrega() {
        return classificacaoDocumentoEntrega;
    }

    public void setClassificacaoDocumentoEntrega(ClassificacaoDocumento documentoEntrega) {
        this.classificacaoDocumentoEntrega = documentoEntrega;
    }

    public Boolean getClassificacaoDocumentoEntregaMultiplosDocumentos() {
        return classificacaoDocumentoEntregaMultiplosDocumentos;
    }

    public void setClassificacaoDocumentoEntregaMultiplosDocumentos(Boolean documentoEntregaMultiplosDocumentos) {
        this.classificacaoDocumentoEntregaMultiplosDocumentos = documentoEntregaMultiplosDocumentos;
    }

    public Boolean getClassificacaoDocumentoEntregaObrigatorio() {
        return classificacaoDocumentoEntregaObrigatorio;
    }

    public void setClassificacaoDocumentoEntregaObrigatorio(Boolean documentoEntregaObrigatorio) {
        this.classificacaoDocumentoEntregaObrigatorio = documentoEntregaObrigatorio;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private ModeloEntrega update(ModeloEntrega modeloEntrega) {
        try {
            UserTransaction transaction = Transaction.instance();
            transaction.begin();
            ModeloEntrega update = modeloEntregaDao.update(modeloEntrega);
            transaction.commit();
            return update;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void persist(ModeloEntrega modeloEntrega) {
        try {
            UserTransaction transaction = Transaction.instance();
            transaction.begin();
            modeloEntregaDao.persist(modeloEntrega);
            transaction.commit();
            return;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
