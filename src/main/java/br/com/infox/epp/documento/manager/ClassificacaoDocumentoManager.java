package br.com.infox.epp.documento.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.dao.ClassificacaoDocumentoDAO;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;

@AutoCreate
@Name(ClassificacaoDocumentoManager.NAME)
public class ClassificacaoDocumentoManager extends Manager<ClassificacaoDocumentoDAO, ClassificacaoDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "classificacaoDocumentoManager";
    public static final String CODIGO_CLASSIFICACAO_ACESSO_DIRETO = "acessoDireto";

    public List<ClassificacaoDocumento> getUseableClassificacaoDocumento(
            boolean isModelo, Papel papel) {
        return getDao().getUseableClassificacaoDocumento(isModelo, papel);
    }
    
    public boolean existsClassificaoAcessoDireto() {
        return getDao().findByCodigo(CODIGO_CLASSIFICACAO_ACESSO_DIRETO) != null;
    }
    
    public ClassificacaoDocumento getClassificaoParaAcessoDireto() {
        return getDao().findByCodigo(CODIGO_CLASSIFICACAO_ACESSO_DIRETO);
    }
}
