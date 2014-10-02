package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.ASSINATURA_OBRIGATORIA;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.CODIGO_DOCUMENTO_PARAM;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.PAPEL_PARAM;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.CLASSIFICACAO_DOCUMENTO_PARAM;

import java.util.HashMap;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;

@AutoCreate
@Name(ClassificacaoDocumentoDAO.NAME)
public class ClassificacaoDocumentoDAO extends DAO<ClassificacaoDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "classificacaoDocumentoDAO";

    public List<ClassificacaoDocumento> getUseableClassificacaoDocumento(boolean isModelo, Papel papel) {
        final HashMap<String, Object> parameters = new HashMap<>();
        TipoDocumentoEnum tipoDocumento = isModelo ? TipoDocumentoEnum.P : TipoDocumentoEnum.D;
        parameters.put(ClassificacaoDocumentoQuery.TIPO_DOCUMENTO_PARAM, tipoDocumento);
        parameters.put(PAPEL_PARAM, papel);
        return getNamedResultList(ClassificacaoDocumentoQuery.CLASSIFICACAO_DOCUMENTO_USEABLE, parameters);
    }

    public boolean isAssinaturaObrigatoria(ClassificacaoDocumento tipoProcessoDocumento, Papel papel) {
        HashMap<String, Object> params = new HashMap<String, Object>(0);
        params.put(CLASSIFICACAO_DOCUMENTO_PARAM, tipoProcessoDocumento);
        params.put(PAPEL_PARAM, papel);
        ClassificacaoDocumentoPapel tpdp = getNamedSingleResult(ASSINATURA_OBRIGATORIA, params);
        if (tpdp != null) {
            return tpdp.getTipoAssinatura() != TipoAssinaturaEnum.F;
        }
        return false;
    }
    
    public ClassificacaoDocumento findByCodigo(String codigo) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(CODIGO_DOCUMENTO_PARAM, codigo);
        return getNamedSingleResult(FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO, parameters);
    }
}
