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
package br.com.infox.ibpm.application;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.Jbpm;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.job.executor.JobExecutor;


/**
 * Componente responsavel por inicializar o serviço de Job
 * utilizado pelos componentes Timer do jBPM no projeto
 * 
 * @author luizruiz
 *
 */

@Name(JobExecutorLauncher.NAME)
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install(dependencies="org.jboss.seam.bpm.jbpm", precedence=BUILT_IN)
@Startup(depends="org.jboss.seam.bpm.jbpm")
public class JobExecutorLauncher {
    
    static final String NAME = "JobExecutorLauncher";
    private static final LogProvider LOG = Logging.getLogProvider(JobExecutorLauncher.class);

	@Create
	public void init() {
		try {
			JobExecutor jobExecutor = Jbpm.instance().getJbpmConfiguration().getJobExecutor();
			jobExecutor.start();
		} catch (RuntimeException e) {
		    LOG.error(".init()", e);
		}
	}
	
}