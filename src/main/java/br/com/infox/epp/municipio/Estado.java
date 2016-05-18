package br.com.infox.epp.municipio;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;

@Entity
@Table(name = "tb_estado")
public class Estado implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "EstadoGenerator", sequenceName = "sq_estado")
	@GeneratedValue(generator = "EstadoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_estado", unique = true, nullable = false)
	private Integer id;
	
	@NotNull
	@Size(min = 1, max = 2)
	@Column(name = "cd_estado", nullable = false, length = 2)
	private String codigo;
	
	@NotNull
	@Size(min = 1, max = 30)
	@Column(name = "ds_estado", nullable = false, length = LengthConstants.DESCRICAO_PEQUENA)
	private String nome;
	
	@NotNull
	@Size(min = 1, max = 2)
	@Column(name = "cd_estado_ibge", nullable = false, length = 2)
	private String codigoIBGE;
	
	@Column(name = "in_ativo")
	private Boolean ativo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCodigoIBGE() {
		return codigoIBGE;
	}

	public void setCodigoIBGE(String codigoIBGE) {
		this.codigoIBGE = codigoIBGE;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}
