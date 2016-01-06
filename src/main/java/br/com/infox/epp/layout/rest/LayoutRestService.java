package br.com.infox.epp.layout.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.layout.entity.ResourceSkin;
import br.com.infox.epp.layout.manager.LayoutManager;
import br.com.infox.epp.layout.rest.entity.MetadadosResource;

@Stateless
public class LayoutRestService {
	
	@Inject
	private LayoutManager manager;
	
	public MetadadosResource getMetadados(String codigoSkin, String pathRecurso) {
		ResourceSkin resourceSkin = manager.getResourceSkin(codigoSkin, pathRecurso); 
		return new MetadadosResource(resourceSkin);
	}
	
	public byte[] carregarBinario(String codigoSkin, String pathRecurso) {
		ResourceSkin resourceSkin = manager.getResourceSkin(codigoSkin, pathRecurso);
		if(resourceSkin == null) {
			return null;
		}
		return resourceSkin.getResource().getResource();
	}

}
