/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.component;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.dom4j.Element;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.core.Init;
import org.jboss.seam.deployment.FileDescriptor;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.XML;

import br.com.infox.seam.deploy.ComponentDeploymentHandler;
import br.com.infox.seam.deploy.CustomInitialization;
import br.com.itx.component.Util;
/**
 * Classe que carrega os componentes xml (grid, form, etc.) que est�o no diret�rio META-INF
 * 
 * @author luizruiz
 *
 */

@Name("componentStartup")
@Scope(ScopeType.APPLICATION)
@Startup
@Install(precedence=FRAMEWORK)
public class ComponentStartup {

	private ComponentDeploymentHandler hotDeploymentHandler;
	private CustomInitialization initialization = new CustomInitialization();
	
	@Create 
	public void create() {
		if (!Init.instance().isDebug()) {
			return;
		}
		hotDeploymentHandler = new Util().eval(
				"#{hotDeploymentStrategy.deploymentHandlers['componentDeploymentHandler']}");
		if (hotDeploymentHandler != null) {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(hotDeploymentHandler.getClassLoader());
			Set<FileDescriptor> hotResources = hotDeploymentHandler.getResources();
			List<FileDescriptor> resources = new ArrayList<FileDescriptor>(hotResources);
			Collections.sort(resources, new Comparator<FileDescriptor>(
					) {
						public int compare(FileDescriptor o1, FileDescriptor o2) {
							String name1 = o1.getName();
							String name2 = o2.getName();
							name1 = name1.substring(name1.lastIndexOf('/'));
							name2 = name2.substring(name2.lastIndexOf('/'));
							return name1.compareTo(name2);
						}
			});
			reloadComponents(resources);
			Thread.currentThread().setContextClassLoader(cl);
		}
	}

	public void reloadComponents(List<FileDescriptor> resources) {
		if (resources != null) {
			for (FileDescriptor fd : resources) {
				installComponent(fd);
			}
		}
	}

	private void installComponent(FileDescriptor fileDescriptor) {
		InputStream stream = null;
		try {
			stream = fileDescriptor.getUrl().openStream();
		} catch (IOException e) {
		}
		if (stream != null) {
			try {
				Properties replacements = new Properties();
				Element rootElement = XML.getRootElement(stream);
				initialization.installComponentsFromXmlElements(rootElement, replacements);
			} catch (Exception e) {
				throw new RuntimeException("error while reading "
						+ fileDescriptor.getName(), e);
			} finally {
				Resources.closeStream(stream);
			}
		}
	}

}