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

import org.jboss.seam.deployment.AbstractDeploymentHandler;
import org.jboss.seam.deployment.DeploymentMetadata;

public class ComponentDeploymentHandler extends AbstractDeploymentHandler {

	private static DeploymentMetadata METADATA = new DeploymentMetadata() {

		public String getFileNameSuffix() {
			return ".component.xml";
		}
	};

	public static final String NAME = "componentDeploymentHandler";

	private ClassLoader classLoader;

	public DeploymentMetadata getMetadata() {
		return METADATA;
	}

	public String getName() {
		return NAME;
	}

	public void postProcess(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}

}