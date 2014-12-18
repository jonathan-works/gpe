package br.com.infox.epp.processo.metadado.system;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.reflections.Reflections;

import br.com.infox.core.messages.Messages;
import br.com.infox.epp.system.EppMessagesContextLoader;

@Name(MetadadoLabelLoader.NAME)
@AutoCreate
@Scope(ScopeType.APPLICATION)
@Startup(depends = {EppMessagesContextLoader.NAME})
public class MetadadoLabelLoader implements Serializable {
	public static final String NAME = "metadadoLabelLoader";
	public static final String METADADO_MESSAGES = "metadadoMessages";
	private static final long serialVersionUID = 1L;

	@Create
	public void init() throws InstantiationException, IllegalAccessException {
		Map<String, MetadadoProcessoDefinition> mainMap = new HashMap<>();
		Reflections reflections = new Reflections("br.com.infox");
		Set<Class<? extends MetadadoProcessoProvider>> providers = reflections.getSubTypesOf(MetadadoProcessoProvider.class);
		for (Class<? extends MetadadoProcessoProvider> providerClass : providers) {
			MetadadoProcessoProvider provider = providerClass.newInstance();
			Map<String, MetadadoProcessoDefinition> providerMap = provider.getDefinicoesMetadados();
			for (String key : providerMap.keySet()) {
				if (!mainMap.containsKey(key)) {
					mainMap.put(key, providerMap.get(key));
				} else {
					MetadadoProcessoDefinition labelExistente = mainMap.get(key);
					MetadadoProcessoDefinition labelCandidata = providerMap.get(key);
					if (labelCandidata.getPrioridade() > labelExistente.getPrioridade()) {
						mainMap.put(key, labelCandidata);
					}
				}
			}
		}
		
		Map<String, String> messages = new HashMap<>();
		for (String key : mainMap.keySet()) {
			messages.put(key, Messages.resolveMessage(mainMap.get(key).getLabel()));
		}
		
		Contexts.getApplicationContext().set(METADADO_MESSAGES, messages);
	}
}
