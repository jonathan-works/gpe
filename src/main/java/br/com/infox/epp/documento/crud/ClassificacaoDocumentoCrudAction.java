package br.com.infox.epp.documento.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.manager.TipoProcessoDocumentoManager;

@Name(ClassificacaoDocumentoCrudAction.NAME)
public class ClassificacaoDocumentoCrudAction extends AbstractCrudAction<TipoProcessoDocumento, TipoProcessoDocumentoManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "classificacaoDocumentoCrudAction";

}
