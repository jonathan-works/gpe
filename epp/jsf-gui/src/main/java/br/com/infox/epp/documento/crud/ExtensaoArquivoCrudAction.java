package br.com.infox.epp.documento.crud;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;
import br.com.infox.epp.documento.manager.ExtensaoArquivoManager;

@Named
@ViewScoped
public class ExtensaoArquivoCrudAction extends AbstractCrudAction<ExtensaoArquivo, ExtensaoArquivoManager>{

    private static final long serialVersionUID = 1L;

    @Inject
    private ExtensaoArquivoManager extensaoArquivoManager;

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

    @Override
    protected ExtensaoArquivoManager getManager() {
        return extensaoArquivoManager;
    }
}
