package br.com.infox.epp.layout.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.core.persistence.ORConstants;

@Entity
@Table(name = Resource.TABLE_NAME)
public class BinarioResource {

	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = ORConstants.GENERATOR, sequenceName = "sq_resource")
	@GeneratedValue(generator = ORConstants.GENERATOR, strategy = GenerationType.SEQUENCE)
	@Column(name = "id_resource", nullable = false, unique = true)
	private Integer id;
	
	@Lob
	@NotNull
	@Column(name="ob_recurso")
	private byte[] resource;

	public byte[] getResource() {
		return resource;
	}

	public void setResource(byte[] resource) {
		this.resource = resource;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
