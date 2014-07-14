package br.com.infox.epp.documento.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.documento.dao.ExtensaoArquivoDAO;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;

@Name(ExtensaoArquivoManager.NAME)
@Scope(ScopeType.EVENT)
public class ExtensaoArquivoManager extends Manager<ExtensaoArquivoDAO, ExtensaoArquivo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "extensaoArquivoManager";
    
}
