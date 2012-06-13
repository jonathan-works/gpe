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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.taskmgmt.exe.TaskInstance;


@Name("groupTaskList")
@Scope(ScopeType.EVENT)
@BypassInterceptors
public class GroupTaskList extends ActorTaskList {
	
	
	@SuppressWarnings("unchecked")
	public List<TaskInstance> getTaskList() {
		Actor actor = Actor.instance();
		String actorId = actor.getId();
		if (actorId == null) {
			return Collections.EMPTY_LIST;
		}
		List<String> groupIds = new ArrayList<String>(actor.getGroupActorIds());
		groupIds.add(actorId);
		return ManagedJbpmContext.instance().getGroupTaskList(groupIds);
	}

}