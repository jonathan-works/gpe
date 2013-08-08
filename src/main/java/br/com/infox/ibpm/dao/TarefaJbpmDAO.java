package br.com.infox.ibpm.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(TarefaJbpmDAO.NAME)
@AutoCreate
public class TarefaJbpmDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tarefaJbpmDAO";
	
	/**
	 * Insere para cada tarefa na tabela de tb_tarefa todos os ids que esse já possuiu.
	 */
	public void inserirVersoesTarefas() {
		String hql = "insert into public.tb_tarefa_jbpm (id_tarefa, id_jbpm_task) " +
						"select t.id_tarefa, jt.id_ from public.tb_tarefa t " +
							"inner join public.tb_fluxo f using (id_fluxo) " +
							"inner join jbpm_task jt on jt.name_ = t.ds_tarefa " +
							"inner join jbpm_processdefinition pd on pd.id_ = jt.processdefinition_ " +
						"where f.ds_fluxo = pd.name_ and not exists " +
							"(select 1 from public.tb_tarefa_jbpm tj " +
								"where tj.id_tarefa = t.id_tarefa and tj.id_jbpm_task = jt.id_)";
		entityManager.createNativeQuery(hql).executeUpdate();
	}

}
