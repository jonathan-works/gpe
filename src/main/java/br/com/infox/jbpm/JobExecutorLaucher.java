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
package br.com.infox.jbpm;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.Jbpm;
import org.jbpm.job.executor.JobExecutor;


/**
 * Componente responsavel por inicializar o servi�o de Job
 * utilizado pelos componentes Timer do jBPM no projeto
 * 
 * @author luizruiz
 *
 */

@Name("JobExecutorLaucher")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install(dependencies="org.jboss.seam.bpm.jbpm", precedence=BUILT_IN)
@Startup(depends="org.jboss.seam.bpm.jbpm")
public class JobExecutorLaucher {

	@Create
	public void init() {
		try {
			JobExecutor jobExecutor = Jbpm.instance().getJbpmConfiguration().getJobExecutor();
			jobExecutor.start();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	
}