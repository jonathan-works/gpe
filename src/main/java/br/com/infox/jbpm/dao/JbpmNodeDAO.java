package br.com.infox.jbpm.dao;

import java.math.BigInteger;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.jbpm.JbpmUtil;

@Name(JbpmNodeDAO.NAME)
@AutoCreate
public class JbpmNodeDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "jbpmNodeDAO";
	
	public BigInteger findNodeIdByIdProcessDefinitionAndName(BigInteger idProcessDefinition, String taskName){
		String hql = "select max(id_) from jbpm_node where processdefinition_ = :idProcessDefinition and name_ = :nodeName";
		return (BigInteger) JbpmUtil.getJbpmSession().createSQLQuery(hql)
									.setParameter("idProcessDefinition",idProcessDefinition)
									.setParameter("nodeName", taskName).uniqueResult();
	}

}
