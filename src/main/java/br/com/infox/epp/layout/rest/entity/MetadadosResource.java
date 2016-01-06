package br.com.infox.epp.layout.rest.entity;

import java.util.Date;

import javax.ws.rs.core.EntityTag;

import br.com.infox.epp.layout.entity.ResourceSkin;

public class MetadadosResource {
	
	private Date lastModified;
	private EntityTag etag;
	
	public MetadadosResource(ResourceSkin resourceSkin) {
		this.etag = new EntityTag(Long.toString(resourceSkin.getDataModificacao().getTime()));
		this.lastModified = resourceSkin.getDataModificacao();
	}

	public Date getLastModified() {
		return lastModified;
	}

	public EntityTag getEtag() {
		return etag;
	}
}
