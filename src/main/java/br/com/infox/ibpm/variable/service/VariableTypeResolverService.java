package br.com.infox.ibpm.variable.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.ibpm.variable.entity.VariableInfo;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class VariableTypeResolverService {
	
	public Map<String, VariableInfo> buildVariableInfoMap(Long processDefinitionId) {
		Map<String, VariableInfo> variableMap = new HashMap<>();
		String hql = "select distinct va.mappedName from org.jbpm.taskmgmt.def.Task t "
				+ " inner join t.taskController tc "
				+ " inner join tc.variableAccesses va "
				+ " where t.processDefinition.id = :processDefinitionId";
		List<String> mappedNames = BeanManager.INSTANCE.getReference(EntityManager.class).createQuery(hql, String.class).setParameter("processDefinitionId", processDefinitionId).getResultList();
		for (String mappedName : mappedNames) {
			variableMap.put(mappedName.split(":")[1], new VariableInfo(mappedName));
		}
		return variableMap;
	}
}
