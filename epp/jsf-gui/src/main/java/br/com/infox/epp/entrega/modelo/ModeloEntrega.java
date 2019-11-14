package br.com.infox.epp.entrega.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.fluxo.entity.ModeloPasta;

@Entity
@Table(name="tb_modelo_entrega")
public class ModeloEntrega implements Serializable {

    private static final String GENERATOR_NAME = "GeneratorModeloEntrega";

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR_NAME, sequenceName = "sq_modelo_entrega")
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_modelo_entrega", unique = true, nullable = false)
    private Long id;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dt_limite_entrega",  nullable=false)
    private Date dataLimite;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dt_liberacao",  nullable=true)
    private Date dataLiberacao;
    
    @NotNull
    @Column(name="in_ativo", nullable=false)
    private Boolean ativo;
    
    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_modelo_pasta")
    private ModeloPasta modeloPasta;
    
    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_modelo_certidao")
    private ModeloDocumento modeloCertidao;
    
    @NotNull
    @Column(name="in_sinal_disparado", nullable = false)
    private Boolean sinalDisparado = false;

    @NotNull
    @Version
    @Column(name="nr_version", nullable = false)
    private Integer version;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "modeloEntrega")
    private List<ModeloEntregaItem> itensModelo = new ArrayList<>();

    @JoinColumn(name="id_modelo_entrega")
    @OneToMany(fetch=FetchType.LAZY, orphanRemoval=true, cascade=CascadeType.ALL)
    private List<TipoResponsavelEntrega> tiposResponsaveis;
    
    @JoinColumn(name="id_modelo_entrega")
    @OneToMany(fetch=FetchType.LAZY, orphanRemoval=true, cascade=CascadeType.ALL)
    private List<ClassificacaoDocumentoEntrega> documentosEntrega;

    @PrePersist
    @PreUpdate
    private void setDataLimiteEndOfDay() {
		if (getDataLimite() != null) {
			setDataLimite(DateUtil.getEndOfDay(getDataLimite()));
		}
	}
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
    public Boolean getAtivo() {
        return ativo;
    }
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    public ModeloDocumento getModeloCertidao() {
        return modeloCertidao;
    }

    public void setModeloCertidao(ModeloDocumento modeloCertidao) {
        this.modeloCertidao = modeloCertidao;
    }
    public ModeloPasta getModeloPasta() {
        return modeloPasta;
    }
    public void setModeloPasta(ModeloPasta modeloPasta) {
        this.modeloPasta = modeloPasta;
    }
    public Boolean getSinalDisparado() {
        return sinalDisparado;
    }

    public void setSinalDisparado(Boolean sinalDisparado) {
        this.sinalDisparado = sinalDisparado;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<ModeloEntregaItem> getItensModelo() {
		return itensModelo;
	}
    
    public void setItensModelo(List<ModeloEntregaItem> itens) {
		this.itensModelo = itens;
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
    
    @Transient
    public List<CategoriaEntregaItem> getItens() {
		List<CategoriaEntregaItem> categoriaItens = new ArrayList<>();
		for (ModeloEntregaItem itemModelo : getItensModelo()) {
			categoriaItens.add(itemModelo.getItem());
		}
		return categoriaItens;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ModeloEntrega))
			return false;
		ModeloEntrega other = (ModeloEntrega) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
    
    

}
