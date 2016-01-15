package br.com.infox.epp.layout.entity;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import edu.emory.mathcs.backport.java.util.Collections;

@Entity
@Table(name = ResourceBin.TABLE_NAME)
@Cacheable
public class ResourceBin {
	
	public static final String TABLE_NAME = "tb_resource_bin";
	
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "ResourceBinGenerator", sequenceName = "sq_resource_bin")
	@GeneratedValue(generator = "ResourceBinGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_resource_bin", nullable = false, unique = true)
	private Long id;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="id_resource")
	private Resource resource;
	
	@NotNull
	@Column(name = "dt_modificacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModificacao;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="tb_resource_bin_skin", joinColumns={@JoinColumn(name="id_resource_bin")}, inverseJoinColumns={@JoinColumn(name="id_skin")})
	private Set<Skin> skins = new TreeSet<>();
	
	@NotNull
	@Column(name="id_binario")
	private Integer idBinario;
	
	@NotNull
	@Column(name="tp_resource_bin")
	@Enumerated(EnumType.STRING)
	private TipoArquivo tipo;
	
	public enum TipoArquivo {
		JPG, PNG, GIF, SVG, SVGZ;
		
		public String getExtensao() {
			return this.name().toLowerCase();
		}
	}
	
	public Long getId() {
		return id;
	}
	
	public void add(Skin skin) {
		skins.add(skin);
		//resourceSkin.setResource(this);
	}
	
	public void remove(Skin skin) {
		skins.remove(skin);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Skin> getResourcesSkins() {
		return Collections.unmodifiableSet(skins);
	}
	
	
	public Date getDataModificacao() {
		return dataModificacao;
	}

	public void setDataModificacao(Date dataModificacao) {
		this.dataModificacao = dataModificacao;
	}

	public Integer getIdBinario() {
		return idBinario;
	}

	public void setIdBinario(Integer idBinario) {
		this.idBinario = idBinario;
	}

	public TipoArquivo getTipo() {
		return tipo;
	}

	public void setTipo(TipoArquivo tipo) {
		this.tipo = tipo;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
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
		if (!(obj instanceof ResourceBin))
			return false;
		ResourceBin other = (ResourceBin) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}	
}
