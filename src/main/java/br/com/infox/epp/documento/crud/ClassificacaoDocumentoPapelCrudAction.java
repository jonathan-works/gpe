package br.com.infox.epp.documento.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;

@Name(ClassificacaoDocumentoPapelCrudAction.NAME)
public class ClassificacaoDocumentoPapelCrudAction extends AbstractCrudAction<ClassificacaoDocumentoPapel, ClassificacaoDocumentoPapelManager> {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "classificacaoDocumentoPapelCrudAction";

    @In
    private PapelManager papelManager;

    public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
        newInstance();
        getInstance().setClassificacaoDocumento(classificacaoDocumento);
    }

    public List<Papel> papelItems() {
        return papelManager.getPapeisNaoAssociadosAClassificacaoDocumento(getInstance().getClassificacaoDocumento());
    }

}
