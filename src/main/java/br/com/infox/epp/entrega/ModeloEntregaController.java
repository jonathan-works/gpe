package br.com.infox.epp.entrega;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento_;
import br.com.infox.epp.documento.list.ModeloDocumentoList;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.modelo.ClassificacaoDocumentoEntrega;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;
import br.com.infox.epp.entrega.modelo.TipoResponsavelEntrega;

public class ModeloEntregaController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
    @Inject private ModeloEntregaSearch modeloEntregaSearch;
    
    private Integer id;
    private List<CategoriaEntregaItem> itens;
    private Date dataLimite;
    private Date dataLiberacao;
    private ModeloDocumento modeloCertidao;
    private List<TipoResponsavelEntrega> tiposResponsaveis;
    private List<ClassificacaoDocumentoEntrega> documentosEntrega;
    
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
        if (modeloEntrega != null){
            this.id=modeloEntrega.getId();
            this.documentosEntrega=modeloEntrega.getDocumentosEntrega();
            this.modeloCertidao = modeloEntrega.getModeloCertidao();
            this.dataLiberacao = modeloEntrega.getDataLiberacao();
            this.dataLimite = modeloEntrega.getDataLimite();
            this.tiposResponsaveis = modeloEntrega.getTiposResponsaveis();
            return;
        }
        this.id=null;
        this.modeloCertidao = null;
        this.dataLimite = null;
        this.dataLiberacao = new Date();
        this.documentosEntrega=new ArrayList<>();
        this.tiposResponsaveis = new ArrayList<>();
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

    public Date getDataLiberacao() {
        return dataLiberacao;
    }

    public void setDataLiberacao(Date dataLiberacao) {
        this.dataLiberacao = dataLiberacao;
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

    public List<ClassificacaoDocumentoEntrega> getDocumentosEntrega() {
        return documentosEntrega;
    }

    public void setDocumentosEntrega(List<ClassificacaoDocumentoEntrega> documentosEntrega) {
        this.documentosEntrega = documentosEntrega;
    }

    public List<ModeloDocumento> completeModeloCertidao(String consulta){
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ModeloDocumento> cq = cb.createQuery(ModeloDocumento.class);
        Root<ModeloDocumento> modelo = cq.from(ModeloDocumento.class);
        
        cq = cq.select(modelo).where(cb.like(cb.lower(modelo.get(ModeloDocumento_.tituloModeloDocumento)), cb.lower(cb.literal( "%"+consulta.toLowerCase()+"%" ))));
        
        return em.createQuery(cq).getResultList();
    }
    public List<ClassificacaoDocumento> completeClassificacaoDocumento(String consulta){
        return new ArrayList<>();
    }

    public ClassificacaoDocumento getDocumentoEntrega(){
        return new ClassificacaoDocumento();
    }
    public void setDocumentoEntrega(ClassificacaoDocumento classificacaoDocumento){
    }
    
    public void save(){
        if (id==null){
            System.out.println("Save");
        } else {
            System.out.println("Update");
        }
    }
    
}
