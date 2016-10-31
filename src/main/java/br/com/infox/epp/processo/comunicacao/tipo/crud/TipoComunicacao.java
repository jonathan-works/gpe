package br.com.infox.epp.processo.comunicacao.tipo.crud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;

@Entity
@Table(name = "tb_tipo_comunicacao")
@NamedQueries({
	@NamedQuery(name = TipoComunicacaoQuery.LIST_TIPO_COMUNICACAO_ATIVOS, query = TipoComunicacaoQuery.LIST_TIPO_COMUNICACAO_ATIVOS_QUERY)
})
public class TipoComunicacao implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "TipoComunicacaoGenerator", allocationSize = 1, initialValue = 1, sequenceName = "sq_tipo_comunicacao")
    @GeneratedValue(generator = "TipoComunicacaoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tipo_comunicacao")
    private Long id;
    
    @NotNull
    @Column(name = "ds_tipo_comunicacao", nullable = false, length = LengthConstants.DESCRICAO_MEDIA, unique = true)
    private String descricao;
    
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "cd_tipo_comunicacao", nullable = false, length = 150, unique = true)
    private String codigo;

	@Min(0)
    @NotNull
    @Column(name = "nr_dias_prazo_ciencia", nullable = false)
    private Integer quantidadeDiasCiencia;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tp_uso_comunicacao", nullable = false)
    private TipoUsoComunicacaoEnum tipoUsoComunicacao = TipoUsoComunicacaoEnum.A;
	
    @NotNull
    @Column(name = "in_ativo", nullable = false)
    private Boolean ativo;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_tipo_modelo_documento")
    private TipoModeloDocumento tipoModeloDocumento;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_classificacao_documento")
    private ClassificacaoDocumento classificacaoDocumento;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoComunicacao", orphanRemoval = true)
    private List<TipoComunicacaoClassificacaoDocumento> tipoComunicacaoClassificacaoDocumentos = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_classificacao_ped_prorroga", nullable = true)
    private ClassificacaoDocumento classificacaoProrrogacao;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Integer getQuantidadeDiasCiencia() {
        return quantidadeDiasCiencia;
    }

    public void setQuantidadeDiasCiencia(Integer quantidadeDiasCiencia) {
        this.quantidadeDiasCiencia = quantidadeDiasCiencia;
    }
    
	public TipoUsoComunicacaoEnum getTipoUsoComunicacao() {
		return tipoUsoComunicacao;
	}

	public void setTipoUsoComunicacao(TipoUsoComunicacaoEnum tipoUsoComunicacao) {
		this.tipoUsoComunicacao = tipoUsoComunicacao;
	}

	public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    public ClassificacaoDocumento getClassificacaoDocumento() {
		return classificacaoDocumento;
	}
    
    public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
		this.classificacaoDocumento = classificacaoDocumento;
	}
    
    public TipoModeloDocumento getTipoModeloDocumento() {
		return tipoModeloDocumento;
	}
    
    public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
		this.tipoModeloDocumento = tipoModeloDocumento;
	}

    public List<TipoComunicacaoClassificacaoDocumento> getTipoComunicacaoClassificacaoDocumentos() {
		return tipoComunicacaoClassificacaoDocumentos;
	}
    
    public void setTipoComunicacaoClassificacaoDocumentos(List<TipoComunicacaoClassificacaoDocumento> tipoComunicacaoClassificacaoDocumentos) {
		this.tipoComunicacaoClassificacaoDocumentos = tipoComunicacaoClassificacaoDocumentos;
	}
    
	public ClassificacaoDocumento getClassificacaoProrrogacao() {
		return classificacaoProrrogacao;
	}

	public void setClassificacaoProrrogacao(ClassificacaoDocumento classificacaoProrrogacao) {
		this.classificacaoProrrogacao = classificacaoProrrogacao;
	}

	@Override
    public String toString() {
    	return descricao;
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TipoComunicacao))
			return false;
		TipoComunicacao other = (TipoComunicacao) obj;
		if (getDescricao() == null) {
			if (other.getDescricao() != null)
				return false;
		} else if (!getDescricao().equals(other.getDescricao()))
			return false;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}
