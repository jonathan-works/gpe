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
package br.com.infox.ibpm.jbpm;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.TaskInstance;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import br.com.infox.util.constants.WarningConstants;
import br.com.itx.exception.ApplicationException;


@Name("subprocessActionHandler")
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
public class SubprocessActionHandler {

	@SuppressWarnings(WarningConstants.UNCHECKED)
	@Observer(Event.EVENTTYPE_SUBPROCESS_CREATED)
	public void copyVariablesToSubprocess() {
		try {
			Token token = TaskInstance.instance().getToken();
			ProcessInstance subProcessInstance = token.getSubProcessInstance();
			Map<String, Object> variables = TaskInstance.instance().getVariables();
			subProcessInstance.getContextInstance().addVariables(variables);
		} catch (Exception ex) {
			throw new ApplicationException(ApplicationException.
					createMessage("copiar variaveis para o subprocesso", 
								  "copyVariablesToSubprocess()", 
								  "SubprocessoActionHandler", 
								  "BPM"));
		}
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	@Observer(Event.EVENTTYPE_SUBPROCESS_END)
	public void copyVariablesFromSubprocess() {
		try {
			Token token = TaskInstance.instance().getToken();
			ProcessInstance subProcessInstance = token.getProcessInstance();
			Map<String, Object> variables = subProcessInstance.getContextInstance().getVariables();
			org.jboss.seam.bpm.ProcessInstance.instance().getContextInstance().addVariables(variables);
		} catch (Exception ex) {
			throw new ApplicationException(ApplicationException.
					createMessage("copiar as variaveis do subprocesso", 
								  "copyVariablesFromSubprocess()", 
								  "SubprocessoActionHandler", 
								  "BPM"));
		}
	}

}