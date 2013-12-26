package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.LIST_ATIVOS;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.MODELO_BY_GRUPO_AND_TIPO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.MODELO_BY_LISTA_IDS;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.MODELO_BY_TITULO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.PARAM_GRUPO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.PARAM_LISTA_IDS;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.PARAM_TIPO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.PARAM_TITULO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;

@Name(ModeloDocumentoDAO.NAME)
@AutoCreate
public class ModeloDocumentoDAO extends GenericDAO {
    private static final long serialVersionUID = -39703831180567768L;
    public static final String NAME = "modeloDocumentoDAO";

    public List<ModeloDocumento> getModeloDocumentoList() {
        return getNamedResultList(LIST_ATIVOS);
    }

    public ModeloDocumento getModeloDocumentoByTitulo(String titulo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_TITULO, titulo);
        return getNamedSingleResult(MODELO_BY_TITULO, parameters);
    }

    public List<ModeloDocumento> getModeloDocumentoByGrupoAndTipo(
            GrupoModeloDocumento grupo, TipoModeloDocumento tipo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_GRUPO, grupo);
        parameters.put(PARAM_TIPO, tipo);
        return getNamedResultList(MODELO_BY_GRUPO_AND_TIPO, parameters);
    }

    public List<ModeloDocumento> getModelosDocumentoInListaModelos(
            String listaModelos) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_LISTA_IDS, listaModelos);
        return getNamedResultList(MODELO_BY_LISTA_IDS, parameters);
    }

}
