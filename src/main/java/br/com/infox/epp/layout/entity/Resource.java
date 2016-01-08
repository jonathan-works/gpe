package br.com.infox.epp.layout.entity;

import javax.persistence.Cacheable;
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
@Table(name="tb_resource")
@Cacheable
public class Resource {
	
	public enum Resources {
		LOGO_LOGIN, LOGO_TOPO;
	}
	
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "ResourceGenerator", sequenceName = "sq_resource")
	@GeneratedValue(generator = "ResourceGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_resource", nullable = false, unique = true)
	private Long id;
	
	@NotNull
	@Column(name="cd_resource")
	@Size(max=LengthConstants.DESCRICAO_PEQUENA)
	private String codigo;
	
	@NotNull
	@Column(name="nm_resource")
	@Size(max=LengthConstants.DESCRICAO_GRANDE)
	private String nome;
	
	@NotNull
	@Column(name="ds_path")
	@Size(max=LengthConstants.DESCRICAO_GRANDE)
	private String path;
		

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getId() {
		return id;
	}
	
	
}
