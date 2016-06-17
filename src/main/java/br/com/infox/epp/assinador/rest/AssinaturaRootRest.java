package br.com.infox.epp.assinador.rest;

import javax.ws.rs.Path;

import br.com.infox.epp.cdi.config.BeanManager;

/**
 * Classe implementada por não conseguir colocar A anotação {@link Path} em {@link AssinaturaRest}
 * @author paulo
 *
 */
@Path("/assinatura")
public interface AssinaturaRootRest {
	
	public static class AssinaturaRootRestImpl implements AssinaturaRootRest {
		@Override
		public AssinaturaRest getAssinaturaRest() {
			AssinaturaRestImpl assinaturaRestImpl = BeanManager.INSTANCE.getReference(AssinaturaRestImpl.class);
			return assinaturaRestImpl;
		}
		
	}
	
	@Path("/")
	public AssinaturaRest getAssinaturaRest();

}
