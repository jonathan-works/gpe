package br.com.infox.epp.processo.documento.assinatura;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.reflections.Reflections;

import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.seam.util.ComponentUtil;

@Name(AssinaturaDocumentoListenerService.NAME)
@Scope(ScopeType.APPLICATION)
@AutoCreate
@Startup
public class AssinaturaDocumentoListenerService {
	public static final String NAME = "assinaturaDocumentoListenerService";
	private static final LogProvider LOG = Logging.getLogProvider(AssinaturaDocumentoListenerService.class);
	
	private List<String> listeners = new ArrayList<>();
	
	@Create
	public void init() {
		Reflections reflections = new Reflections("br.com.infox");
		Class<AssinaturaDocumentoListener> assinaturaListenerClass = AssinaturaDocumentoListener.class;
		Set<Class<? extends AssinaturaDocumentoListener>> listenerClasses = reflections.getSubTypesOf(assinaturaListenerClass);
		for (Class<? extends AssinaturaDocumentoListener> listenerClass : listenerClasses) {
			Name name = listenerClass.getAnnotation(Name.class);
			Scope scope = listenerClass.getAnnotation(Scope.class);
			if (name != null && scope != null && (scope.value() == ScopeType.EVENT || scope.value() == ScopeType.STATELESS)) {
				listeners.add(name.value());
			} else {
				StringBuilder msg = new StringBuilder("A classe ");
				msg.append(listenerClass.getCanonicalName());
				msg.append(" implementa ");
				msg.append(assinaturaListenerClass.getCanonicalName());
				msg.append(" mas apresenta os seguintes problemas: ");
				if (name == null) {
					msg.append("Não é componente Seam; ");
				}
				if (scope == null) {
					msg.append("Não possui escopo; ");
				}
				if (scope.value() != ScopeType.EVENT && scope.value() != ScopeType.STATELESS) {
					msg.append("Possui escopo diferente de EVENT e STATELESS");
				}
				LOG.warn(msg.toString());
			}
		}
	}
	
	public void dispatch(Documento documento) {
		for (String componentName : listeners) {
			AssinaturaDocumentoListener listener = ComponentUtil.getComponent(componentName);
			LOG.info("Invocando postSignDocument para o componente " + componentName);
			listener.postSignDocument(documento);
		}
	}
}
