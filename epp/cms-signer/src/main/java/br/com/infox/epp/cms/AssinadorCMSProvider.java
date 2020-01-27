package br.com.infox.epp.cms;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import br.com.infox.assinatura.assinador.Assinador;
import br.com.infox.assinatura.assinador.impl.AssinadorFactory;

public class AssinadorCMSProvider {
	
	@Produces
	@Default
	@Dependent
	public Assinador produceAssinador() {
		return AssinadorFactory.getDefault();
	}

}
