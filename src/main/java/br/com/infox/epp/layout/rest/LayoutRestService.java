package br.com.infox.epp.layout.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.layout.entity.Resource;
import br.com.infox.epp.layout.manager.LayoutManager;
import br.com.infox.epp.layout.rest.entity.MetadadosResource;

@Stateless
public class LayoutRestService {
	
	@Inject
	private LayoutManager manager;
	
	public MetadadosResource getMetadados(String codigoSkin, String pathRecurso) {
		Resource resource = manager.getResource(codigoSkin, pathRecurso); 
		return new MetadadosResource(resource);
	}
	
	public byte[] carregarBinario(String codigoSkin, String pathRecurso) {
		return manager.carregarBinario(codigoSkin, pathRecurso);
	}

}
