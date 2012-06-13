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