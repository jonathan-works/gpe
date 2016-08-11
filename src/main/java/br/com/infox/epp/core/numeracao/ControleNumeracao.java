package br.com.infox.epp.core.numeracao;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "tb_controle_numeracao")
public class ControleNumeracao implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "ControleNumeracaoGenerator", sequenceName="sq_controle_numeracao", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "ControleNumeracaoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_controle_numeracao", nullable = true, unique = true)
	private Long id;
	
	@NotNull
	@Size(min = 1, max = 250)
	@Column(name = "nm_key", length = 250, nullable = false)
	private String key;
	
	@NotNull
	@Column(name = "nr_proximo_numero", nullable = false)
	private Long proximoNumero;
	
	@Version
	@Column(name = "nr_version", nullable = false)
	private Long version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getProximoNumero() {
		return proximoNumero;
	}

	public void setProximoNumero(Long proximoNumero) {
		this.proximoNumero = proximoNumero;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
}