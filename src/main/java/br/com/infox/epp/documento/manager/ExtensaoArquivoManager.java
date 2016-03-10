package br.com.infox.epp.documento.manager;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.documento.dao.ExtensaoArquivoDAO;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;

@AutoCreate
@Stateless
@Name(ExtensaoArquivoManager.NAME)
public class ExtensaoArquivoManager extends Manager<ExtensaoArquivoDAO, ExtensaoArquivo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "extensaoArquivoManager";
    
    public ExtensaoArquivo getTamanhoMaximo(ClassificacaoDocumento classificacao, String extensaoArquivo) {
        return getDao().getTamanhoMaximo(classificacao, extensaoArquivo);
    }
    
}
