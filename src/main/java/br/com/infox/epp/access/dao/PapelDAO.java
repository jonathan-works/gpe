package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.PapelQuery.ID_PAPEL_PARAM;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_BY_IDENTIFICADORES;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_BY_LOCALIZACAO;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_NAO_ASSOCIADOS_A_TIPO_PROCESSO_DOCUMENTO;
import static br.com.infox.epp.access.query.PapelQuery.PAPEL_BY_IDENTIFICADOR;
import static br.com.infox.epp.access.query.PapelQuery.PARAM_IDENTIFICADOR;
import static br.com.infox.epp.access.query.PapelQuery.PARAM_LISTA_IDENTIFICADORES;
import static br.com.infox.epp.access.query.PapelQuery.PARAM_LOCALIZACAO;
import static br.com.infox.epp.access.query.PapelQuery.PARAM_TIPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.access.query.PapelQuery.PARAM_TIPO_PROCESSO_DOCUMENTO;
import static br.com.infox.epp.access.query.PapelQuery.PERMISSOES_BY_PAPEL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;

@Name(PapelDAO.NAME)
@AutoCreate
public class PapelDAO extends DAO<Papel> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "papelDAO";

    public List<Papel> getPapeisNaoAssociadosATipoModeloDocumento(
            TipoModeloDocumento tipoModeloDocumento) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_TIPO_MODELO_DOCUMENTO, tipoModeloDocumento);
        return getNamedResultList(PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO, parameters);
    }

    public List<Papel> getPapeisNaoAssociadosATipoProcessoDocumento(
            ClassificacaoDocumento tipoProcessoDocumento) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_TIPO_PROCESSO_DOCUMENTO, tipoProcessoDocumento);
        return getNamedResultList(PAPEIS_NAO_ASSOCIADOS_A_TIPO_PROCESSO_DOCUMENTO, parameters);
    }

    public Papel getPapelByIndentificador(String identificador) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_IDENTIFICADOR, identificador);
        return getNamedSingleResult(PAPEL_BY_IDENTIFICADOR, parameters);
    }

    public List<Papel> getPapeisByListaDeIdentificadores(
            List<String> identificadores) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_LISTA_IDENTIFICADORES, identificadores);
        return getNamedResultList(PAPEIS_BY_IDENTIFICADORES, parameters);
    }

    public List<Papel> getPapeisDeUsuarioByLocalizacao(Localizacao localizacao) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_LOCALIZACAO, localizacao);
        return getNamedResultList(PAPEIS_BY_LOCALIZACAO, parameters);
    }

    public List<String> getListaDeNomesDosPapeis() {
        return getResultList("select distinct identificador from Papel", null);
    }

    public List<String> getListaPermissoes(Papel papel) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_PAPEL_PARAM, papel.getIdPapel());
        return getNamedResultList(PERMISSOES_BY_PAPEL, parameters);
    }

}
