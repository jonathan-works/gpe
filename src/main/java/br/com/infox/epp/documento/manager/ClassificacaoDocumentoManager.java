package br.com.infox.epp.documento.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.documento.dao.ClassificacaoDocumentoDAO;
import br.com.infox.ibpm.entity.ProcessoDocumento;

@Name(ClassificacaoDocumentoManager.NAME)
@AutoCreate
public class ClassificacaoDocumentoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "classificacaoDocumentoManager";
	
	@In private ClassificacaoDocumentoDAO classificacaoDocumentoDAO;
	
	public List<ProcessoDocumento> getProcessoDocumentoByTask(TaskInstance task){
		return classificacaoDocumentoDAO.getProcessoDocumentoByTask(task);
	}

}
