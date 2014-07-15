package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.ExtensaoArquivoQuery.CLASSIFICACAO_PARAM;
import static br.com.infox.epp.documento.query.ExtensaoArquivoQuery.EXTENSAO_PARAM;
import static br.com.infox.epp.documento.query.ExtensaoArquivoQuery.LIMITE_EXTENSAO;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;

@Name(ExtensaoArquivoDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ExtensaoArquivoDAO extends DAO<ExtensaoArquivo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "extensaoArquivoDAO";
    
    public ExtensaoArquivo getTamanhoMaximo(TipoProcessoDocumento classificacao, String extensaoArquivo) {
        Map<String, Object> params = new HashMap<>();
        params.put(CLASSIFICACAO_PARAM, classificacao);
        params.put(EXTENSAO_PARAM, extensaoArquivo);
        return getNamedSingleResult(LIMITE_EXTENSAO, params);
    }
    
}
