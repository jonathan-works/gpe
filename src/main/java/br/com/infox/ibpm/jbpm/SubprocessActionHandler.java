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

import br.com.itx.exception.AplicationException;


@Name("subprocessActionHandler")
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
public class SubprocessActionHandler {

	@SuppressWarnings("unchecked")
	@Observer(Event.EVENTTYPE_SUBPROCESS_CREATED)
	public void copyVariablesToSubprocess() {
		try {
			Token token = TaskInstance.instance().getToken();
			ProcessInstance subProcessInstance = token.getSubProcessInstance();
			Map<String, Object> variables = TaskInstance.instance().getVariables();
			subProcessInstance.getContextInstance().addVariables(variables);
		} catch (Exception ex) {
			throw new AplicationException(AplicationException.
					createMessage("copiar variaveis para o subprocesso", 
								  "copyVariablesToSubprocess()", 
								  "SubprocessoActionHandler", 
								  "BPM"));
		}
	}
	
	@SuppressWarnings("unchecked")
	@Observer(Event.EVENTTYPE_SUBPROCESS_END)
	public void copyVariablesFromSubprocess() {
		try {
			Token token = TaskInstance.instance().getToken();
			ProcessInstance subProcessInstance = token.getProcessInstance();
			Map<String, Object> variables = subProcessInstance.getContextInstance().getVariables();
			org.jboss.seam.bpm.ProcessInstance.instance().getContextInstance().addVariables(variables);
		} catch (Exception ex) {
			throw new AplicationException(AplicationException.
					createMessage("copiar as variaveis do subprocesso", 
								  "copyVariablesFromSubprocess()", 
								  "SubprocessoActionHandler", 
								  "BPM"));
		}
	}

}