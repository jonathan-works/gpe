package br.com.infox.ibpm.node.dao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.SQLQuery;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.core.dao.DAO;
import br.com.infox.ibpm.util.JbpmUtil;

@Stateless
@AutoCreate
@Name(JbpmNodeDAO.NAME)
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

    public Number findNodeIdByIdProcessDefinitionAndName(Number idProcessDefinition, String taskName) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Number> cq = cb.createQuery(Number.class);
        Root<Node> node = cq.from(Node.class);
        cq.select(cb.max(node.<Long>get("id")));
        cq.where(
                cb.equal(node.<ProcessDefinition>get("processDefinition").<Long>get("id"), cb.literal(idProcessDefinition)),
                cb.equal(node.<String>get("name"), cb.literal(taskName))
        );
        List<Number> result = getEntityManager().createQuery(cq).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

}
