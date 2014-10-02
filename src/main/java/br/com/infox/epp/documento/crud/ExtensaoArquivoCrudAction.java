package br.com.infox.epp.documento.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ExtensaoArquivoManager;

@Name(ExtensaoArquivoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ExtensaoArquivoCrudAction extends AbstractCrudAction<ExtensaoArquivo, ExtensaoArquivoManager>{

    private static final long serialVersionUID = 1L;
    public static final String NAME = "extensaoArquivoCrudAction";
    
    private ClassificacaoDocumento tipoProcessoDocumento;

    public void setTipoProcessoDocumento(ClassificacaoDocumento tipoProcessoDocumento) {
        this.tipoProcessoDocumento = tipoProcessoDocumento;
    }
    
    @Override
    protected boolean isInstanceValid() {
        getInstance().setTipoProcessoDocumento(tipoProcessoDocumento);
        return super.isInstanceValid();
    }

    @Override
    protected void afterSave(String ret) {
        newInstance();
        super.afterSave(ret);
    }
    
}
