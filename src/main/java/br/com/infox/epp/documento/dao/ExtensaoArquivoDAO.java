package br.com.infox.epp.documento.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;

@Name(ExtensaoArquivoDAO.NAME)
@Scope(ScopeType.EVENT)
public class ExtensaoArquivoDAO extends DAO<ExtensaoArquivo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "extensaoArquivoDAO";
    
}
