package br.com.infox.epp.processo.documento.assinatura;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.processo.documento.entity.Documento;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AssinaturaDocumentoListenerService {
	
	private static final LogProvider LOG = Logging.getLogProvider(AssinaturaDocumentoListenerService.class);
	
	@Any
	@Inject
	private Instance<AssinaturaDocumentoListener> listeners;
	
	public void dispatch(Documento documento) {
		for (AssinaturaDocumentoListener listener : listeners) {
			LOG.info("Invocando postSignDocument para o componente " + listener.getClass().getName());
			listener.postSignDocument(documento);
		}
	}
}
