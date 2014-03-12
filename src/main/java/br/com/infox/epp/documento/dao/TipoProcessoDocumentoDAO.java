package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.ASSINATURA_OBRIGATORIA;
import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.PAPEL_PARAM;
import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_PARAM;

import java.util.HashMap;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;

@Name(TipoProcessoDocumentoDAO.NAME)
@AutoCreate
public class TipoProcessoDocumentoDAO extends DAO<TipoProcessoDocumento> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoProcessoDocumentoDAO";
	
    public List<TipoProcessoDocumento> getUseableTipoProcessoDocumento(boolean isModelo, Papel papel) {
        final HashMap<String, Object> parameters = new HashMap<>();
        TipoDocumentoEnum tipoDocumento;
        if (isModelo) {
            tipoDocumento = TipoDocumentoEnum.P;
        } else {
            tipoDocumento = TipoDocumentoEnum.D;
        }
        parameters.put(TipoProcessoDocumentoQuery.TIPO_DOCUMENTO_PARAM, tipoDocumento);
        parameters.put(PAPEL_PARAM, papel);
        return getNamedResultList(TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_USEABLE, parameters);
    }

    public boolean isAssinaturaObrigatoria(TipoProcessoDocumento tipoProcessoDocumento, Papel papel) {
        HashMap<String, Object> params = new HashMap<String, Object>(0);
        params.put(TIPO_PROCESSO_DOCUMENTO_PARAM, tipoProcessoDocumento);
        params.put(PAPEL_PARAM, papel);
        Boolean result = getNamedSingleResult(ASSINATURA_OBRIGATORIA, params);
        if (result != null) {
            return result;
        }
        return false;
    }

}
