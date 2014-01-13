package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.ASSINATURA_OBRIGATORIA;
import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.LIST_TIPO_PROCESSO_DOCUMENTO;
import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.PAPEL_PARAM;
import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_INTERNO_ANEXO;
import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_INTERNO_TEXTO;
import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_PARAM;

import java.util.HashMap;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;

@Name(TipoProcessoDocumentoDAO.NAME)
@AutoCreate
public class TipoProcessoDocumentoDAO extends GenericDAO {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoProcessoDocumentoDAO";
	
    public List<TipoProcessoDocumento> getTipoProcessoDocumentoInterno(boolean isModelo) {
        if (isModelo) {
            return getNamedResultList(TIPO_PROCESSO_DOCUMENTO_INTERNO_TEXTO);
        } else {
            return getNamedResultList(TIPO_PROCESSO_DOCUMENTO_INTERNO_ANEXO);
        }
    }

    // Retorna um TipoProcessoDocumento ~aleatório
    public TipoProcessoDocumento getTipoProcessoDocumentoFluxo() {
        return getNamedSingleResult(LIST_TIPO_PROCESSO_DOCUMENTO);
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
