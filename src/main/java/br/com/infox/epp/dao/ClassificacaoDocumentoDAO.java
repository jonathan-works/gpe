package br.com.infox.epp.dao;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.ProcessoDocumento;

@Name(ClassificacaoDocumentoDAO.NAME)
@AutoCreate
public class ClassificacaoDocumentoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "classificacaoDocumentoDAO";
	
	public List<ProcessoDocumento> getProcessoDocumentoByTask(TaskInstance task) {
		return (List<ProcessoDocumento>) entityManager.createQuery(
				"select o from ProcessoDocumento o where idJbpmTask = :id", ProcessoDocumento.class)
				.setParameter("id", task.getId())
				.getResultList();
	}
	
}
