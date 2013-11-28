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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;

@Entity
@Table(name = DefinicaoVariavelProcesso.TABLE_NAME, uniqueConstraints = {
	@UniqueConstraint(columnNames = {"nm_variavel", "id_fluxo"})
})
public class DefinicaoVariavelProcesso implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_definicao_variavel_processo";
	
	@Id
	@SequenceGenerator(name = "DefinicaoVariavelProcessoGenerator", sequenceName = "sq_tb_definicao_variavel_processo")
	@GeneratedValue(generator = "DefinicaoVariavelProcessoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_definicao_variavel_processo", nullable = false, unique = true)
	private Long id;
	
	@Column(name = "nm_variavel", nullable = false, length = LengthConstants.DESCRICAO_PEQUENA)
	@Size(min = 1, max = LengthConstants.DESCRICAO_PEQUENA, message = "{beanValidation.size}")
	@NotNull(message = "{beanValidation.notNull}")
	private String nome;
	
	@Column(name = "ds_label", nullable = false, length = LengthConstants.DESCRICAO_ENTIDADE)
	@Size(min = 1, max = LengthConstants.DESCRICAO_ENTIDADE, message = "{beanValidation.notNull}")
	@NotNull(message = "{beanValidation.notNull}")
	private String label;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_fluxo", nullable = false)
	private Fluxo fluxo;
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fluxo == null) ? 0 : fluxo.hashCode());
		result = prime
				* result
				+ ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DefinicaoVariavelProcesso))
			return false;
		DefinicaoVariavelProcesso other = (DefinicaoVariavelProcesso) obj;
		if (fluxo == null) {
			if (other.fluxo != null)
				return false;
		} else if (!fluxo.equals(other.fluxo))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
}
