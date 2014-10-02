package br.com.infox.epp.documento.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.documento.dao.ClassificacaoDocumentoDAO;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.Documento;

@Name(ClassificacaoDocumentoManager.NAME)
@AutoCreate
public class ClassificacaoDocumentoManager extends Manager<ClassificacaoDocumentoDAO, TipoProcessoDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "classificacaoDocumentoManager";

    public List<Documento> getProcessoDocumentoByTask(TaskInstance task) {
        return getDao().getProcessoDocumentoByTask(task);
    }

}
