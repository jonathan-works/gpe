package br.com.infox.ibpm.node.dao;

import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.SQLQuery;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(JbpmNodeDAO.NAME)
@AutoCreate
public class JbpmNodeDAO extends DAO<Void> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "jbpmNodeDAO";

    public void atualizarNodesModificados(Map<Number, String> modifiedNodes) {
        if (modifiedNodes.size() > 0) {
            String update = "update jbpm_node set name_ = :nodeName where id_ = :nodeId";
            SQLQuery q = JbpmUtil.getJbpmSession().createSQLQuery(update);
            for (Entry<Number, String> e : modifiedNodes.entrySet()) {
                q.setParameter("nodeName", e.getValue());
                q.setParameter("nodeId", e.getKey());
                q.executeUpdate();
            }
        }
        JbpmUtil.getJbpmSession().flush();
    }

    public Number findNodeIdByIdProcessDefinitionAndName(
            Number idProcessDefinition, String taskName) {
        String hql = "select max(id_) from jbpm_node where processdefinition_ = :idProcessDefinition and name_ = :nodeName";
        return (Number) JbpmUtil.getJbpmSession().createSQLQuery(hql).setParameter("idProcessDefinition", idProcessDefinition).setParameter("nodeName", taskName).uniqueResult();
    }

}
