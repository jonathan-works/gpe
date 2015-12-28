package br.com.infox.epp.layout.entity;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.infox.core.persistence.ORConstants;

@Entity
@Table(name = ResourceSkin.TABLE_NAME)
public class ResourceSkin {
	
	public static final String TABLE_NAME = "tb_resource_skin";
	
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = ORConstants.GENERATOR, sequenceName = "sq_resource_skin")
	@GeneratedValue(generator = ORConstants.GENERATOR, strategy = GenerationType.SEQUENCE)
	@Column(name = "id_resource_skin", nullable = false, unique = true)
	private Integer id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_skin")
	private Skin skin;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_resource")
	private Resource resource;
	
	@NotNull
	@Column(name = "dt_modificacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModificacao;

	public Skin getSkin() {
		return skin;
	}

	public void setSkin(Skin skin) {
		this.skin = skin;
	}

	public Resource getResource() {
		return resource;
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}	

	public Date getDataModificacao() {
		return dataModificacao;
	}

	public void setDataModificacao(Date dataModificacao) {
		this.dataModificacao = dataModificacao;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((skin == null) ? 0 : skin.hashCode());
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
		ResourceSkin other = (ResourceSkin) obj;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (skin == null) {
			if (other.skin != null)
				return false;
		} else if (!skin.equals(other.skin))
			return false;
		return true;
	}	
}
