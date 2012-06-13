/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.seam.deploy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.dom4j.Element;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.Init;
import org.jboss.seam.init.ComponentDescriptor;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.util.Conversions;
import org.jboss.seam.util.Reflections;

/**
 * Wrap da classe Initialization do Seam, adaptada para atender os componentes do META-INF
 * @author luizruiz
 *
 */
@SuppressWarnings("unchecked")
public class CustomInitialization extends Initialization {
	
	private Set<ComponentDescriptor> installedComponents = new HashSet<ComponentDescriptor>();

	public CustomInitialization() {
		super(ServletLifecycle.getServletContext());
	}
		
	public void installComponentsFromXmlElements(Element rootElement, Properties replacements)
			throws ClassNotFoundException {
		String methodName = "installComponentsFromXmlElements";
		try {
			Method method = Initialization.class.getDeclaredMethod(
					methodName , Element.class, Properties.class);
			method.setAccessible(true);
			method.invoke(this, rootElement, replacements);
			addComponents();
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Erro na carga de componentes: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, Set<ComponentDescriptor>> getComponentDescriptors() {
		Field field = Reflections.getField(Initialization.class, "componentDescriptors");
		try {
			return (Map<String, Set<ComponentDescriptor>>) Reflections.get(field, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, Conversions.PropertyValue> getProperties() {
		Field field = Reflections.getField(Initialization.class, "properties");
		try {
			return (Map<String, Conversions.PropertyValue>) Reflections.get(field, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void addComponents() {
		for (Entry<String, Set<ComponentDescriptor>> e : getComponentDescriptors().entrySet()) {
			for (ComponentDescriptor cd : e.getValue()) {
				if (installedComponents.add(cd)) {
					addComponent(cd);
				}
			}
		}
	}
	
	private void addComponent(ComponentDescriptor descriptor) {
		Map<String, Conversions.PropertyValue> properties = 
			(Map<String, Conversions.PropertyValue>) 
			ServletLifecycle.getServletContext().getAttribute(Component.PROPERTIES);
		properties.putAll(getProperties());

		String name = descriptor.getName();
		String componentName = name + Initialization.COMPONENT_SUFFIX;
		try {
			Component component = new Component(descriptor.getComponentClass(),
					name, descriptor.getScope(), descriptor.isStartup(),
					descriptor.getStartupDependencies(), descriptor
							.getJndiName());
			ServletLifecycle.getServletContext().setAttribute(componentName, component);
			
			Init init = (Init) ServletLifecycle.getServletContext().getAttribute(Seam.getComponentName(Init.class));
			init.addHotDeployableComponent(component.getName());
		} catch (Exception e) {
			throw new RuntimeException("Could not create Component: " + name, e);
		}
	}


}