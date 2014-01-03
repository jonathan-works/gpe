package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.PapelQuery.*;
import static br.com.infox.core.constants.WarningConstants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.itx.util.EntityUtil;

@Name(PapelDAO.NAME)
@AutoCreate
public class PapelDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "papelDAO";

    public List<Papel> getPapeisNaoAssociadosATipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_TIPO_MODELO_DOCUMENTO, tipoModeloDocumento);
        return getNamedResultList(PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO, parameters);
    }

    public List<Papel> getPapeisNaoAssociadosATipoProcessoDocumento(TipoProcessoDocumento tipoProcessoDocumento) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_TIPO_PROCESSO_DOCUMENTO, tipoProcessoDocumento);
        return getNamedResultList(PAPEIS_NAO_ASSOCIADOS_A_TIPO_PROCESSO_DOCUMENTO, parameters);
    }

    public Papel getPapelByIndentificador(String identificador) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_IDENTIFICADOR, identificador);
        return getNamedSingleResult(PAPEL_BY_IDENTIFICADOR, parameters);
    }

    public List<Papel> getPapeisByListaDeIdentificadores(List<String> identificadores) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_LISTA_IDENTIFICADORES, identificadores);
        return getNamedResultList(PAPEIS_BY_IDENTIFICADORES, parameters);
    }

    public List<Papel> getPapeisDeUsuarioByLocalizacao(Localizacao localizacao) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_LOCALIZACAO, localizacao);
        return getNamedResultList(PAPEIS_BY_LOCALIZACAO, parameters);
    }

}
