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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tb_def_var_proc_recurso")
public class DefinicaoVariavelProcessoRecurso implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "DefinicaoVariavelProcessoRecursoGenerator", sequenceName = "sq_def_var_proc_recurso", allocationSize = 1, initialValue = 1)
	@GeneratedValue(generator = "DefinicaoVariavelProcessoRecursoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_def_var_proc_recurso")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@NotNull
	@JoinColumn(name = "id_definicao_variavel_processo", nullable = false)
	private DefinicaoVariavelProcesso definicaoVariavelProcesso;
	
	@NotNull
	@Column(name = "cd_recurso", nullable = false)
	private String recurso;
	
	@NotNull
	@Column(name = "nr_ordem", nullable = false)
	private Integer ordem;

	@NotNull
	@Column(name = "in_visivel_usuario_externo", nullable = false)
	private Boolean visivelUsuarioExterno = false;
	
	@NotNull
	@Version
	@Column(name = "nr_version", nullable = false)
	private Long version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DefinicaoVariavelProcesso getDefinicaoVariavelProcesso() {
		return definicaoVariavelProcesso;
	}

	public void setDefinicaoVariavelProcesso(DefinicaoVariavelProcesso definicaoVariavelProcesso) {
		this.definicaoVariavelProcesso = definicaoVariavelProcesso;
	}

	public String getRecurso() {
		return recurso;
	}
	
	public void setRecurso(String recurso) {
		this.recurso = recurso;
	}
	
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}
	
	public Boolean getVisivelUsuarioExterno() {
		return visivelUsuarioExterno;
	}
	
	public void setVisivelUsuarioExterno(Boolean visivelUsuarioExterno) {
		this.visivelUsuarioExterno = visivelUsuarioExterno;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DefinicaoVariavelProcessoRecurso))
			return false;
		DefinicaoVariavelProcessoRecurso other = (DefinicaoVariavelProcessoRecurso) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}
