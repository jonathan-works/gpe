package br.com.infox.epp.fluxo.entity;

import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.DEFINICAO_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.DEFINICAO_BY_FLUXO_NOME_QUERY;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.DEFINICAO_BY_ID_PROCESSO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.DEFINICAO_BY_ID_PROCESSO_QUERY;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.DEFINICAO_VISIVEL_PAINEL_BY_ID_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.DEFINICAO_VISIVEL_PAINEL_BY_ID_FLUXO_QUERY;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.GENERATOR_DEFINICAO_VARIAVEL_PROCESSO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.ID_DEFINICAO_VARIAVEL_PROCESSO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.ID_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.LABEL;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.LIST_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.LIST_BY_FLUXO_QUERY;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.NOME_VARIAVEL;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.SEQUENCE_DEFINICAO_VARIAVEL_PROCESSO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.TABLE_DEFINICAO_VARIAVEL_PROCESSO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.TOTAL_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.TOTAL_BY_FLUXO_QUERY;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.VALOR_PADRAO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.VISIVEL;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.VISIVEL_PAINEL;

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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;

@Entity
@Table(name = TABLE_DEFINICAO_VARIAVEL_PROCESSO, uniqueConstraints = { @UniqueConstraint(columnNames = {
    NOME_VARIAVEL, ID_FLUXO }) })
@NamedQueries(value = {
    @NamedQuery(name = DEFINICAO_BY_FLUXO, query = DEFINICAO_BY_FLUXO_NOME_QUERY),
    @NamedQuery(name = LIST_BY_FLUXO, query = LIST_BY_FLUXO_QUERY),
    @NamedQuery(name = TOTAL_BY_FLUXO, query = TOTAL_BY_FLUXO_QUERY),
    @NamedQuery(name = DEFINICAO_BY_ID_PROCESSO, query = DEFINICAO_BY_ID_PROCESSO_QUERY),
    @NamedQuery(name = DEFINICAO_VISIVEL_PAINEL_BY_ID_FLUXO, query = DEFINICAO_VISIVEL_PAINEL_BY_ID_FLUXO_QUERY)
})
public class DefinicaoVariavelProcesso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR_DEFINICAO_VARIAVEL_PROCESSO, sequenceName = SEQUENCE_DEFINICAO_VARIAVEL_PROCESSO)
    @GeneratedValue(generator = GENERATOR_DEFINICAO_VARIAVEL_PROCESSO, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_DEFINICAO_VARIAVEL_PROCESSO, nullable = false, unique = true)
    private Long id;

    @Column(name = NOME_VARIAVEL, nullable = false, length = LengthConstants.DESCRICAO_GRANDE)
    @Size(min = 1, max = LengthConstants.DESCRICAO_GRANDE, message = "{beanValidation.size}")
    @NotNull(message = "{beanValidation.notNull}")
    private String nome;

    @Column(name = LABEL, nullable = false, length = LengthConstants.DESCRICAO_ENTIDADE)
    @Size(min = 1, max = LengthConstants.DESCRICAO_ENTIDADE, message = "{beanValidation.notNull}")
    @NotNull(message = "{beanValidation.notNull}")
    private String label;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ID_FLUXO, nullable = false)
    private Fluxo fluxo;

    @Column(name = VISIVEL)
    private Boolean visivel;
    
    @NotNull
    @Column(name = VISIVEL_PAINEL)
    private Boolean visivelPainel;
    
    @Size(min = 0, max = LengthConstants.DESCRICAO_GRANDE, message = "{beanValidation.size}")
    @Column(name=VALOR_PADRAO, length = LengthConstants.DESCRICAO_GRANDE)
    private String valorPadrao;
    
    @NotNull
    @Column(name = "nr_ordem", nullable = false)
    private Integer ordem;
    
    @Version
    @Column(name = "nr_version", nullable = false)
    private Long version = 0L;
    
    public DefinicaoVariavelProcesso() {
    	setVisivel(Boolean.TRUE);
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Fluxo getFluxo() {
        return fluxo;
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }

	public Boolean getVisivel() {
		return visivel;
	}

	public void setVisivel(Boolean visivel) {
		this.visivel = visivel;
	}
	
	public Boolean getVisivelPainel() {
        return visivelPainel;
    }

    public void setVisivelPainel(Boolean visivelPainel) {
        this.visivelPainel = visivelPainel;
    }
    
    public String getValorPadrao() {
		return valorPadrao;
	}

	public void setValorPadrao(String valorPadrao) {
		this.valorPadrao = valorPadrao;
	}

	public Integer getOrdem() {
		return ordem;
	}
	
	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}
	
	public Long getVersion() {
		return version;
	}
	
	public void setVersion(Long version) {
		this.version = version;
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fluxo == null) ? 0 : fluxo.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((nome == null) ? 0 : nome.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefinicaoVariavelProcesso)) {
            return false;
        }
        DefinicaoVariavelProcesso other = (DefinicaoVariavelProcesso) obj;
        if (fluxo == null) {
            if (other.fluxo != null) {
                return false;
            }
        } else if (!fluxo.equals(other.fluxo)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (nome == null) {
            if (other.nome != null) {
                return false;
            }
        } else if (!nome.equals(other.nome)) {
            return false;
        }
        return true;
    }
}
