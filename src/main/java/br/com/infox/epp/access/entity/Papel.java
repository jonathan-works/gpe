package br.com.infox.epp.access.entity;

import static br.com.infox.core.constants.LengthConstants.*;
import static br.com.infox.core.persistence.ORConstants.*;
import static br.com.infox.epp.access.query.PapelQuery.*;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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

@Entity
@Table(name = TABLE_PAPEL, schema=PUBLIC, uniqueConstraints = @UniqueConstraint(columnNames = IDENTIFICADOR))
@NamedQueries({
    @NamedQuery(name=PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO, 
            query=PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO_QUERY)
})
public class Papel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idPapel;
	private String nome;
	private String identificador;

	private List<Papel> grupos;

	public Papel() {
	}

	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_PAPEL)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_PAPEL, unique = true, nullable = false)
	public int getIdPapel() {
		return this.idPapel;
	}

	public void setIdPapel(int idPerfil) {
		this.idPapel = idPerfil;
	}

	@Column(name = NOME_PAPEL, length=NOME_PADRAO)
	@Size(max=NOME_PADRAO)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = IDENTIFICADOR, length = DESCRICAO_PADRAO)
	@Size(max=DESCRICAO_PADRAO)
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
	@JoinTable(name = "tb_papel_grupo", schema=PUBLIC, joinColumns = @JoinColumn(name = "id_papel"), inverseJoinColumns = @JoinColumn(name = "membro_do_grupo"))
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