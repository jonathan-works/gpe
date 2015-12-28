package br.com.infox.epp.layout.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.persistence.ORConstants;
import edu.emory.mathcs.backport.java.util.Collections;

@Entity
@Table(name = Resource.TABLE_NAME)
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
	@Column(name="ob_recurso")
	private byte[] recurso;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="resource", cascade=CascadeType.ALL)
	private Set<ResourceSkin> resourcesSkins = new HashSet<>();
	
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

	public byte[] getRecurso() {
		return recurso;
	}

	public void setRecurso(byte[] recurso) {
		this.recurso = recurso;
	}

	public Integer getId() {
		return id;
	}
	
	public void setTipo(TipoResource tipo) {
		setPath(tipo.getPath());
	}
	
	public void add(ResourceSkin resourceSkin) {
		resourcesSkins.add(resourceSkin);
		resourceSkin.setResource(this);
	}
	
	public void remove(ResourceSkin resourceSkin) {
		resourcesSkins.remove(resourceSkin);
	}
	
	@SuppressWarnings("unchecked")
	public Set<ResourceSkin> getResourcesSkins() {
		return Collections.unmodifiableSet(resourcesSkins);
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
		if (getClass() != obj.getClass())
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
	
}
