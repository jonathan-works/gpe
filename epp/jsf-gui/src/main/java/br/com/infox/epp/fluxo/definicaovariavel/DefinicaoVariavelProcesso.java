package br.com.infox.epp.fluxo.definicaovariavel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Entity
@Table(name = "tb_definicao_variavel_processo", uniqueConstraints = { 
	@UniqueConstraint(columnNames = {"nm_variavel", "id_fluxo"}) 
})
public class DefinicaoVariavelProcesso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "DefinicaoVariavelProcessoGenerator", sequenceName = "sq_definicao_variavel_processo")
    @GeneratedValue(generator = "DefinicaoVariavelProcessoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_definicao_variavel_processo", nullable = false, unique = true)
    private Long id;

    @Column(name = "nm_variavel", nullable = false, length = LengthConstants.DESCRICAO_GRANDE)
    @Size(min = 1, max = LengthConstants.DESCRICAO_GRANDE, message = "{beanValidation.size}")
    @NotNull(message = "{beanValidation.notNull}")
    private String nome;

    @Column(name = "ds_label", nullable = false, length = LengthConstants.DESCRICAO_ENTIDADE)
    @Size(min = 1, max = LengthConstants.DESCRICAO_ENTIDADE, message = "{beanValidation.notNull}")
    @NotNull(message = "{beanValidation.notNull}")
    private String label;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fluxo", nullable = false)
    private Fluxo fluxo;

    @Size(min = 0, max = LengthConstants.DESCRICAO_GRANDE, message = "{beanValidation.size}")
    @Column(name = "vl_padrao", length = LengthConstants.DESCRICAO_GRANDE)
    private String valorPadrao;

    @Version
    @Column(name = "nr_version", nullable = false)
    private Long version = 0L;
    
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
	
    public String getValorPadrao() {
		return valorPadrao;
	}

	public void setValorPadrao(String valorPadrao) {
		this.valorPadrao = valorPadrao;
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
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }
}
