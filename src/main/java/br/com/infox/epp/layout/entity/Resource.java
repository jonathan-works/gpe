package br.com.infox.epp.layout.entity;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.persistence.ORConstants;
import edu.emory.mathcs.backport.java.util.Collections;

@Entity
@Table(name = Resource.TABLE_NAME)
@Cacheable
public class Resource {
	
	public static final String TABLE_NAME = "tb_resource";
	
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = ORConstants.GENERATOR, sequenceName = "sq_resource")
	@GeneratedValue(generator = ORConstants.GENERATOR, strategy = GenerationType.SEQUENCE)
	@Column(name = "id_resource", nullable = false, unique = true)
	private Integer id;
	
	@NotNull
	@Column(name="ds_path")
	@Size(max=LengthConstants.DESCRICAO_GRANDE)
	private String path;
	
	@NotNull
	@Column(name = "dt_modificacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModificacao;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="tb_resource_skin", joinColumns={@JoinColumn(name="id_resource")}, inverseJoinColumns={@JoinColumn(name="id_skin")})
	private Set<Skin> skins = new TreeSet<>();
	
	@NotNull
	@Column(name="id_binario")
	private Integer idBinario;
	
	public enum TipoResource {
		LOGO_LOGIN("imagens/logo_epp_login.png"), LOGO_TOPO("imagens/logo_epp_topo.png");
		
		private String path;
		
		private TipoResource(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getId() {
		return id;
	}
	
	public void setTipo(TipoResource tipo) {
		setPath(tipo.getPath());
	}
	
	public void add(Skin skin) {
		skins.add(skin);
		//resourceSkin.setResource(this);
	}
	
	public void remove(ResourceSkin skin) {
		skins.remove(skin);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Skin> getResourcesSkins() {
		return Collections.unmodifiableSet(skins);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Resource other = (Resource) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
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
	
}
