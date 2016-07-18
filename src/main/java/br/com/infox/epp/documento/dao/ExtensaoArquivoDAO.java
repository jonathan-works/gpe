package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.ExtensaoArquivoQuery.CLASSIFICACAO_PARAM;
import static br.com.infox.epp.documento.query.ExtensaoArquivoQuery.EXTENSAO_PARAM;
import static br.com.infox.epp.documento.query.ExtensaoArquivoQuery.LIMITE_EXTENSAO;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;

@Stateless
@AutoCreate
@Name(ExtensaoArquivoDAO.NAME)
public class ExtensaoArquivoDAO extends DAO<ExtensaoArquivo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "extensaoArquivoDAO";
    
    public ExtensaoArquivo getTamanhoMaximo(ClassificacaoDocumento classificacao, String extensaoArquivo) {
        Map<String, Object> params = new HashMap<>();
        params.put(CLASSIFICACAO_PARAM, classificacao);
        params.put(EXTENSAO_PARAM, extensaoArquivo);
        return getNamedSingleResult(LIMITE_EXTENSAO, params);
    }
    
}
