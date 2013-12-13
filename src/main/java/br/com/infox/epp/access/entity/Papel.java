package br.com.infox.epp.access.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ForeignKey;
import org.jboss.seam.annotations.security.management.RoleGroups;
import org.jboss.seam.annotations.security.management.RoleName;

import br.com.infox.core.constants.LengthConstants;

@Entity
@Table(name = "tb_papel", schema="public", uniqueConstraints = @UniqueConstraint(columnNames = "ds_identificador"))
public class Papel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idPapel;
	private String nome;
	private String identificador;

	private List<Papel> grupos;

	public Papel() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_papel")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_papel", unique = true, nullable = false)
	public int getIdPapel() {
		return this.idPapel;
	}

	public void setIdPapel(int idPerfil) {
		this.idPapel = idPerfil;
	}

	@Column(name = "ds_nome", length=LengthConstants.DESCRICAO_PADRAO)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "ds_identificador", length = LengthConstants.DESCRICAO_PADRAO)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	@NotNull
	@RoleName
	public String getIdentificador() {
		return this.identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	@RoleGroups
	@ManyToMany
	@JoinTable(name = "tb_papel_grupo", schema="public", joinColumns = @JoinColumn(name = "id_papel"), inverseJoinColumns = @JoinColumn(name = "membro_do_grupo"))
	@ForeignKey(name="tb_papel_grupo_papel_fk", inverseName = "tb_papel_grupo_membro_fk" )
	@OrderBy("nome")
	public List<Papel> getGrupos() {
		return this.grupos;
	}

	public void setGrupos(List<Papel> grupos) {
		this.grupos = grupos;
	}

	@Override
	public String toString() {
		if (this.nome == null) {
			return this.identificador;
		}
		return this.nome;
	}

	@Transient
	public boolean getAtivo() {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Papel)) {
			return false;
		}
		Papel other = (Papel) obj;
		if (getIdPapel() != other.getIdPapel()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdPapel();
		return result;
	}
}