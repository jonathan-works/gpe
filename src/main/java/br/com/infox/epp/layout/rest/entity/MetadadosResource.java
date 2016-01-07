package br.com.infox.epp.layout.rest.entity;

import java.util.Date;

import javax.ws.rs.core.EntityTag;

import br.com.infox.epp.layout.entity.Resource;

public class MetadadosResource {
	
	private Date lastModified;
	private EntityTag etag;
	
	public MetadadosResource(Resource resource) {
		this.etag = new EntityTag(Long.toString(resource.getDataModificacao().getTime()));
		this.lastModified = resource.getDataModificacao();
	}

	public Date getLastModified() {
		return lastModified;
	}

	public EntityTag getEtag() {
		return etag;
	}
}
