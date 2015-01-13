package br.com.infox.epp.processo.comunicacao.tipo.crud;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
    @Column(name = "nr_dias_prazo_ciencia", nullable = false)
    private Integer quantidadeDiasCiencia;
    
    @NotNull
    @Column(name = "in_ativo", nullable = false)
    private Boolean ativo;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_tipo_modelo_documento")
    private TipoModeloDocumento tipoModeloDocumento;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_classificacao_documento")
    private ClassificacaoDocumento classificacaoDocumento;

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

    public Integer getQuantidadeDiasCiencia() {
        return quantidadeDiasCiencia;
    }

    public void setQuantidadeDiasCiencia(Integer quantidadeDiasCiencia) {
        this.quantidadeDiasCiencia = quantidadeDiasCiencia;
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
    
    @Override
    public String toString() {
    	return descricao;
    }
}
