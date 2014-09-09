package br.com.infox.epp.fluxo.entity;

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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.fluxo.query.VariavelClassificacaoDocumentoQuery;

@Entity
@Table(name = "tb_variavel_classificacao_doc", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "nm_variavel", "id_fluxo", "id_tipo_processo_documento"
    })
})
@NamedQueries({
    @NamedQuery(name = VariavelClassificacaoDocumentoQuery.PUBLICAR, query = VariavelClassificacaoDocumentoQuery.PUBLICAR_QUERY),
    @NamedQuery(name = VariavelClassificacaoDocumentoQuery.CLASSIFICACOES_PUBLICADAS_DA_VARIAVEL, query = VariavelClassificacaoDocumentoQuery.CLASSIFICACOES_PUBLICADAS_DA_VARIAVEL_QUERY),
    @NamedQuery(name = VariavelClassificacaoDocumentoQuery.VARIAVEL_CLASSIFICACAO_LIST, query = VariavelClassificacaoDocumentoQuery.VARIAVEL_CLASSIFICACAO_LIST_QUERY),
    @NamedQuery(name = VariavelClassificacaoDocumentoQuery.FIND_VARIAVEL_CLASSIFICACAO, query = VariavelClassificacaoDocumentoQuery.FIND_VARIAVEL_CLASSIFICACAO_QUERY)
})
public class VariavelClassificacaoDocumento implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name = "VariavelClassificacaoDocumentoGenerator", allocationSize = 1, sequenceName = "sq_variavel_classificacao_doc")
    @GeneratedValue(generator = "VariavelClassificacaoDocumentoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_variavel_classificacao_doc")
    private Long id;
    
    @NotNull
    @Size(max = 255)
    @Column(name = "nm_variavel", nullable = false, length = 255)
    private String variavel;
    
    @NotNull
    @Column(name = "in_publicado", nullable = false)
    private Boolean publicado;
    
    @NotNull
    @Column(name = "in_remover_na_publicacao", nullable = false)
    private Boolean removerNaPublicacao;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fluxo", nullable = false)
    private Fluxo fluxo;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_processo_documento", nullable = false)
    private TipoProcessoDocumento classificacaoDocumento;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVariavel() {
        return variavel;
    }

    public void setVariavel(String variavel) {
        this.variavel = variavel;
    }

    public Fluxo getFluxo() {
        return fluxo;
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }

    public TipoProcessoDocumento getClassificacaoDocumento() {
        return classificacaoDocumento;
    }

    public void setClassificacaoDocumento(TipoProcessoDocumento classificacaoDocumento) {
        this.classificacaoDocumento = classificacaoDocumento;
    }
    
    public Boolean getPublicado() {
        return publicado;
    }
    
    public void setPublicado(Boolean publicado) {
        this.publicado = publicado;
    }
    
    public Boolean getRemoverNaPublicacao() {
        return removerNaPublicacao;
    }
    
    public void setRemoverNaPublicacao(Boolean removerNaPublicacao) {
        this.removerNaPublicacao = removerNaPublicacao;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((getClassificacaoDocumento() == null) ? 0
                        : getClassificacaoDocumento().hashCode());
        result = prime * result + ((getFluxo() == null) ? 0 : getFluxo().hashCode());
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result
                + ((getVariavel() == null) ? 0 : getVariavel().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof VariavelClassificacaoDocumento))
            return false;
        VariavelClassificacaoDocumento other = (VariavelClassificacaoDocumento) obj;
        if (getClassificacaoDocumento() == null) {
            if (other.getClassificacaoDocumento() != null)
                return false;
        } else if (!getClassificacaoDocumento().equals(other.getClassificacaoDocumento()))
            return false;
        if (getFluxo() == null) {
            if (other.getFluxo() != null)
                return false;
        } else if (!getFluxo().equals(other.getFluxo()))
            return false;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        if (getVariavel() == null) {
            if (other.getVariavel() != null)
                return false;
        } else if (!getVariavel().equals(other.getVariavel()))
            return false;
        return true;
    }
}
