package br.com.infox.epp.documento.dao;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.Documento;

@Name(ClassificacaoDocumentoDAO.NAME)
@AutoCreate
public class ClassificacaoDocumentoDAO extends DAO<TipoProcessoDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "classificacaoDocumentoDAO";

    public List<Documento> getProcessoDocumentoByTask(TaskInstance task) {
        return (List<Documento>) getEntityManager().createQuery("select o from ProcessoDocumento o where idJbpmTask = :id", Documento.class).setParameter("id", task.getId()).getResultList();
    }

}
