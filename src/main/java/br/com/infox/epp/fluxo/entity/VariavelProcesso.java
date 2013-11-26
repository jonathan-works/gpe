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
@Table(name = VariavelProcesso.TABLE_NAME, uniqueConstraints = {
	@UniqueConstraint(columnNames = {"nm_variavel", "id_fluxo"})
})
public class VariavelProcesso implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_variavel_processo";
	
	@Id
	@SequenceGenerator(name = "VariavelProcessoGenerator", sequenceName = "sq_tb_variavel_processo", initialValue = 1)
	@GeneratedValue(generator = "VariavelProcessoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_variavel_processo", nullable = false, unique = true)
	private Long idVariavelProcesso;
	
	@Column(name = "nm_variavel", nullable = false, length = LengthConstants.DESCRICAO_PEQUENA)
	@Size(min = 1, max = LengthConstants.DESCRICAO_PEQUENA, message = "{beanValidation.size}")
	@NotNull(message = "{beanValidation.notNull}")
	private String nome;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_fluxo", nullable = false)
	private Fluxo fluxo;
	
	public Long getIdVariavelProcesso() {
		return idVariavelProcesso;
	}

	public void setIdVariavelProcesso(Long idVariavelProcesso) {
		this.idVariavelProcesso = idVariavelProcesso;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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
				+ ((idVariavelProcesso == null) ? 0 : idVariavelProcesso
						.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VariavelProcesso))
			return false;
		VariavelProcesso other = (VariavelProcesso) obj;
		if (fluxo == null) {
			if (other.fluxo != null)
				return false;
		} else if (!fluxo.equals(other.fluxo))
			return false;
		if (idVariavelProcesso == null) {
			if (other.idVariavelProcesso != null)
				return false;
		} else if (!idVariavelProcesso.equals(other.idVariavelProcesso))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
}
