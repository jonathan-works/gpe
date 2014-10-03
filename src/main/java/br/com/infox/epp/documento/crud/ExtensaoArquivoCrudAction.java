package br.com.infox.epp.documento.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ExtensaoArquivoManager;

@Scope(ScopeType.CONVERSATION)
@Name(ExtensaoArquivoCrudAction.NAME)
public class ExtensaoArquivoCrudAction extends AbstractCrudAction<ExtensaoArquivo, ExtensaoArquivoManager>{

    private static final long serialVersionUID = 1L;
    public static final String NAME = "extensaoArquivoCrudAction";
    
    private ClassificacaoDocumento classificacaoDocumento;

    public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
        this.classificacaoDocumento = classificacaoDocumento;
    }
    
    @Override
    protected boolean isInstanceValid() {
        getInstance().setClassificacaoDocumento(classificacaoDocumento);
        return super.isInstanceValid();
    }

    @Override
    protected void afterSave(String ret) {
        newInstance();
        super.afterSave(ret);
    }
    
}
