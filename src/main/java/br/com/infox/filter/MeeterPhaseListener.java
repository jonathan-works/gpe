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
package br.com.infox.filter;

import java.util.Date;

import javax.faces.event.PhaseEvent;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * Classe para medição de tempo das fases do ciclos de vida JSF
 * 
 * Para habilitar remova o comentário dos observer
 * @author luiz
 *
 */
@Name("meeterPhaseListener")
@BypassInterceptors
public class MeeterPhaseListener {
	
	private long time;
	
//	@Observer("org.jboss.seam.beforePhase")
	public void beforePhase(PhaseEvent event) {
		time = new Date().getTime();
		System.out.println("Entrou: " + event.getPhaseId());
	}

//	@Observer("org.jboss.seam.afterPhase")
	public void afterPhase(PhaseEvent event) {
		System.out.println("Saiu: " + event.getPhaseId() + " - " + (new Date().getTime() - time));
		time = 0; 
	}
}