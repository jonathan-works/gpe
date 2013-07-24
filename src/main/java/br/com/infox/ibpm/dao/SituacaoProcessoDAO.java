package br.com.infox.ibpm.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.itx.util.EntityUtil;

@Name(SituacaoProcessoDAO.NAME)
@AutoCreate
public class SituacaoProcessoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "situacaoProcessoDAO";
	
	public Long getQuantidadeTarefasAtivasByTaskId(long taskId){
		String hql = "select count(o.idTaskInstance) from SituacaoProcesso o where o.idTaskInstance = :ti";
		return (Long) entityManager.createQuery(hql).setParameter("ti", taskId).getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getProcessosAbertosByIdTarefa(Integer idTarefa, Map<String, Object> selected){
		StringBuilder sb = new StringBuilder();
		sb.append("select s.idProcesso from SituacaoProcesso s ");
		sb.append("where s.idTarefa = :idTarefa ");
		sb.append(getTreeTypeRestriction(selected));
		sb.append("group by s.idProcesso");
		Query query = EntityUtil.getEntityManager().createQuery(sb.toString());
		return (List<Integer>) query.setParameter("idTarefa", idTarefa).getResultList();
	}
	
	private String getTreeTypeRestriction(Map<String, Object> selected) {
		String treeType = (String) selected.get("tree");
		String nodeType = (String) selected.get("type");
		if ("caixa".equals(treeType) && "Task".equals(nodeType)) {
			return "and s.idCaixa is null ";
		}
		if (treeType == null && "Caixa".equals(nodeType)) {
			return "and s.idCaixa is not null ";
		}
		return "";
	}

}
