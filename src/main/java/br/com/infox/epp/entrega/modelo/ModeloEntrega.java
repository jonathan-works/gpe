package br.com.infox.epp.entrega.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;

@Entity
@Table(name="tb_modelo_entrega")
public class ModeloEntrega implements Serializable {

    private static final String GENERATOR_NAME = "GeneratorModeloEntrega";

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR_NAME, sequenceName = "sq_modelo_entrega")
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_modelo_entrega", unique = true, nullable = false)
    private Integer id;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dt_limite_entrega",  nullable=false)
    private Date dataLimite;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dt_liberacao",  nullable=true)
    private Date dataLiberacao;
    
    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_modelo_certidao")
    private ModeloDocumento modeloCertidao;

    @JoinTable(name="tb_modelo_entrega_item", 
            joinColumns=@JoinColumn(name="id_modelo_entrega"), 
            inverseJoinColumns=@JoinColumn(name="id_categoria_entrega_item"))
    @OneToMany(fetch=FetchType.LAZY)
    private List<CategoriaEntregaItem> itens;

    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="id_modelo_entrega")
    private List<TipoResponsavelEntrega> tiposResponsaveis;
    
    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="id_modelo_entrega")
    private List<ClassificacaoDocumentoEntrega> documentosEntrega;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<CategoriaEntregaItem> getItens() {
        return itens;
    }

    public void setItens(List<CategoriaEntregaItem> itens) {
        this.itens = itens;
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

}
