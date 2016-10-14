package br.com.infox.epp.unidadedecisora.dao;

import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.CODIGO_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.FIND_UDM_BY_CODIGO_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.ID_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.ID_UNIDADE_DEC_COLEGIADA;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.ID_USUARIO_LOGIN;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_BY_UNIDADE_DECISORA_COLEGIADA;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_EXISTE_UDM_BY_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_UDM_BY_USUARIO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica_;

@AutoCreate
@Stateless
@Name(UnidadeDecisoraMonocraticaDAO.NAME)
public class UnidadeDecisoraMonocraticaDAO extends DAO<UnidadeDecisoraMonocratica>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraMonocraticaDAO";
	
	public List<UnidadeDecisoraMonocratica> searchUnidadeDecisoraMonocraticaAtivoWithIdColegiada(Integer idColegiada){
		Map<String, Object> map = new HashMap<String, Object>(1);
		map.put(ID_UNIDADE_DEC_COLEGIADA, idColegiada);
		return getNamedResultList(SEARCH_BY_UNIDADE_DECISORA_COLEGIADA, map);
	}
	
	public List<Map<String, String>> searchUnidadeDecisoraMonocraticaWithIdUsuario(Integer idUsuario){
		Map<String, Object> map = new HashMap<>(1);
		map.put(ID_USUARIO_LOGIN, idUsuario);
		return getNamedResultList(SEARCH_UDM_BY_USUARIO, map);
	}
	
	public UnidadeDecisoraMonocratica existeUnidadeDecisoraComLocalizacao(Integer idLocalizacao){
        Map<String, Object> map = new HashMap<>(1);
        map.put(ID_LOCALIZACAO, idLocalizacao);
        return getNamedSingleResult(SEARCH_EXISTE_UDM_BY_LOCALIZACAO, map);
    }
	
	public UnidadeDecisoraMonocratica findByCodigoLocalizacao(String codigoLocalizacao) {
		Map<String, Object> params = new HashMap<>();
		params.put(CODIGO_LOCALIZACAO, codigoLocalizacao);
		return getNamedSingleResult(FIND_UDM_BY_CODIGO_LOCALIZACAO, params);
	}
	
	public List<PessoaFisica> getRelatores() {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PessoaFisica> query = cb.createQuery(PessoaFisica.class);
		Root<UnidadeDecisoraMonocratica> udm = query.from(UnidadeDecisoraMonocratica.class);
		query.where(cb.isTrue(udm.get(UnidadeDecisoraMonocratica_.ativo)), cb.isNotNull(udm.get(UnidadeDecisoraMonocratica_.chefeGabinete)));
		query.select(udm.get(UnidadeDecisoraMonocratica_.chefeGabinete));
		query.orderBy(cb.asc(udm.get(UnidadeDecisoraMonocratica_.chefeGabinete).get(PessoaFisica_.nome)));
		return entityManager.createQuery(query).getResultList();
	}

	public UnidadeDecisoraMonocratica findByRelator(PessoaFisica relator) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<UnidadeDecisoraMonocratica> query = cb.createQuery(UnidadeDecisoraMonocratica.class);
		Root<UnidadeDecisoraMonocratica> udm = query.from(UnidadeDecisoraMonocratica.class);
		query.where(
				cb.equal(udm.get(UnidadeDecisoraMonocratica_.chefeGabinete), relator),
				cb.isTrue(udm.get(UnidadeDecisoraMonocratica_.ativo))
		);
		query.select(udm);
		query.orderBy(cb.asc(udm.get(UnidadeDecisoraMonocratica_.chefeGabinete).get(PessoaFisica_.nome)));
		return getSingleResult(entityManager.createQuery(query));
	}
	
	public List<UnidadeDecisoraMonocratica> searchUnidadeDecisoraMonocraticaAtivo() {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    CriteriaQuery<UnidadeDecisoraMonocratica> cq = cb.createQuery(UnidadeDecisoraMonocratica.class);
	    Root<UnidadeDecisoraMonocratica> udm = cq.from(UnidadeDecisoraMonocratica.class);
	    cq.select(udm);
	    cq.where(cb.isTrue(udm.get(UnidadeDecisoraMonocratica_.ativo)));
	    return getEntityManager().createQuery(cq).getResultList();
	}
	
	@Override
	public List<UnidadeDecisoraMonocratica> findAll() {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UnidadeDecisoraMonocratica> cq = cb.createQuery(UnidadeDecisoraMonocratica.class);
        Root<UnidadeDecisoraMonocratica> root = cq.from(UnidadeDecisoraMonocratica.class);
        cq.orderBy(cb.asc(root.get(UnidadeDecisoraMonocratica_.nome)));
        return getEntityManager().createQuery(cq).getResultList();
	}
}
