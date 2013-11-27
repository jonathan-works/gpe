package br.com.infox.epp.processo.dao;

import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.itx.util.EntityUtil;
import br.com.itx.util.HibernateUtil;

@Name(ProcessoDAO.NAME)
@AutoCreate
public class ProcessoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoDAO";
	
	public void anulaActorId(String actorId) {
		String query = "update public.tb_processo set nm_actor_id = null where nm_actor_id = :actorId";
		HibernateUtil.getSession().createSQLQuery(query).setParameter("actorId", actorId).executeUpdate();
	}
	
	public void apagarActorIdDoProcesso(Processo processo){
		String hql = "update public.tb_processo set nm_actor_id = null where id_processo = :id";
        getEntityManager().createNativeQuery(hql)
                    .setParameter("id", processo.getIdProcesso())
                    .executeUpdate();
	}
	
	public void anularTodosActorId() {
		String query = "update public.tb_processo set nm_actor_id = null ";
		HibernateUtil.getSession().createSQLQuery(query).executeUpdate();
	}
	
	public void moverProcessosParaCaixa(List<Integer> idList, Caixa caixa){
		String hql = "update Processo set caixa = :caixa where idProcesso in (:idList)";
		EntityUtil.getEntityManager().createQuery(hql)
				.setParameter("caixa", caixa)
				.setParameter("idList", idList)
				.executeUpdate();
		EntityUtil.getEntityManager().flush();
	}
	
	public void moverProcessoParaCaixa(Caixa caixa, Processo processo) {
		EntityUtil.flush();
		EntityUtil.getEntityManager().createNativeQuery(
			"update public.tb_processo set id_caixa = :caixa " +
			"where id_processo = :idProcesso")
			.setParameter("caixa", caixa.getIdCaixa())
			.setParameter("idProcesso", processo.getIdProcesso())
			.executeUpdate();
		return;
	}
	
	public void removerProcessoDaCaixaAtual(Processo processo){
		String sql = "update public.tb_processo set id_caixa = null where "
                + "id_processo = :processo";
        getEntityManager().createNativeQuery(sql)
        				.setParameter("processo", processo.getIdProcesso())
        				.executeUpdate();
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
    public List<Processo> findProcessosByIdProcessoAndActorId(int idProcesso, String login){
	    StringBuilder sb = new StringBuilder();
        sb.append("select o from Processo o where ");
        sb.append("o.idProcesso = :id ");
        sb.append("and o.actorId like :login");
        Query q = EntityUtil.createQuery(sb.toString());
        q.setParameter("id", idProcesso);
        q.setParameter("login", login);
        return q.getResultList();
	}
	
	public void atualizarProcessos(){
		String sql = "update jbpm_processinstance pi set processdefinition_ = " +
				"(select max(id_) from jbpm_processdefinition pd " +
				"where name_ = (select name_ from jbpm_processdefinition " +
				"where id_ = pi.processdefinition_));\n" +

				"update jbpm_token t set node_ = "+
				"(select max(n.id_) from jbpm_node n "+
				"inner join jbpm_processdefinition pd on pd.id_ = n.processdefinition_ "+
				"where n.name_ = (select name_ from jbpm_node where id_ = t.node_) "+
				"and pd.name_ = (select procdef.name_ from jbpm_processinstance procinst "+
				"inner join jbpm_processdefinition procdef on procdef.id_ = procinst.processdefinition_ "+
				"where procinst.id_ = t.processinstance_) "+
				"and n.class_ = (select class_ from jbpm_node where id_ = t.node_));\n" +
				
				"update jbpm_taskinstance ti set task_ = "+ 
				"(select max(t.id_) from jbpm_task t "+
				"inner join jbpm_processdefinition pd on pd.id_ = t.processdefinition_ "+
				"where t.name_ = (select name_ from jbpm_task where id_ = ti.task_) and "+
				"pd.name_ = (select procdef.name_ from jbpm_processinstance procinst "+
				"inner join jbpm_processdefinition procdef on procdef.id_ = procinst.processdefinition_ "+
				"where procinst.id_ = ti.procinst_)) "+
				"where end_ is null;\n" +
				
				"update public.tb_processo_localizacao_ibpm pl set id_task_jbpm =" +
				"(select max(id_) from jbpm_task t where exists" +
				"(select * from jbpm_task where name_ = t.name_ " +
				"and id_ = pl.id_task_jbpm))";
		
		JbpmUtil.getJbpmSession().createSQLQuery(sql).executeUpdate();
	}

}
