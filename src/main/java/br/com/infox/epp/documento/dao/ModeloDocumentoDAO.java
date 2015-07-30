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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento_;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;

@Name(ModeloDocumentoDAO.NAME)
@AutoCreate
public class ModeloDocumentoDAO extends DAO<ModeloDocumento> {
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
            List<Integer> ids) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_LISTA_IDS, ids);
        return getNamedResultList(MODELO_BY_LISTA_IDS, parameters);
    }

	public List<ModeloDocumento> getModeloDocumentoByTipo(TipoModeloDocumento tipoModeloDocumento) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ModeloDocumento> cq = cb.createQuery(ModeloDocumento.class);
		Root<ModeloDocumento> from = cq.from(ModeloDocumento.class);
		Predicate equalTipoModelo = cb.equal(from.get(ModeloDocumento_.tipoModeloDocumento), tipoModeloDocumento);
		Predicate ativo = cb.equal(from.get(ModeloDocumento_.ativo), true);
		Predicate where = cb.and(equalTipoModelo, ativo);

		cq.select(from);
		cq.where(where);
		cq.orderBy(cb.asc(from.get(ModeloDocumento_.modeloDocumento)));

		return getEntityManager().createQuery(cq).getResultList();
	}
}
